package org.gneisenau.youtube.handler.youtube;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.youtube.util.YoutubeFactory;
import org.gneisenau.youtube.model.UserSettingsRepository;
import org.gneisenau.youtube.model.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;

@Service
public class YoutubeHandler {

	@Autowired
	private UserSettingsRepository settingsDAO;
	@Autowired
	private YoutubeFactory youtubefactory;
	private static final Logger logger = LoggerFactory.getLogger(YoutubeHandler.class);

	private static final String API_KEY = "AIzaSyD9GYNNMLGXfc8OeZx0etSYvU94STP9hrM";

	private Map<String, String> categories = new HashMap<String, String>();
	private long lastUpdate = 0;
	private long nextUpdateDelta = 1800000;//every 30 min

	/**
	 * Create a playlist and add it to the authorized account.
	 * 
	 * @throws AuthorizeException
	 * @throws SecretsStoreException
	 * @throws ClientSecrectsException
	 * @throws IOException
	 */
	public Map<String, String> getPlaylists(String username) {
		Map<String, String> playlistMap = new HashMap<String, String>();
		try {
			String mailTo = settingsDAO.findByUserName(username).getMailTo();
			if (mailTo == null || mailTo.trim().length() == 0) {
				return new HashMap<String, String>();
			}
			YouTube.Playlists.List searchList = youtubefactory.getYoutube(username).playlists().list("id,snippet,contentDetails");
			searchList.setFields(
					"etag,eventId,items(contentDetails,etag,id,kind,player,snippet,status),kind,nextPageToken,pageInfo,prevPageToken,tokenPagination");
			searchList.setMine(true);
			searchList.setMaxResults((long) 10);
			PlaylistListResponse playListResponse = searchList.execute();
			List<Playlist> playlists = playListResponse.getItems();

			if (playlists != null) {
				Iterator<Playlist> iteratorPlaylistResults = playlists.iterator();
				if (!iteratorPlaylistResults.hasNext()) {
					System.out.println(" There aren't any results for your query.");
				}
				while (iteratorPlaylistResults.hasNext()) {
					Playlist playlist = iteratorPlaylistResults.next();
					playlistMap.put(playlist.getId(), playlist.getSnippet().getTitle());
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return playlistMap;
	}

	public Map<String, String> getCategories() {
		if(lastUpdate == 0 || System.currentTimeMillis() - lastUpdate > nextUpdateDelta){
			categories.clear();
		}
		if (categories.size() == 0) {
			URL url;
			try {
				url = new URL("https://www.googleapis.com/youtube/v3/videoCategories?part=snippet&regionCode=de&key="
						+ API_KEY);
				InputStream is = url.openStream();
				JsonReader rdr = Json.createReader(is);

				JsonObject obj = rdr.readObject();
				JsonArray results = obj.getJsonArray("items");
				for (JsonObject result : results.getValuesAs(JsonObject.class)) {
					categories.put(result.getString("id"), result.getJsonObject("snippet").getString("title"));
				}
				lastUpdate = System.currentTimeMillis();
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		return categories;
	}
	

	
}
