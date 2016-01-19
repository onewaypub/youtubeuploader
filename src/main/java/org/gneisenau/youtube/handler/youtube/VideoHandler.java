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
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.ClientSecrectsException;
import org.gneisenau.youtube.handler.video.exceptions.PreUploadException;
import org.gneisenau.youtube.handler.video.exceptions.ReleaseException;
import org.gneisenau.youtube.handler.video.exceptions.SecretsStoreException;
import org.gneisenau.youtube.handler.video.exceptions.UploadException;
import org.gneisenau.youtube.model.PrivacySetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Videos.Update;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;

/**
 * Upload a video to the authenticated user's channel. Use OAuth 2.0 to
 * authorize the request. Note that you must add your video files to the project
 * folder to upload them with this application.
 *
 * @author Jeremy Walker
 */
@Service
@PropertySource("file:${user.home}/youtubeuploader.properties")
public class VideoHandler {

	@Autowired
	private Auth auth;
	@Value("${youtube.app.name}")
	private String youtubeAppName;

	private static final Logger logger = Logger.getLogger(VideoHandler.class);

	/**
	 * Define a global instance of a Youtube object, which will be used to make
	 * YouTube Data API requests.
	 */
	private static YouTube youtube;

	/**
	 * Define a global variable that specifies the MIME type of the video being
	 * uploaded.
	 */
	private static final String VIDEO_FILE_FORMAT = "video/*";

	/**
	 * Upload the user-selected video to the user's YouTube channel. The code
	 * looks for the video in the application's project folder and uses OAuth
	 * 2.0 to authorize the API request.
	 *
	 * @param args
	 *            command line args (not used).
	 * @throws AuthorizeException
	 * @throws PreUploadException
	 * @throws UploadException
	 * @throws IOException
	 */
	public String upload(final Long id, PrivacySetting privacySetting, InputStream content, List<String> tags,
			String title, String desc, String channelId, String categoryId, String playlistId, String username,
			boolean ageRestricted) throws AuthorizeException, PreUploadException, UploadException {

		initYoutube(username);

		Video videoObjectDefiningMetadata = new Video();

		setVideoStatus(privacySetting, videoObjectDefiningMetadata);

		VideoSnippet snippet = new VideoSnippet();
		setMetadata(tags, title, desc, channelId, categoryId, snippet);
		videoObjectDefiningMetadata.setSnippet(snippet);

		InputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT, content);

		Video returnedVideo = insert(videoObjectDefiningMetadata, mediaContent);

		if (playlistId != null) {
			try {
				insertPlaylistItem(playlistId, returnedVideo.getId());
			} catch (IOException e) {
				logger.error(e);
			}
		}
		return returnedVideo.getId();
	}

	public String updateMetadata(final Long id, PrivacySetting privacySetting, String youtubeId, List<String> tags,
			String title, String desc, String channelId, String categoryId, String playlistId, String username,
			boolean ageRestricted) throws AuthorizeException, IOException, ReleaseException, PreUploadException, UploadException {

		initYoutube(username);

		Video video = getVideoFromYoutube(youtubeId);

		VideoSnippet videoSnippet = video.getSnippet();

		setMetadata(tags, title, desc, channelId, categoryId, videoSnippet);

		Video returnedVideo = updateVideo(video);

		return returnedVideo.getId();
	}

	public void release(String youtubeId, PrivacySetting privacySetting, String username)
			throws ClientSecrectsException, SecretsStoreException, AuthorizeException, IOException, ReleaseException {

		initYoutube(username);

		Video video = getVideoFromYoutube(youtubeId);

		setVideoStatus(privacySetting, video);

		YouTube.Videos.Update updateVideosRequest = youtube.videos().update("status", video);
		updateVideosRequest.execute();

	}

	private Video insert(Video videoObjectDefiningMetadata, InputStreamContent mediaContent)
			throws PreUploadException, UploadException {
		YouTube.Videos.Insert videoInsert;
		try {
			videoInsert = youtube.videos().insert("snippet,statistics,status", videoObjectDefiningMetadata,
					mediaContent);
		} catch (IOException e) {
			throw new PreUploadException(e);
		}

		Video returnedVideo;
		try {
			returnedVideo = videoInsert.execute();
		} catch (IOException e) {
			throw new UploadException(e);
		}
		return returnedVideo;
	}

	private Video updateVideo(Video video) throws PreUploadException, UploadException {
		Update videoUpdate;
		try {
			videoUpdate = youtube.videos().update("snippet", video);
		} catch (IOException e) {
			throw new PreUploadException(e);
		}

		Video returnedVideo;
		try {
			returnedVideo = videoUpdate.execute();
		} catch (IOException e) {
			throw new UploadException(e);
		}
		return returnedVideo;
	}

	private void initYoutube(String username) throws AuthorizeException {
		Credential credential;
		try {
			credential = auth.authorize(youtubeAppName, username);
		} catch (Exception e) {
			throw new AuthorizeException(e);
		}

		youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
				.setApplicationName(Auth.APP_NAME).build();
	}

	private void setMetadata(List<String> tags, String title, String desc, String channelId, String categoryId,
			VideoSnippet snippet) {
		snippet.setTitle(title);
		snippet.setDescription(desc);
		snippet.setChannelId(channelId);
		snippet.setTags(tags);
		if (categoryId != null || categoryId != "-1") {
			snippet.setCategoryId(categoryId);
		}
	}

	private void setVideoStatus(PrivacySetting privacySetting, Video video) {
		VideoStatus status = new VideoStatus();
		status.setPrivacyStatus(privacySetting.toString().toLowerCase());
		video.setStatus(status);
	}

	private Video getVideoFromYoutube(String youtubeId) throws IOException, ReleaseException {
		YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet").setId(youtubeId);
		VideoListResponse listResponse = listVideosRequest.execute();

		List<Video> videoList = listResponse.getItems();
		if (videoList.isEmpty()) {
			throw new ReleaseException("VideoID konnte auf Youtube nicht gefunden werden: " + youtubeId);
		}

		Video video = videoList.get(0);
		return video;
	}

	private static String insertPlaylistItem(String playlistId, String videoId) throws IOException {

		ResourceId resourceId = new ResourceId();
		resourceId.setKind("youtube#video");
		resourceId.setVideoId(videoId);

		PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
		playlistItemSnippet.setPlaylistId(playlistId);
		playlistItemSnippet.setResourceId(resourceId);

		PlaylistItem playlistItem = new PlaylistItem();
		playlistItem.setSnippet(playlistItemSnippet);

		YouTube.PlaylistItems.Insert playlistItemsInsertCommand = youtube.playlistItems()
				.insert("snippet,contentDetails", playlistItem);
		PlaylistItem returnedPlaylistItem = playlistItemsInsertCommand.execute();

		return returnedPlaylistItem.getId();
	}
}