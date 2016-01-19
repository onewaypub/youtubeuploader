package org.gneisenau.youtube.processor.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.gneisenau.youtube.events.StatusUpdateEvent;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.PreUploadException;
import org.gneisenau.youtube.handler.video.exceptions.UploadException;
import org.gneisenau.youtube.handler.youtube.ImageHandler;
import org.gneisenau.youtube.handler.youtube.ProgressAwareInputStream;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class VideoThumbnailUploadTask extends AbstractYoutubeTask {

	@Autowired
	private ImageHandler imgUploader;

	@Autowired
	public VideoThumbnailUploadTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	public int process(Video v) {
		String mail = userSettingsDAO.findByUserName(v.getUsername()).getMailTo();
		if (mail != null && mail.trim().length() == 0) {
			return VideoTask.STOP;
		}
		File thumb = new File(v.getThumbnail());
			v.setState(State.OnUpload);
			try {
				ProgressAwareInputStream inputStream = new ProgressAwareInputStream(new FileInputStream(thumb),
						thumb.length(), thumb);
				inputStream.setOnProgressListener(new ProgressAwareInputStream.OnProgressListener() {
					@Override
					public void onProgress(int percentage, Object tag) {
						StatusUpdateEvent event = new StatusUpdateEvent(v.getId(), State.OnUpload, percentage, this);
						publishEvent(event);
					}
				});
				try{
					imgUploader.upload(v.getId(), v.getYoutubeId(), inputStream, v.getUsername(), thumb.length());
				} finally {
					try {
						inputStream.close();
					} catch (IOException e) {
						logger.error("Cannot close progress aware inputstream for thumbnail upload", e);
					}
				}
			} catch (FileNotFoundException e) {
				handleError(v, "Das Thumbnail konnte auf der Festplatt nicht mehr gefunden werden", null);
				return VideoTask.STOP;
			} catch (PreUploadException e) {
				handleError(v, "Vorbereitung des Thumbnails für Youtube fehlgeschlagen", e);
				return VideoTask.STOP;
			} catch (AuthorizeException e) {
				handleError(v, "Authorisierung bei Youtube fehlgeschlagen", e);
				return VideoTask.STOP;
			} catch (UploadException e) {
				handleError(v, "Das Thumbnails konnte nicht hochgeladen werden", e);
				return VideoTask.STOP;
			}
			//v.setState(State.OnUpload.nextState());
			if (userSettingsDAO.findByUserName(v.getUsername()).isNotifyReleaseState()) {
				mailService.sendStatusMail(v.getTitle(), v.getState(), v.getUsername());
			}
			return VideoTask.CONTINUE;
	}

}
