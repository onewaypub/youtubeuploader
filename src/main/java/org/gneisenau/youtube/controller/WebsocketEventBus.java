package org.gneisenau.youtube.controller;

import org.gneisenau.youtube.events.ErrorEvent;
import org.gneisenau.youtube.events.InfoEvent;
import org.gneisenau.youtube.events.StatusUpdateEvent;
import org.gneisenau.youtube.events.VideoAddEvent;
import org.gneisenau.youtube.events.VideoDeleteEvent;
import org.gneisenau.youtube.events.VideoUpdateEvent;
import org.gneisenau.youtube.events.WarningEvent;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.gneisenau.youtube.to.EventTO;
import org.gneisenau.youtube.to.StatusEventTO;
import org.gneisenau.youtube.to.VideoTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebsocketEventBus {

	@Autowired
	private SimpMessagingTemplate template;
	@Autowired
	private VideoRepository videoDAO;

	public void notifyNewVideo(VideoTO v) {
		v.setLocalVideoUrl("getVideo/" + v.getId() + ".mp4");
		v.setLocalThumbnailUrl("getThumbnailImage/" + v.getId());
		EventTO addVideoTO = new EventTO(v, new VideoAddEvent(v.getId(), this));
		template.convertAndSend("/topic/event", addVideoTO);
	}

	public void notifyDeleteVideo(VideoTO v) {
		EventTO deleteVideoTO = new EventTO(v, new VideoDeleteEvent(v.getId(), this));
		template.convertAndSend("/topic/event", deleteVideoTO);
	}

	@EventListener
	public void onApplicationEvent(StatusUpdateEvent event) {
		Video video = videoDAO.findById(event.getId());
		StatusEventTO statusTO = new StatusEventTO(video, event);
		template.convertAndSend("/topic/event", statusTO);
	}

	@EventListener
	public void onApplicationEvent(ErrorEvent event) {
		EventTO statusTO = new EventTO(event.getText(), event);
		template.convertAndSend("/topic/event", statusTO);
	}

	@EventListener
	public void onApplicationEvent(WarningEvent event) {
		EventTO statusTO = new EventTO(event.getText(), event);
		template.convertAndSend("/topic/event", statusTO);
	}

	@EventListener
	public void onApplicationEvent(InfoEvent event) {
		EventTO statusTO = new EventTO(event.getText(), event);
		template.convertAndSend("/topic/event", statusTO);
	}

	@EventListener
	public void onApplicationEvent(VideoUpdateEvent event) {
		EventTO statusTO = new EventTO(event.getO(), event);
		template.convertAndSend("/topic/event", statusTO);
	}

}
