/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.gneisenau.youtube.handler.youtube;

import java.io.IOException;

import org.gneisenau.youtube.message.MailSendService;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;

public class AuthorizationCodeMailGateway extends AuthorizationCodeInstalledApp {

	private final MailSendService mailservice;
	private String mailTo;

	public AuthorizationCodeMailGateway(AuthorizationCodeFlow flow, VerificationCodeReceiver receiver,
			MailSendService mailService, String mailTo) {
		super(flow, receiver);
		this.mailservice = mailService;
		this.mailTo = mailTo;
	}

	@Override
	protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
		String authURL = authorizationUrl.build();
		System.out.println(authURL);
		//sendMail(authURL, mailTo);
	}

	private void sendMail(String url, String mailTo) {
		mailservice.sendYoutubeAuthorizationMail(url, mailTo);
	}

}
