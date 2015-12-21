package org.gneisenau.youtube.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.gneisenau.youtube.model.UserSettings;
import org.gneisenau.youtube.model.UserSettingsRepository;
import org.gneisenau.youtube.to.UserSettingsTO;
import org.gneisenau.youtube.to.VideoTO;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.google.api.Google;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Transactional
@Controller
public class SettingsController {

	private Facebook facebook;
	private Twitter twitter;
	private Google google;

	@Autowired
	private UserSettingsRepository userSettingsDAO;
	private static final Logger logger = Logger.getLogger(SettingsController.class);

	@RequestMapping(value = "/settings", method = RequestMethod.POST)
	public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
		UserSettings settings = userSettingsDAO.findByLoggedInUser();

		ModelAndView model = new ModelAndView("settings");
		model.addObject("usersettings", settings);
		return model;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@Valid UserSettingsTO to, BindingResult result, Model m) {

		UserSettings settings = userSettingsDAO.findByLoggedInUser();

		settings.setMailTo(to.getMailTo());
		settings.setNotifyProcessedState(to.isNotifyProcessedState());
		settings.setNotifyReleaseState(to.isNotifyReleaseState());
		settings.setNotifyUploadState(to.isNotifyUploadState());
		settings.setNotifyErrorState(to.isNotifyErrorState());
		String tags = to.getDefaultTags();
		String[] tagList = tags.split(",");
		List<String> tagsCollection = new ArrayList<String>();
		CollectionUtils.addAll(tagsCollection, tagList);
		settings.setDefaultTags(tagsCollection);
		settings.setVideoFooter(to.getVideoFooter());
		settings.setPostOnFacebook(to.isPostOnFacebook());
		settings.setPostOnTwitter(to.isPostOnTwitter());
		settings.setFacebookPost(to.getFacebookPost());
		settings.setTwitterPost(to.getTwitterPost());

		userSettingsDAO.persist(settings);
		return "redirect:/list.do";
	}

	@RequestMapping(value = "/connectToFacebook", method = RequestMethod.POST)
	public String connectFacebook() {
		return "redirect:/connect/facebook.do";
	}
	@RequestMapping(value = "/connectToTwitter", method = RequestMethod.POST)
	public String connectTwitter() {
		return "redirect:/connect/twitter.do";
	}
	@RequestMapping(value = "/connectToGoogle", method = RequestMethod.POST)
	public String connectGoogle() {
		return "redirect:/connect/google.do";
	}


	@RequestMapping(value = "/connectedToFacebook", method = RequestMethod.POST)
	private boolean isConnectedToFacebook() {
		return facebook.isAuthorized();
	}

	@RequestMapping(value = "/connectedToTwitter", method = RequestMethod.POST)
	private boolean isConnectedToTwitter() {
		return twitter.isAuthorized();
	}

}
