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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

@Component
public class VideoProcessor extends AbstractProcessor{

	@Autowired
	private List<VideoTask> videoProcessingChain;
	@Autowired
	private VideoRepository videoDAO;
	@Autowired
	private UserSettingsRepository userSettingsDAO;
	@Autowired
	protected MailSendService mailService;

	@Autowired
	public VideoProcessor(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@PostConstruct
	public void init() {
		Collections.sort(videoProcessingChain, AnnotationAwareOrderComparator.INSTANCE);
	}

	@Override
	protected void runChain(Video v) {
		for (VideoTask chainItem : videoProcessingChain) {
			int process = chainItem.process(v);		
			videoDAO.persist(v);
			videoDAO.flush();
			if(VideoTask.STOP == process){
				break;
			} 
		}
	}

	@Override
	protected void notifyProcessing(Video v) {
		if (userSettingsDAO.findByUserName(v.getUsername()).isNotifyProcessedState()) {
			mailService.sendStatusMail(v.getTitle(), v.getState(), v.getUsername());
		}
	}

	@Override
	protected State initialProcessState() {
		return State.OnProcessing;
	}
}
