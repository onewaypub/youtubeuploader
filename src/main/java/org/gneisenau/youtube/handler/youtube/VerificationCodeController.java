package org.gneisenau.youtube.handler.youtube;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.commons.lang.StringUtils;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
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

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

@Controller
public class VerificationCodeController {

	private final static String connectPath = "/connect/youtube";
	@Autowired
	private SecurityUtil secUtil;
	@Autowired
	private Auth authService;
	private Map<String, String> userTokenRegister = new PassiveExpiringMap<String, String>(600000);
	private static final Logger logger = LoggerFactory.getLogger(VerificationCodeController.class);

	/**
	 * GET /connect/{providerId} - Displays a web page showing connection status
	 * provider.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws AuthorizeException 
	 */
	@RequestMapping(value = connectPath, produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
	public @ResponseBody String showConnectionStatus(HttpServletRequest request, HttpServletResponse response)
			throws IOException, AuthorizeException {
		GoogleAuthorizationCodeFlow flow = authService.createGoogleAuthorizationCodeFlow();
		Credential credential = flow.loadCredential(secUtil.getPrincipal());
		if(credential != null) {
			return "youtubeConnected";
		} else {
			return "youtubeConnect";
		}

	}


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
	public @ResponseBody String initiateConnection(HttpServletRequest request, HttpServletResponse response) throws AuthorizeException {
		GoogleAuthorizationCodeFlow flow = authService.createGoogleAuthorizationCodeFlow();
		String uuid = UUID.randomUUID().toString();
		//Check if the user has already a uuid token; if yes then delete it.
		if(userTokenRegister.containsValue(secUtil.getPrincipal())){
			for(Entry<String, String> e : userTokenRegister.entrySet()){
				if(e.getValue().equals(secUtil.getPrincipal())){
					userTokenRegister.remove(e.getKey());
				}
			}
		}
		userTokenRegister.put(uuid, secUtil.getPrincipal());
		AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl()
				.setRedirectUri(connectPath + "/" + uuid.toString());
		return "redirect:" + authorizationUrl.build();

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
	@RequestMapping(value = connectPath
			+ "/{uuid}", params = "code", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
	public @ResponseBody String retrieveCode(@PathVariable("uuid") String uuid,
			@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response)
					throws IOException, AuthorizeException {
		String error = request.getParameter("error");
		if (userTokenRegister.containsKey(uuid) && StringUtils.isBlank(error) && secUtil.getPrincipal().equals(userTokenRegister.containsKey(uuid))) {
			// if found and matched remove entry. Entry can only be used one
			// time
			String userid = userTokenRegister.remove(uuid);
			GoogleAuthorizationCodeFlow flow = authService.createGoogleAuthorizationCodeFlow();
			AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl()
					.setRedirectUri(connectPath + "/" + uuid);
			TokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(authorizationUrl.build()).execute();
			flow.createAndStoreCredential(tokenResponse, userid);
			return "youtubeConnected";
		} else {
			if(userTokenRegister.containsKey(uuid) && !secUtil.getPrincipal().equals(userTokenRegister.containsKey(uuid))){
				logger.warn("Wrong user and token combination:" + secUtil.getPrincipal() + "<->" + userTokenRegister.get(uuid) + ". Remove previous token");
				userTokenRegister.remove(uuid);
			}
			if(StringUtils.isNotBlank(error)){
				logger.error("Error on authentication from youtube with error: " + error);
			}
			return "youtubeConnect";
		}
	}

	/**
	 * DELETE /connect/{providerId}/{providerUserId} - Severs a specific
	 * connection with the provider, based on the user's provider user ID.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = connectPath
			+ "/{providerUserId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.DELETE)
	public @ResponseBody String severConnection(@PathVariable("providerUserId") String providerUserId,
			HttpServletRequest request, HttpServletResponse response) {
		return null;

	}

}
