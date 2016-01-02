package org.gneisenau.youtube.processor;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.gneisenau.youtube.events.StatusUpdateEvent;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class VideoChain {
	@Autowired
	private List<AbstractVideoProcessor> videoProcessingChain;
	@Autowired
	private VideoRepository videoDAO;

	private final ApplicationEventPublisher publisher;

	@Autowired
	public VideoChain(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	@PostConstruct
	public void init() {
		Collections.sort(videoProcessingChain, AnnotationAwareOrderComparator.INSTANCE);
	}

	public void publishEvent(StatusUpdateEvent event){
		this.publisher.publishEvent(event);
	}

	@Transactional
	public void execute(List<Video> videos) {
		int i = Math.round(100 / videoProcessingChain.size());
		
		for (Video videoTemp : videos) {
			Video v = videoDAO.findById(videoTemp.getId());
			StatusUpdateEvent event = new StatusUpdateEvent(v.getId(), State.OnProcessing, 10, v);
			publishEvent(event);			
			for (VideoProcessor chainItem : videoProcessingChain) {
				int process = chainItem.process(v);
				
				videoDAO.persist(v);
				videoDAO.flush();
				if(VideoProcessor.STOP == process){
					break;
				} else {
					event = new StatusUpdateEvent(v.getId(), v.getState(), i, v);
					publishEvent(event);
				}
				i = i + Math.round(100 / videoProcessingChain.size());
			}
			v.setState(State.WaitForUpload);
			videoDAO.persist(v);
			videoDAO.flush();
			event = new StatusUpdateEvent(v.getId(), v.getState(), 0, v);
			publishEvent(event);
		}
	}
}
