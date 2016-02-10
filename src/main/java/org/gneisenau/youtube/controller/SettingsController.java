package org.gneisenau.youtube.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.dozer.DozerBeanMapper;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.youtube.util.Auth;
import org.gneisenau.youtube.model.UserSettings;
import org.gneisenau.youtube.model.UserSettingsRepository;
import org.gneisenau.youtube.to.UserSettingsTO;
import org.gneisenau.youtube.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.google.api.Google;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Transactional
@Controller
public class SettingsController {

	@Autowired
	private Facebook facebook;
	@Autowired
	private Twitter twitter;
	@Autowired
	private Google google;

	@Autowired
	private UserSettingsRepository userSettingsDAO;
	@Autowired
	private DozerBeanMapper dozerBeanMapper;
	@Autowired
	private Auth authService;
	@Autowired
	private SecurityUtil secUtil;

	@RequestMapping(value = "/settings", method = RequestMethod.GET)
	public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
		
		UserSettingsTO settings = dozerBeanMapper.map(userSettingsDAO.findByLoggedInUser(), UserSettingsTO.class);

		ModelAndView model = new ModelAndView("settings");
		model.addObject("usersettings", settings);
		model.addObject("connectedToFacebook", false);
		try {
			model.addObject("connectedToYoutube", authService.authorize("youtube", secUtil.getPrincipal()) == null ? false : true);
		} catch (AuthorizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addObject("connectedToTwitter", false);
		model.addObject("connectedToGoogle", false);
		return model;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@Valid UserSettingsTO to, BindingResult result, Model m) {

		UserSettings settings = userSettingsDAO.findByLoggedInUser();

		dozerBeanMapper.map(to, settings);

		userSettingsDAO.persist(settings);
		return "redirect:/list";
	}

}
