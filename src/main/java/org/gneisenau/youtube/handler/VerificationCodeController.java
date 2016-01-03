package org.gneisenau.youtube.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VerificationCodeController {

	public final static String connectPath = "/connect/youtube";

	@RequestMapping(value = connectPath, produces = MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String recieveAuthToken(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		//If is already connected show connected page
		
		//Check if the requests contains the code for the flow		
		
		//else if redirect to AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);
		// with redirectUri of this controller path
		
		
		
		String error = request.getParameter("error");
		String code = request.getParameter("code");

		String doc = "<html><head><title>OAuth 2.0 Authentication Token Recieved</title></head><body>"
				+ "Received verification code. You may now close this window...</body></HTML>";
		return doc;
	}
	
	

}
