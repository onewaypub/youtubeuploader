package org.gneisenau.youtube.handler.youtube;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.commons.lang.StringUtils;
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
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.collect.Lists;

@Controller
public class VerificationCodeController {

	private static final String APPROVAL_PROMPT = "force";
	private static final String ACCESS_TYPE_OFFLINE = "offline";
	public final static String connectPath = "/connect/youtube";
	private List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube",
			"https://www.googleapis.com/auth/youtube.upload");
	public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	public static final JsonFactory JSON_FACTORY = new JacksonFactory();
	@Autowired
	private SecurityUtil secUtil;
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
	 */
	@RequestMapping(value = connectPath, produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
	public @ResponseBody String showConnectionStatus(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		GoogleClientSecrets clientSecrets = null;
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, scopes).setAccessType(ACCESS_TYPE_OFFLINE).setApprovalPrompt(APPROVAL_PROMPT).build();
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
	 */
	@RequestMapping(value = connectPath, produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
	public @ResponseBody String initiateConnection(HttpServletRequest request, HttpServletResponse response) {
		GoogleClientSecrets clientSecrets = null;
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, scopes).setAccessType(ACCESS_TYPE_OFFLINE).setApprovalPrompt(APPROVAL_PROMPT).build();
		String uuid = UUID.randomUUID().toString();
		userTokenRegister.put(secUtil.getPrincipal(), uuid);
		AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl()
				.setRedirectUri(connectPath + "/" + secUtil.getPrincipal() + "/" + uuid.toString());
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
	 */
	@RequestMapping(value = connectPath
			+ "/{userId}/{uuid}", params = "code", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
	public @ResponseBody String retrieveCode(@PathVariable("userId") String userid, @PathVariable("uuid") String uuid,
			@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response)
					throws IOException {
		String error = request.getParameter("error");
		if (userTokenRegister.containsKey(userid) && userTokenRegister.get(userid).equals(uuid) && StringUtils.isBlank(error)) {
			// if found and matched remove entry. Entry can only be used one
			// time
			userTokenRegister.remove(userid);
			GoogleClientSecrets clientSecrets = null;
			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
					clientSecrets, scopes).setAccessType(ACCESS_TYPE_OFFLINE).setApprovalPrompt(APPROVAL_PROMPT)
							.build();
			AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl()
					.setRedirectUri(connectPath + "/" + userid + "/" + uuid);
			TokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(authorizationUrl.build()).execute();
			flow.createAndStoreCredential(tokenResponse, userid);
			return "youtubeConnected";
		} else {
			if (userTokenRegister.containsKey(userid) && !userTokenRegister.get(userid).equals(uuid)) {
				// if token is wrong. delete key for security proposes
				userTokenRegister.remove(userid);
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
