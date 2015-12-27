package org.gneisenau.youtube.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.gneisenau.youtube.events.StatusUpdateEvent;
import org.gneisenau.youtube.exceptions.AuthorizeException;
import org.gneisenau.youtube.exceptions.PreUploadException;
import org.gneisenau.youtube.exceptions.UploadException;
import org.gneisenau.youtube.handler.VideoHandler;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.UploadState;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.utils.ProgressAwareInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
class VideoUploadProcessor extends AbstractVideoProcessor {

	@Autowired
	protected VideoHandler vidUploader;

	@Autowired
	public VideoUploadProcessor(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	public int process(Video v) {
		String mail = userSettingsDAO.findByUserName(v.getUsername()).getMailTo();
		if (mail != null && mail.trim().length() == 0) {
			return VideoProcessor.CONTINUE;
		}
		File video = new File(v.getVideo());
		v.setState(State.OnUpload);
		try {
			v.setVideoUploadState(UploadState.MEDIA_IN_PROGRESS);
			ProgressAwareInputStream inputStream = new ProgressAwareInputStream(new FileInputStream(video),
					video.length(), video);
			inputStream.setOnProgressListener(new ProgressAwareInputStream.OnProgressListener() {
				@Override
				public void onProgress(int percentage, Object tag) {
					StatusUpdateEvent event = new StatusUpdateEvent(v.getId(), State.OnUpload, percentage, this);
					publishEvent(event);
				}
			});
			try {
				String id = vidUploader.upload(v.getId(), v.getPrivacySetting(), inputStream, v.getTags(), v.getTitle(),
						createDescription(v), v.getChannelId(), v.getCategoryId(), v.getPlaylistId(), v.getUsername(),
						v.isAgeRestricted());
				v.setYoutubeId(id);
				v.setVideoUrl("https://www.youtube.com/watch?v=" + id);
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("Cannot close progress aware inputstream for video upload", e);
				}
			}
		} catch (FileNotFoundException e) {
			handleError(v, "Video konnte auf der Festplatt nicht mehr gefunden werden", null);
			return VideoProcessor.STOP;
		} catch (AuthorizeException e) {
			handleError(v, "Authorisierung bei Youttube fehlgeschlagen", e);
			return VideoProcessor.STOP;
		} catch (PreUploadException e) {
			handleError(v, "Vorbereitung des Videos für Youtube fehlgeschlagen", e);
			return VideoProcessor.STOP;
		} catch (UploadException e) {
			handleError(v, "Das Video konnte nicht hochgeladen werden", e);
			return VideoProcessor.STOP;
		}
		v.setState(State.WaitForListing);
		if (userSettingsDAO.findByUserName(v.getUsername()).isNotifyReleaseState()) {
			mailService.sendStatusMail(v.getTitle(), v.getState(), v.getUsername());
		}
		return VideoProcessor.CONTINUE;
	}

	private String createDescription(Video v) {
		String desc = v.getDescription() + "\n\nTitel: " + v.getShorttitle() + "\nGenre: " + v.getGerne()
				+ "\nEntwickler: " + v.getDeveloper() + "\nPublisher: " + v.getPublisher() + "\nVeröffentlichung: "
				+ v.getPublished() + "\n\nhttps://www.facebook.com/pages/PeachesLp/781275711939550"
				+ "\nhttps://twitter.com/Peaches_LP";
		return desc;
	}

}
