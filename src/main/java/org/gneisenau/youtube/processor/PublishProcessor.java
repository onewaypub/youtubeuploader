package org.gneisenau.youtube.processor;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Validate;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.processor.task.ChainAction;
import org.gneisenau.youtube.processor.task.PublishTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PublishProcessor extends AbstractProcessor {

	@Autowired
	private List<PublishTask> releaseProcessingChain;

	@Autowired
	public PublishProcessor(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@PostConstruct
	public void init() {
		Collections.sort(releaseProcessingChain, AnnotationAwareOrderComparator.INSTANCE);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	protected void runChain(Video v) throws Exception {
		Validate.notEmpty(v.getYoutubeId());
		for (PublishTask chainItem : releaseProcessingChain) {
			ChainAction process = chainItem.process(v);
			if (ChainAction.STOP.equals(process)) {
				break;
			}
		}
	}

	@Override
	protected void notifyProcessing(Video v) {
		if (userSettingsDAO.findOrCreateByUserName(v.getUsername()).isNotifyReleaseState()) {
			mailService.sendStatusMail(v.getTitle(), v.getState(), v.getUsername());
		}
	}

	@Override
	protected State initialProcessState() {
		return State.OnUpload;
	}

	@Override
	protected List<Video> getProcessingVideoList() {
		return videoDAO.findAllWaitForListing();
	}
}
