package org.gneisenau.youtube.handler.youtube.util;

import java.util.Map;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.commons.lang3.Validate;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.youtube.YouTube;

@Service
public class YoutubeFactory {

	private Map<String, YouTube> youtubeMap = new PassiveExpiringMap<String, YouTube>(1000 * 60 * 5);

	@Value("${youtube.app.name}")
	private String youtubeAppName;
	@Autowired
	private Auth auth;
	@Autowired
	private HttpTransport httpTransport;
	@Autowired
	private JsonFactory jsonFactory;

	public YouTube getYoutube(String username) throws AuthorizeException {

		Validate.notEmpty(username);

		if (youtubeMap.containsKey(username)) {
			return youtubeMap.get(username);
		} else {
			Credential credential;
			try {
				credential = auth.authorize(youtubeAppName, username);
			} catch (Exception e) {
				throw new AuthorizeException(e);
			}
			if(credential == null){
				return null;
			}

			YouTube youtube = new YouTube.Builder(httpTransport, jsonFactory, credential)
					.setApplicationName(Auth.APP_NAME).build();

			youtubeMap.put(username, youtube);
			return youtube;

		}
	}

}
