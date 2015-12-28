package org.gneisenau.youtube.video;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gneisenau.youtube.events.FFMpegProgressEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FFMpegController {

	private final ApplicationEventPublisher publisher;

	@Autowired
	public FFMpegController(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	public void publishEvent(FFMpegProgressEvent event){
		this.publisher.publishEvent(event);
	}

	@RequestMapping(value = "/ffmpeg/stats/{id}", method = RequestMethod.GET)
	public @ResponseBody void generateReport(@PathVariable("id") long id, HttpServletRequest request, HttpServletResponse response) {
		FFMpegProgressEvent event = new FFMpegProgressEvent(id, this);
		publishEvent(event);
	}

}
