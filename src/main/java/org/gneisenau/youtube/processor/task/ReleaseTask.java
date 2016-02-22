package org.gneisenau.youtube.processor.task;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.NotFoundException;
import org.gneisenau.youtube.handler.video.exceptions.UpdateException;
import org.gneisenau.youtube.handler.youtube.VideoHandler;
import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(value = 2)
public class ReleaseTask extends AbstractProcessorTask implements PublishTask {

	@Autowired
	protected VideoHandler vidUploader;

	@Autowired
	public ReleaseTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public ChainAction process(Video v) throws TaskException {
		Validate.notNull(v, "Video is not given");
		Validate.notEmpty(v.getUsername(), "username not given");

		if (StringUtils.isBlank(v.getYoutubeId()) || v.getReleaseDate() == null) {
			return ChainAction.STOP;
		}
		try {
			vidUploader.release(v.getYoutubeId(), PrivacySetting.Public, v.getUsername());
		} catch (NotFoundException e) {
			throw new TaskException(v, "Kann Video nicht der Playlist hinzuf\u00fcgen", e);
		} catch (AuthorizeException e) {
			throw new TaskException(v, "Kann Video nicht der Playlist hinzuf\u00fcgen", e);
		} catch (UpdateException e) {
			throw new TaskException(v, "Kann Video nicht der Playlist hinzuf\u00fcgen", e);
		}
		return ChainAction.CONTINUE;
	}

}
