package org.gneisenau.youtube.processor;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.gneisenau.youtube.message.MailSendService;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.UserSettingsRepository;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.gneisenau.youtube.processor.task.VideoTask;
import org.gneisenau.youtube.processor.task.YoutubeTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

@Component
public class YoutubeProcessor extends AbstractProcessor {

	@Autowired
	private List<YoutubeTask> youtubeProcessingChain;
	@Autowired
	private VideoRepository videoDAO;
	@Autowired
	private UserSettingsRepository userSettingsDAO;
	@Autowired
	protected MailSendService mailService;

	@Autowired
	public YoutubeProcessor(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@PostConstruct
	public void init() {
		Collections.sort(youtubeProcessingChain, AnnotationAwareOrderComparator.INSTANCE);
	}

	@Override
	protected void runChain(Video v) {
		for (YoutubeTask chainItem : youtubeProcessingChain) {
			int process = chainItem.process(v);
			videoDAO.persist(v);
			videoDAO.flush();
			if (VideoTask.STOP == process) {
				break;
			}
		}
	}

	@Override
	protected void notifyProcessing(Video v) {
		if (userSettingsDAO.findByUserName(v.getUsername()).isNotifyUploadState()) {
			mailService.sendStatusMail(v.getTitle(), v.getState(), v.getUsername());
		}
	}

	@Override
	protected State initialProcessState() {
		return State.OnUpload;
	}
}
