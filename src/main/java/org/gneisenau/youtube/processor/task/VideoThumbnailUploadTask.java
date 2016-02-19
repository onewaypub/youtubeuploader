package org.gneisenau.youtube.processor.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.gneisenau.youtube.events.StatusUpdateEvent;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.UploadException;
import org.gneisenau.youtube.handler.youtube.ImageHandler;
import org.gneisenau.youtube.handler.youtube.ProgressAwareInputStream;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(value = 2)
public class VideoThumbnailUploadTask extends AbstractProcessorTask implements YoutubeTask {

	@Autowired
	private ImageHandler imgUploader;

	@Autowired
	public VideoThumbnailUploadTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public ChainAction process(Video v) throws TaskException {
		if (StringUtils.isBlank(v.getThumbnail()) || StringUtils.isBlank(v.getYoutubeId())) {
			return ChainAction.CONTINUE;
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
			try {
				String imgUrl = imgUploader.upload(v.getYoutubeId(), inputStream, v.getUsername(), thumb.length());
				v.setThumbnailUrl(imgUrl);
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("Cannot close progress aware inputstream for thumbnail upload", e);
				}
			}
		} catch (FileNotFoundException e) {
			throw new TaskException(v, "Das Thumbnail konnte auf der Festplatt nicht mehr gefunden werden", null);
		} catch (AuthorizeException e) {
			throw new TaskException(v, "Authorisierung bei Youtube fehlgeschlagen", e);
		} catch (UploadException e) {
			throw new TaskException(v, "Das Thumbnails konnte nicht hochgeladen werden", e);
		}
		return ChainAction.CONTINUE;
	}

}
