package org.gneisenau.youtube.processor;

import java.util.List;

import org.gneisenau.youtube.events.StatusUpdateEvent;
import org.gneisenau.youtube.message.MailSendService;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public abstract class AbstractProcessor {


	@Autowired
	private VideoRepository videoDAO;
	@Autowired
	protected MailSendService mailService;

	private final ApplicationEventPublisher publisher;

	@Autowired
	public AbstractProcessor(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	public void publishEvent(StatusUpdateEvent event) {
		this.publisher.publishEvent(event);
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW, isolation=Isolation.READ_COMMITTED, timeout=10800)
	public void execute(){
		List<Video> videos = getProcessingVideoList();
		for (Video videoTemp : videos) {
			execute(videoTemp);
		}
	}
	
	private void execute(Video videoTemp){
		Video v = videoDAO.findById(videoTemp.getId());

		v.setState(initialProcessState());
		videoDAO.persist(v);
		videoDAO.flush();

		StatusUpdateEvent event = new StatusUpdateEvent(v.getId(), v.getState(), 0, v);
		publishEvent(event);

		runChain(v);

		v.setState(v.getState().nextState());
		videoDAO.persist(v);
		videoDAO.flush();

		event = new StatusUpdateEvent(v.getId(), v.getState(), 0, v);
		publishEvent(event);

		notifyProcessing(v);
	}

	protected abstract void runChain(Video v);
	protected abstract void notifyProcessing(Video v);
	protected abstract State initialProcessState();
	protected abstract List<Video> getProcessingVideoList();

}
