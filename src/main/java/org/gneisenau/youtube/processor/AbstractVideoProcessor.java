package org.gneisenau.youtube.processor;

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
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractVideoProcessor implements VideoProcessor {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	protected UserSettingsRepository userSettingsDAO;

	@Autowired
	protected MailSendService mailService;

	private final ApplicationEventPublisher publisher;

	@Autowired
	public AbstractVideoProcessor(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	public void publishEvent(StatusUpdateEvent event){
		this.publisher.publishEvent(event);
	}


	protected void handleError(Video v, String message) {
		List<String> errors = v.getErrors();
		if (errors == null)
			errors = new ArrayList<String>();
		errors.add(message);
		v.setErrors(errors);
		v.setState(State.Error);
		if (userSettingsDAO.findByUserName(v.getUsername()).isNotifyErrorState()) {
			mailService.sendErrorMail(message, v.getTitle(), v.getState(), v.getUsername());
		}
	}

	protected void handleError(Video v, String message, Exception e) {
		if (e != null) {
			String returnJSON = e.getMessage();
			try {
				ByteArrayInputStream is = new ByteArrayInputStream(returnJSON.getBytes());
				JsonReader rdr = Json.createReader(is);

				JsonObject obj = rdr.readObject();
				String results = obj.getString("message");
				message = message + " - " + results;
			} catch (Exception ex) {
				logger.error("", ex);
			}
		}
		logger.error("", e);
		List<String> errors = v.getErrors();
		if (errors == null)
			errors = new ArrayList<String>();
		errors.add(message);
		v.setErrors(errors);
		v.setState(State.Error);
		if (userSettingsDAO.findByUserName(v.getUsername()).isNotifyErrorState()) {
			mailService.sendErrorMail(message, v.getTitle(), v.getState(), v.getUsername());
		}
	}

}
