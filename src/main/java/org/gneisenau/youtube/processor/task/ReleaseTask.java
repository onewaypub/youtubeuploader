package org.gneisenau.youtube.processor.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

@Component
@Order(value = 2)
public class ReleaseTask extends AbstractProcessorTask  implements PublishTask{

	@Autowired
	protected VideoHandler vidUploader;

	@Autowired
	public ReleaseTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	public int process(Video v) {
		if (StringUtils.isBlank(v.getYoutubeId()) || StringUtils.isBlank(v.getPlaylistId())) {
			return CONTINUE;
		}
		List<String> tags = new ArrayList<String>();
		CollectionUtils.addAll(tags, v.getTags().split(","));
		try {
			vidUploader.release(v.getYoutubeId(), PrivacySetting.Public, v.getUsername());
		} catch (NotFoundException e) {
			handleError(v, "Kann Video nicht der Playlist hinzufügen", e);
			return VideoTask.STOP;
		} catch (AuthorizeException e) {
			handleError(v, "Kann Video nicht der Playlist hinzufügen", e);
			return VideoTask.STOP;
		} catch (UpdateException e) {
			handleError(v, "Kann Video nicht der Playlist hinzufügen", e);
			return VideoTask.STOP;
		}
		if (userSettingsDAO.findByUserName(v.getUsername()).isNotifyReleaseState()) {
			mailService.sendStatusMail(v.getTitle(), v.getState(), v.getUsername());
		}
		return VideoTask.CONTINUE;
	}

}
