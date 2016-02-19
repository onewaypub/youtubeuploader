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
@Order(value = 1)
public class VideoUploadTask extends AbstractProcessorTask implements YoutubeTask {

	@Autowired
	protected VideoHandler vidUploader;

	@Autowired
	public VideoUploadTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public ChainAction process(Video v) throws TaskException {
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
				boolean delete = new File(v.getVideo()).delete();
				logger.error("Video deleted after upload: " + v.getVideo() + " State:" + delete);
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
			throw new TaskException(v, "Video konnte auf der Festplatt nicht mehr gefunden werden", null);
		} catch (AuthorizeException e) {
			throw new TaskException(v, "Authorisierung bei Youttube fehlgeschlagen", e);
		} catch (UploadException e) {
			throw new TaskException(v, "Das Video konnte nicht hochgeladen werden", e);
		}
		return ChainAction.CONTINUE;
	}

}
