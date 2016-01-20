package org.gneisenau.youtube.handler.youtube;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.collect.Lists;

@Controller
public class VerificationCodeController {

	public final static String connectPath = "/connect/youtube";
	// This OAuth 2.0 access scope allows for full read/write access to the
	// authenticated user's account.
	private List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube",
			"https://www.googleapis.com/auth/youtube.upload");
	public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	public static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final String providerId = "youtube";

	/**
	 * GET /connect/{providerId} - Displays a web page showing connection status
	 * provider.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = connectPath, produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
	public @ResponseBody String showConnectionStatus(HttpServletRequest request, HttpServletResponse response) {
		return null;

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
				clientSecrets, scopes).setAccessType("offline").setApprovalPrompt("force").build();
		AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl()
				.setRedirectUri("/connect/" + providerId);
		return "redirect:" + authorizationUrl;

	}

	/**
	 * GET /connect/{providerId}?code={code} - Receives the authorization
	 * callback from the provider, accepting an authorization code. Uses the
	 * code to request an access token and complete the connection.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = connectPath, params = "code", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
	public @ResponseBody String retrieveCode(@RequestParam("code") String code,HttpServletRequest request, HttpServletResponse response) {
		// now check if this is an initial call or the response from
		// youtube. Check if the requests contains the code for the flow
		String error = request.getParameter("error");
		return null;
	}

	/**
	 * DELETE /connect/{providerId}/{providerUserId} - Severs a specific
	 * connection with the provider, based on the user's provider user ID.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = connectPath + "/{providerUserId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.DELETE)
	public @ResponseBody String severConnection(@PathVariable("providerUserId") String providerUserId, HttpServletRequest request, HttpServletResponse response) {
		return null;

	}

	@RequestMapping(value = connectPath, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String recieveAuthToken(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// If is already connected show connected page
		String clientId = ""; // load from property
		String secret = ""; // load from db
		String redirectUri = "";/// connect/youtube
		String authUri = "";
		String tokenUri = "";
		String providerId = "youtube";
		String userId = "";
		GoogleClientSecrets clientSecrets = null;
		// Load the secrects from the db

		// Check the creds
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, scopes).setAccessType("offline").setApprovalPrompt("force").build();
		Credential credential = flow.loadCredential("username");

		// TEst credentials
		if (credential != null && (credential.getRefreshToken() != null || credential.getExpiresInSeconds() > 60)) {
			// redirect to already signed in page
			return "connect/" + providerId + "Connected";
		} else {
			// now check if this is an initial call or the response from
			// youtube. Check if the requests contains the code for the flow
			String code = request.getParameter("code");
			String error = request.getParameter("error");
			// Question: Where to get expireTime
			if (code != null || error != null) {
				// Start sign in
				// else if redirect to AuthorizationCodeRequestUrl
				// authorizationUrl =
				// flow.newAuthorizationUrl().setRedirectUri(redirectUri);
				// with redirectUri of this controller path

				// Redirect to signed in page
				return "connect/" + providerId + "Connected";
			} else {
				// Start sign in
				// else if redirect to AuthorizationCodeRequestUrl
				// authorizationUrl =
				// flow.newAuthorizationUrl().setRedirectUri(redirectUri);
				// with redirectUri of this controller path
				// Redirect to connect site with link to youtube
				return "connect/" + providerId + "Connect";
			}

		}
	}

}
