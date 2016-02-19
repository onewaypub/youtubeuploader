package org.gneisenau.youtube.message;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.UserSettings;
import org.gneisenau.youtube.model.UserSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailSendService {

	private static final Logger logger = LoggerFactory.getLogger(MailSendService.class);
	@Autowired
	private UserSettingsRepository userSettingsDAO;

	@Value("${mail.server}")
	private String mailServer;
	@Value("${mail.sender.user}")
	private String mailSenderUser;
	@Value("${mail.sender.password}")
	private String mailSenderPassword;

	public void sendStatusMail(String videoTitle, State s, String username) {
		Email email = new SimpleEmail();
		email.setHostName(mailServer);
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator(mailSenderUser, mailSenderPassword));
		email.setSSL(true);
		try {
			email.setFrom(mailSenderUser);
			email.setSubject("Video hat sein Status ge\u00e4ndert - " + s + " - " + videoTitle);
			email.setMsg("Das Video " + videoTitle + " ist nun im Status " + s);
			setMailTo(email, username);
			email.send();
		} catch (EmailException e) {
			logger.error("", e);
		}
	}

	private void setMailTo(Email email, String username) throws EmailException {
		UserSettings settings = userSettingsDAO.findOrCreateByUserName(username);
		email.addTo(settings.getMailTo());
	}

	public void sendErrorMail(String fehlermeldung, String videoTitle, State s, String username) {
		Email email = new SimpleEmail();
		email.setHostName(mailServer);
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator(mailSenderUser, mailSenderPassword));
		email.setSSL(true);
		try {
			email.setFrom(mailSenderUser);
			email.setSubject("Fehler beim Video - " + videoTitle);
			email.setMsg("Das Video konnte nicht vollst\u00e4ndig abgearbeitet werden\nAktueller Status ist " + s
					+ "\nFehlermeldung: " + fehlermeldung);
			setMailTo(email, username);
			email.send();
		} catch (EmailException e) {
			logger.error("", e);
		}
	}

}
