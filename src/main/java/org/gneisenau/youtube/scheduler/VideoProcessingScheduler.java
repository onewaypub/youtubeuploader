package org.gneisenau.youtube.scheduler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.ExecuteException;
import org.apache.log4j.Logger;
import org.gneisenau.youtube.controller.IOService;
import org.gneisenau.youtube.exceptions.VideoMergeException;
import org.gneisenau.youtube.exceptions.VideoTranscodeException;
import org.gneisenau.youtube.message.MailSendService;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.UserSettingsRepository;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.gneisenau.youtube.video.VideoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@PropertySource("file:${user.home}/youtubeuploader.properties")
public class VideoProcessingScheduler {

	@Autowired
	private UserSettingsRepository userSettingsDAO;
	@Autowired
	IOService ioService;
	@Value("${tomcat.home.dir}")
	private String introOutroDir;


	@Autowired
	private VideoRepository videoDAO;
	@Autowired
	private VideoUtils videoProcessor;
	@Autowired
	private MailSendService mailService;

	private static final Logger logger = Logger.getLogger(VideoProcessingScheduler.class);

	@Scheduled(fixedDelay = 60000) // every hour
	@Transactional
	public void processVideos() {
		File intro = new File(introOutroDir + "/intro.mp4");
		File outro = new File(introOutroDir + "/outro.mp4");
		List<Video> videos = videoDAO.findAllWaitForPorcessing();
		for (Video v : videos) {

			// Merge Videos
			File transcodedFile = null;
			File tempFile = null;
			File oldFile = null;
			try {
				String tempFileStr = ioService.getTemporaryProcessingFolder() + File.separator + System.currentTimeMillis()
						+ ".mp4";
				String newFile = ioService.getTemporaryFolder() + File.separator + System.currentTimeMillis() + ".mp4";
				v.setState(State.OnProcessing);
				videoDAO.persist(v);
				transcodedFile = new File(v.getVideo() + ".mp4");
				oldFile = new File(v.getVideo());
				try {
					videoProcessor.transcode(new File(v.getVideo()), transcodedFile);
				} catch (ExecuteException e) {
					handleError(v, "Fehler beim Ausführen des Transcodings");
					return;
				} catch (IOException e) {
					handleError(v, "Fehler beim Zugriff auf die Videodateien während des Transcodings");
					return;
				}
				List<File> files = new ArrayList<File>();
				if (intro != null) {
					files.add(intro);
				}
				files.add(transcodedFile);
				if (outro != null) {
					files.add(outro);
				}
				try {
					videoProcessor.merge(newFile, files);
				} catch (IOException e) {
					handleError(v, "Fehler beim Zugriff auf die zusammenzuführenden Videodatei während des Merges");
					return;
				} catch (VideoTranscodeException e) {
					handleError(v, "Zusammenzuführenden Videodateien könnten nicht transcodiert werden ");
					return;
				} catch (VideoMergeException e) {
					handleError(v, "Fehler beim Merge der Videodateien");
					return;
				}
				v.setVideo(newFile);
				v.setState(State.WaitForUpload);
				videoDAO.persist(v);
				if (userSettingsDAO.findByUserName(v.getUsername()).isNotifyProcessedState()) {
					mailService.sendStatusMail(v.getTitle(), v.getState(), v.getUsername());
				}
				tempFile = new File(tempFileStr);
				oldFile.delete();
			} finally {
				transcodedFile.delete();
				tempFile.delete();
			}
		}
	}

	private void handleError(Video v, String message) {
		List<String> errors = v.getErrors();
		if (errors == null)
			errors = new ArrayList<String>();
		errors.add(message);
		v.setErrors(errors);
		v.setState(State.Error);
		if (userSettingsDAO.findByUserName(v.getUsername()).isNotifyErrorState()) {
			mailService.sendErrorMail(message, v.getTitle(), v.getState(), v.getUsername());
		}
		videoDAO.persist(v);
	}

}
