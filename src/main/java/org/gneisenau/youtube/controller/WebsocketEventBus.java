package org.gneisenau.youtube.controller;

import org.gneisenau.youtube.events.StatusUpdateEvent;
import org.gneisenau.youtube.events.VideoAddEvent;
import org.gneisenau.youtube.events.VideoDeleteEvent;
import org.gneisenau.youtube.to.EventTO;
import org.gneisenau.youtube.to.StatusEventTO;
import org.gneisenau.youtube.to.VideoTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class WebsocketEventBus {

	@Autowired
	private SimpMessagingTemplate template;

	public void sendNewVideo(VideoTO v) {
		EventTO addVideoTO = new EventTO(v, new VideoAddEvent(v.getId(), this));
		template.convertAndSend("/topic/event", addVideoTO);
	}

	public void delete(VideoTO v) {
		EventTO deleteVideoTO = new EventTO(v, new VideoDeleteEvent(v.getId(), this));
		template.convertAndSend("/topic/event", deleteVideoTO);
	}

	@TransactionalEventListener
	public void onApplicationEvent(StatusUpdateEvent event) {
		StatusEventTO statusTO = new StatusEventTO(event);
		template.convertAndSend("/topic/event", statusTO);

	}

}
