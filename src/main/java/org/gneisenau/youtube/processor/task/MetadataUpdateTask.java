package org.gneisenau.youtube.processor.task;

import org.apache.commons.lang3.StringUtils;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.NotFoundException;
import org.gneisenau.youtube.handler.video.exceptions.UpdateException;
import org.gneisenau.youtube.handler.youtube.VideoHandler;
import org.gneisenau.youtube.handler.youtube.YouTubeUtils;
import org.gneisenau.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(value = 3)
public class MetadataUpdateTask extends AbstractProcessorTask implements YoutubeTask {

	@Autowired
	protected VideoHandler vidUploader;
	@Autowired
	private YouTubeUtils utils;

	@Autowired
	public MetadataUpdateTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public int process(Video v) {
		if (StringUtils.isBlank(v.getYoutubeId())) {
			return CONTINUE;
		}
		try {
			vidUploader.updateMetadata(v.getPrivacySetting(), v.getYoutubeId(), utils.getTagsList(v), v.getTitle(),
					utils.createDescription(v), v.getChannelId(), v.getCategoryId(), v.getUsername(), false);
		} catch (AuthorizeException e) {
			handleError(v, "Authorisierung bei Youtube fehlgeschlagen", e);
			return VideoTask.STOP;
		} catch (UpdateException e) {
			handleError(v, "Authorisierung bei Youtube fehlgeschlagen", e);
			return VideoTask.STOP;
		} catch (NotFoundException e) {
			handleError(v, "Video konnte nicht mehr gefunden werden", e);
			return VideoTask.STOP;
		}
		return VideoTask.CONTINUE;
	}
}
