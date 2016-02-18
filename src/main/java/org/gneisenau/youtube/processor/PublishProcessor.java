package org.gneisenau.youtube.processor;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.gneisenau.youtube.message.MailSendService;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.UserSettingsRepository;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.gneisenau.youtube.processor.task.PublishTask;
import org.gneisenau.youtube.processor.task.YoutubeTask;
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
	private VideoRepository videoDAO;
	@Autowired
	private UserSettingsRepository userSettingsDAO;
	@Autowired
	protected MailSendService mailService;

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
	protected void runChain(Video v) {
		for (PublishTask chainItem : releaseProcessingChain) {
			int process = PublishTask.STOP;
			try {
				process = chainItem.process(v);
			} catch (Exception e) {
				v.setState(State.Error);
				process = PublishTask.STOP;
			} finally {
				videoDAO.persist(v);
				videoDAO.flush();
			}
			if (PublishTask.STOP == process) {
				break;
			}
		}
	}

	@Override
	protected void notifyProcessing(Video v) {
		if (userSettingsDAO.findByUserName(v.getUsername()).isNotifyReleaseState()) {
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
