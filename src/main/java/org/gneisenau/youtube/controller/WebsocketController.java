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
    
	@MessageMapping("/addvideo")
	public void init(Video v) {
		 VideoTO videoTO = dozerBeanMapper.map(v, VideoTO.class);
		 videoTO.setTitle("test");
		 videoTO.setDescription("test");
		 template.convertAndSend("/topic/message", videoTO);
	}
	
	public void sendNewVideo(Video v) {
		 VideoTO videoTO = dozerBeanMapper.map(v, VideoTO.class);
		 template.convertAndSend("/topic/addVideo", "test");
	}

	public void delete(Video v) {
		 VideoTO videoTO = dozerBeanMapper.map(v, VideoTO.class);
		 template.convertAndSend("/topic/deleteVideo", "test");
	}


}
