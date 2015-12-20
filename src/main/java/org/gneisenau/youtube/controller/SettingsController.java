package org.gneisenau.youtube.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gneisenau.youtube.model.UserSettings;
import org.gneisenau.youtube.model.UserSettingsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Transactional
@Controller
public class SettingsController {

	@Autowired
	private UserSettingsDAO userSettingsDAO;
	private static final Logger logger = LogManager.getLogger(SettingsController.class);

	@RequestMapping(value = "/settings", method = RequestMethod.POST)
	public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
		UserSettings settings = userSettingsDAO.findByLoggedInUser();

		ModelAndView model = new ModelAndView("settings");
		model.addObject("usersettings", settings);
		return model;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@RequestParam("mailTo") String mailTo,
			@RequestParam(value = "notifyProcessedState", required = false) boolean notifyProcessedState,
			@RequestParam(value = "notifyReleaseState", required = false) boolean notifyReleaseState,
			@RequestParam(value = "notifyUploadState", required = false) boolean notifyUploadState,
			@RequestParam(value = "notifyErrorState", required = false) boolean notifyErrorState) {

		UserSettings settings = userSettingsDAO.findByLoggedInUser();

		settings.setMailTo(mailTo);
		settings.setNotifyProcessedState(notifyProcessedState);
		settings.setNotifyReleaseState(notifyReleaseState);
		settings.setNotifyUploadState(notifyUploadState);
		settings.setNotifyErrorState(notifyErrorState);
		userSettingsDAO.persist(settings);
		return "redirect:/list.do";
	}

	@RequestMapping(value = "/back", method = RequestMethod.POST)
	public String back() {
		return "redirect:/list.do";
	}

	private String getPrincipal() {
		String userName = null;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails) {
			userName = ((UserDetails) principal).getUsername();
		} else {
			userName = principal.toString();
		}
		return userName;
	}

}
