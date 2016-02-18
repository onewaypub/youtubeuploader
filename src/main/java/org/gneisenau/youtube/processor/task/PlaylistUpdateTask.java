package org.gneisenau.youtube.processor.task;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.youtube.VideoHandler;
import org.gneisenau.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(value = 1)
public class PlaylistUpdateTask extends AbstractProcessorTask implements PublishTask {

	@Autowired
	protected VideoHandler vidUploader;

	@Autowired
	public PlaylistUpdateTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public int process(Video v) throws TaskException {
		Validate.notNull(v, "Video is not given");
		Validate.notEmpty(v.getUsername(), "username not given");
		
		if (StringUtils.isBlank(v.getYoutubeId()) || StringUtils.isBlank(v.getPlaylistId())) {
			return CONTINUE;
		}
		try {
			vidUploader.insertPlaylistItem(v.getPlaylistId(), v.getYoutubeId(), v.getUsername());
		} catch (IOException e) {
			throw new TaskException(v, "Kann Video nicht der Playlist hinzuf\u00fcgen", e);
		} catch (AuthorizeException e) {
			throw new TaskException(v, "Kann Video nicht der Playlist hinzuf\u00fcgen; Autorisierung fehlgeschlagen", e);
		}
		return VideoTask.CONTINUE;
	}

}
