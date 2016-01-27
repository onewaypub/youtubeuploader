package org.gneisenau.youtube.processor.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.gneisenau.youtube.events.StatusUpdateEvent;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.UploadException;
import org.gneisenau.youtube.handler.youtube.ProgressAwareInputStream;
import org.gneisenau.youtube.handler.youtube.VideoHandler;
import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(value=1)
public class VideoUploadTask extends AbstractYoutubeTask {

	@Autowired
	protected VideoHandler vidUploader;

	@Autowired
	public VideoUploadTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	@Transactional(propagation=Propagation.MANDATORY)
	public int process(Video v) {
		String mail = userSettingsDAO.findByUserName(v.getUsername()).getMailTo();
		if (mail != null && mail.trim().length() == 0) {
			return VideoTask.CONTINUE;
		}
		File video = new File(v.getVideo());
		v.setState(State.OnUpload);
		try {
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
				String id = vidUploader.upload(PrivacySetting.Private, v.getTitle(), inputStream, v.getUsername());
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
			return VideoTask.STOP;
		} catch (AuthorizeException e) {
			handleError(v, "Authorisierung bei Youttube fehlgeschlagen", e);
			return VideoTask.STOP;
		} catch (UploadException e) {
			handleError(v, "Das Video konnte nicht hochgeladen werden", e);
			return VideoTask.STOP;
		}
		return VideoTask.CONTINUE;
	}

}
