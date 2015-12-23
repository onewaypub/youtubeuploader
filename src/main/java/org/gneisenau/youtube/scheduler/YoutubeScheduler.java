package org.gneisenau.youtube.scheduler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.gneisenau.youtube.exceptions.AuthorizeException;
import org.gneisenau.youtube.exceptions.ClientSecrectsException;
import org.gneisenau.youtube.exceptions.PreUploadException;
import org.gneisenau.youtube.exceptions.ReleaseException;
import org.gneisenau.youtube.exceptions.SecretsStoreException;
import org.gneisenau.youtube.exceptions.UploadException;
import org.gneisenau.youtube.handler.Auth;
import org.gneisenau.youtube.handler.ImageHandler;
import org.gneisenau.youtube.handler.VideoHandler;
import org.gneisenau.youtube.message.MailSendService;
import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.UploadState;
import org.gneisenau.youtube.model.UserSettings;
import org.gneisenau.youtube.model.UserSettingsRepository;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class YoutubeScheduler {

	@Autowired
	private UserSettingsRepository userSettingsDAO;
	@Autowired
	private VideoRepository videoDAO;
	@Autowired
	private ImageHandler imgUploader;
	@Autowired
	private VideoHandler vidUploader;
	@Autowired
	private Auth auth;
	@Autowired
	private MailSendService mailService;

	private static final Logger logger = LoggerFactory.getLogger(YoutubeScheduler.class);

	@Scheduled(fixedDelay = 60000) // every hour
	@Transactional
	public void uploadVideos() throws Throwable {
		List<Video> videos = videoDAO.findAllWaitForUpload();
		for (Video v : videos) {
			String mail = userSettingsDAO.findByUserName(v.getUsername()).getMailTo();
			if(mail != null && mail.trim().length() == 0){
				continue;
			}
			File thumb = new File(v.getThumbnail());
			File video = new File(v.getVideo());
			try {
				v.setState(State.OnUpload);
				videoDAO.persist(v);
				try {
					uploadVideo(v, video);
				} catch (FileNotFoundException e) {
					handleError(v, "Video konnte auf der Festplatt nicht mehr gefunden werden", null);
					continue;
				} catch (AuthorizeException e) {
					handleError(v, "Authorisierung bei Youttube fehlgeschlagen", e);
					continue;
				} catch (PreUploadException e) {
					handleError(v, "Vorbereitung des Videos für Youtube fehlgeschlagen", e);
					continue;
				} catch (UploadException e) {
					handleError(v, "Das Video konnte nicht hochgeladen werden", e);
					continue;
				}
				try {
					uploadThumbnail(v, thumb);
				} catch (FileNotFoundException e) {
					handleError(v, "Das Thumbnail konnte auf der Festplatt nicht mehr gefunden werden", null);
					continue;
				} catch (PreUploadException e) {
					handleError(v, "Vorbereitung des Thumbnails für Youtube fehlgeschlagen", e);
					continue;
				} catch (AuthorizeException e) {
					handleError(v, "Authorisierung bei Youtube fehlgeschlagen", e);
					continue;
				} catch (UploadException e) {
					handleError(v, "Das Thumbnails konnte nicht hochgeladen werden", e);
					continue;
				}
				v.setState(State.WaitForListing);
				videoDAO.persist(v);
				if (userSettingsDAO.findByUserName(v.getUsername()).isNotifyReleaseState()) {
					mailService.sendStatusMail(v.getTitle(), v.getState(), v.getUsername());
				}
			} catch (Throwable e) {
				throw e;
			}
		}
	}

	private void handleError(Video v, String message, Exception e) {
		if (e != null) {
			String returnJSON = e.getMessage();
			try {
				ByteArrayInputStream is = new ByteArrayInputStream(returnJSON.getBytes());
				JsonReader rdr = Json.createReader(is);

				JsonObject obj = rdr.readObject();
				String results = obj.getString("message");
				message = message + " - " + results;
			} catch (Exception ex) {
				logger.error("",ex);
			}
		}
		logger.error("", e);
		List<String> errors = v.getErrors();
		if (errors == null)
			errors = new ArrayList<String>();
		errors.add(message);
		v.setErrors(errors);
		v.setState(State.Error);
		videoDAO.persist(v);
		if (userSettingsDAO.findByUserName(v.getUsername()).isNotifyErrorState()) {
			mailService.sendErrorMail(message, v.getTitle(), v.getState(), v.getUsername());
		}
	}

	@Scheduled(fixedDelay = 60000) // every hour
	@Transactional
	public void releaseVideos() {
		List<Video> videos = videoDAO.findAllWaitForListing();
		for (Video v : videos) {
			String mail = userSettingsDAO.findByUserName(v.getUsername()).getMailTo();
			if(mail != null && mail.trim().length() == 0){
				continue;
			}
			String youtubeId = v.getYoutubeId();
			try {
				vidUploader.release(youtubeId, PrivacySetting.Public, v.getUsername());
				v.setState(State.Done);
				v.setPrivacySetting(PrivacySetting.Public);
				mailService.sendStatusMail(v.getTitle(), v.getState(), v.getUsername());
				videoDAO.persist(v);
			} catch ( ReleaseException e) {
				handleError(v, "Fehler beim Freigeben des Videos", e);
			}catch ( AuthorizeException e) {
				handleError(v, "Fehler beim Freigeben des Videos", e);
			}catch (ClientSecrectsException e) {
				handleError(v, "Fehler beim Freigeben des Videos", e);
			}catch (SecretsStoreException e) {
				handleError(v, "Fehler beim Freigeben des Videos", e);
			} catch (IOException e) {
				handleError(v, "Fehler beim Freigeben des Videos", null);
			}
		}
	}

	@Scheduled(fixedDelay = 100000) // every ten minutes
	public void refreshToken() {
		try {
			List<UserSettings> list = userSettingsDAO.findAll();
			for (UserSettings s : list) {
				if (s.getMailTo() != null && s.getMailTo().trim().length() > 0) {
					auth.authorize("YouTubeUpload", s.getUsername());
				}
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void uploadThumbnail(Video v, File thumb)
			throws FileNotFoundException, PreUploadException, AuthorizeException, UploadException {
		v.setThumbnailUploadState(UploadState.MEDIA_IN_PROGRESS);
		videoDAO.persist(v);
		imgUploader.upload(v.getId(), v.getYoutubeId(), new FileInputStream(thumb), v.getUsername(), thumb.length());
		v.setThumbnailUploadState(UploadState.MEDIA_COMPLETE);
		videoDAO.persist(v);
	}

	private void uploadVideo(Video v, File video)
			throws FileNotFoundException, AuthorizeException, PreUploadException, UploadException {
		v.setVideoUploadState(UploadState.MEDIA_IN_PROGRESS);
		videoDAO.persist(v);
		String id = vidUploader.upload(v.getId(), v.getPrivacySetting(), new FileInputStream(video), v.getTags(),
				v.getTitle(), createDescription(v), v.getChannelId(), v.getCategoryId(), v.getPlaylistId(),
				v.getUsername(), v.isAgeRestricted());
		v.setYoutubeId(id);
		v.setVideoUrl("https://www.youtube.com/watch?v=" + id);
		v.setVideoUploadState(UploadState.MEDIA_COMPLETE);
		videoDAO.persist(v);
	}

	private String createDescription(Video v) {
		String desc = v.getDescription() + "\n\nTitel: " + v.getShorttitle() + "\nGenre: " + v.getGerne()
				+ "\nEntwickler: " + v.getDeveloper() + "\nPublisher: " + v.getPublisher() + "\nVeröffentlichung: "
				+ v.getPublished() + "\n\nhttps://www.facebook.com/pages/PeachesLp/781275711939550"
				+ "\nhttps://twitter.com/Peaches_LP";
		return desc;
	}
}
