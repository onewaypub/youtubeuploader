package org.gneisenau.youtube.scheduler;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.gneisenau.youtube.handler.Auth;
import org.gneisenau.youtube.handler.ImageHandler;
import org.gneisenau.youtube.handler.VideoHandler;
import org.gneisenau.youtube.message.MailSendService;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.UserSettingsRepository;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.utils.IOService;
import org.gneisenau.youtube.video.VideoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;

abstract class AbstractVideoProcessor implements VideoProcessor, Ordered {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	protected UserSettingsRepository userSettingsDAO;

	@Autowired
	protected MailSendService mailService;

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
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
