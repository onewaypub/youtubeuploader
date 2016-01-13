package org.gneisenau.youtube.chain;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.gneisenau.youtube.events.StatusUpdateEvent;
import org.gneisenau.youtube.message.MailSendService;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.UserSettingsRepository;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class YoutubeChain {
	@Autowired
	private List<YoutubeProcessor> youtubeProcessingChain;
	@Autowired
	private VideoRepository videoDAO;
	@Autowired
	private UserSettingsRepository userSettingsDAO;
	@Autowired
	protected MailSendService mailService;

	private final ApplicationEventPublisher publisher;

	@Autowired
	public YoutubeChain(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	@PostConstruct
	public void init() {
		Collections.sort(youtubeProcessingChain, AnnotationAwareOrderComparator.INSTANCE);
	}

	public void publishEvent(StatusUpdateEvent event){
		this.publisher.publishEvent(event);
	}

	@Transactional
	public void execute(List<Video> videos) {
		for (Video videoTemp : videos) {
			Video v = videoDAO.findById(videoTemp.getId());
			StatusUpdateEvent event = new StatusUpdateEvent(v.getId(), State.OnUpload, 0, v);
			publishEvent(event);			
			for (YoutubeProcessor chainItem : youtubeProcessingChain) {
				int process = chainItem.process(v);
				
				videoDAO.persist(v);
				videoDAO.flush();
				if(VideoProcessor.STOP == process){
					break;
				}
			}
			v.setState(State.OnUpload.nextState());
			videoDAO.persist(v);
			videoDAO.flush();
			event = new StatusUpdateEvent(v.getId(), v.getState(), 0, v);
			publishEvent(event);
			if (userSettingsDAO.findByUserName(v.getUsername()).isNotifyUploadState()) {
				mailService.sendStatusMail(v.getTitle(), v.getState(), v.getUsername());
			}
		}
	}
}
