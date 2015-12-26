package org.gneisenau.youtube.controller;

import org.dozer.DozerBeanMapper;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.to.VideoTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

	@Autowired
	private DozerBeanMapper dozerBeanMapper;
    @Autowired
    private SimpMessagingTemplate template;
    
	@MessageMapping("/chat")
	@SendTo("/topic/message")
	public void sendNewVideo(Video v) {
		 VideoTO videoTO = dozerBeanMapper.map(v, VideoTO.class);
		 template.convertAndSend("/topic/message", "test");
	}

}
