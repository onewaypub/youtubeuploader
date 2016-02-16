package org.gneisenau.youtube.handler.youtube.util;

import java.util.HashMap;
import java.util.Map;

import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;

@Service
public class YoutubeFactory {

	private Map<String, YouTube> youtubeMap = new HashMap<String, YouTube>();

	@Value("${youtube.app.name}")
	private String youtubeAppName;
	@Autowired
	private Auth auth;

	public YouTube getYoutube(String username) throws AuthorizeException {

		if (youtubeMap.containsKey(username)) {
			return youtubeMap.get(username);
		} else {
			Credential credential;
			try {
				credential = auth.authorize(youtubeAppName, username);
			} catch (Exception e) {
				throw new AuthorizeException(e);
			}

			YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
					.setApplicationName(Auth.APP_NAME).build();

			youtubeMap.put(username, youtube);
			return youtube;

		}
	}

}
