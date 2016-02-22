package org.gneisenau.youtube.processor;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Validate;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.processor.task.ChainAction;
import org.gneisenau.youtube.processor.task.VideoTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

@Component
public class VideoProcessor extends AbstractProcessor {

	@Autowired
	private List<VideoTask> videoProcessingChain;

	@Autowired
	public VideoProcessor(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@PostConstruct
	public void init() {
		Collections.sort(videoProcessingChain, AnnotationAwareOrderComparator.INSTANCE);
	}

	@Override
	protected void runChain(Video v) throws Exception {
		Validate.notEmpty(v.getVideo());
		for (VideoTask chainItem : videoProcessingChain) {
			ChainAction process = chainItem.process(v);
			if (ChainAction.STOP.equals(process)) {
				break;
			}
		}
	}

	@Override
	protected void notifyProcessing(Video v) {
		if (userSettingsDAO.findOrCreateByUserName(v.getUsername()).isNotifyProcessedState()) {
			mailService.sendStatusMail(v.getTitle(), v.getState(), v.getUsername());
		}
	}

	@Override
	protected State initialProcessState() {
		return State.OnProcessing;
	}

	@Override
	protected List<Video> getProcessingVideoList() {
		return videoDAO.findAllWaitForPorcessing();
	}
}
