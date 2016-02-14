package org.gneisenau.youtube.handler.youtube.util;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.commons.lang.StringUtils;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.model.UserconnectionRepository;
import org.gneisenau.youtube.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

@Controller
public class VerificationCodeController {

	private final static String connectPath = "/connect/youtube";
	@Autowired
	private SecurityUtil secUtil;
	@Autowired
	private Auth authService;
	@Autowired
	private UserconnectionRepository userconnectionRepository;
	private Map<String, String> userTokenRegister = new PassiveExpiringMap<String, String>(600000);
	private static final Logger logger = LoggerFactory.getLogger(VerificationCodeController.class);

	/**
	 * POST /connect/{providerId} - Initiates the connection flow with the
	 * provider.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws AuthorizeException
	 */
	@RequestMapping(value = connectPath, produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
	public @ResponseBody ModelAndView initiateConnection(HttpServletRequest request, HttpServletResponse response)
			throws AuthorizeException {
		String uuid = UUID.randomUUID().toString();
		// Check if the user has already a uuid token; if yes then delete it.
		if (userTokenRegister.containsValue(secUtil.getPrincipal())) {
			for (Entry<String, String> e : userTokenRegister.entrySet()) {
				if (e.getValue().equals(secUtil.getPrincipal())) {
					userTokenRegister.remove(e.getKey());
				}
			}
		}
		userTokenRegister.put(uuid, secUtil.getPrincipal());
		GoogleAuthorizationCodeFlow flow = authService.createGoogleAuthorizationCodeFlow("youtube");
		AuthorizationCodeRequestUrl authorizationUrl = createAuthUrl(flow, uuid);
		return new ModelAndView("redirect:" + authorizationUrl.build());

	}

	private AuthorizationCodeRequestUrl createAuthUrl(GoogleAuthorizationCodeFlow flow, String uuid) {
		AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl()
				.setRedirectUri("http://localhost:8080/YouTubeUploader" + connectPath);
		authorizationUrl.setState(uuid.toString());
		return authorizationUrl;
	}

	/**
	 * GET /connect/{providerId}?code={code} - Receives the authorization
	 * callback from the provider, accepting an authorization code. Uses the
	 * code to request an access token and complete the connection.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws AuthorizeException
	 */
	@RequestMapping(value = connectPath, params = { "state", "code" }, method = RequestMethod.GET)
	public @ResponseBody ModelAndView retrieveCode(@RequestParam("state") String uuid,
			@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response)
					throws IOException, AuthorizeException {
		String error = request.getParameter("error");
		if (userTokenRegister.containsKey(uuid) && StringUtils.isBlank(error)
				&& secUtil.getPrincipal().equals(userTokenRegister.get(uuid))) {
			// if found and matched remove entry. Entry can only be used one
			// time
			String userid = userTokenRegister.remove(uuid);
			GoogleAuthorizationCodeFlow flow = authService.createGoogleAuthorizationCodeFlow("youtube");
			TokenResponse tokenResponse = flow.newTokenRequest(code)
					.setRedirectUri("http://localhost:8080/YouTubeUploader" + connectPath).execute();
			flow.createAndStoreCredential(tokenResponse, userid);
		} else {
			if (userTokenRegister.containsKey(uuid)
					&& !secUtil.getPrincipal().equals(userTokenRegister.containsKey(uuid))) {
				logger.warn("Wrong user and token combination:" + secUtil.getPrincipal() + "<->"
						+ userTokenRegister.get(uuid) + ". Remove previous token");
				userTokenRegister.remove(uuid);
			}
			if (StringUtils.isNotBlank(error)) {
				logger.error("Error on authentication from youtube with error: " + error);
			}
		}
		return new ModelAndView("settings");
	}

	/**
	 * DELETE /connect/{providerId}/{providerUserId} - Severs a specific
	 * connection with the provider, based on the user's provider user ID.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = connectPath, method = RequestMethod.DELETE)
	public @ResponseBody ModelAndView severConnection(@PathVariable("providerUserId") String providerUserId,
			HttpServletRequest request, HttpServletResponse response) {
		userconnectionRepository.remove("youtube", secUtil.getPrincipal());
		return new ModelAndView("settings");
	}

}
