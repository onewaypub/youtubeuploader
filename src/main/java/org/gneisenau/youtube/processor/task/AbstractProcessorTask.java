package org.gneisenau.youtube.processor.task;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.gneisenau.youtube.events.StatusUpdateEvent;
import org.gneisenau.youtube.message.MailSendService;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.UserSettingsRepository;
import org.gneisenau.youtube.model.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractProcessorTask {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	private ApplicationEventPublisher publisher;

	@Autowired
	public AbstractProcessorTask(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	public void publishEvent(StatusUpdateEvent event) {
		this.publisher.publishEvent(event);
	}


}
