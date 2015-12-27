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

package org.gneisenau.youtube.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.gneisenau.youtube.exceptions.AuthorizeException;
import org.gneisenau.youtube.exceptions.ClientSecrectsException;
import org.gneisenau.youtube.exceptions.PreUploadException;
import org.gneisenau.youtube.exceptions.ReleaseException;
import org.gneisenau.youtube.exceptions.SecretsStoreException;
import org.gneisenau.youtube.exceptions.UploadException;
import org.gneisenau.youtube.model.PrivacySetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
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

		Credential credential;
		try {
			credential = auth.authorize(youtubeAppName, username);
		} catch (Exception e) {
			throw new AuthorizeException(e);
		}

		youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
				.setApplicationName(Auth.APP_NAME).build();

		Video videoObjectDefiningMetadata = new Video();

		VideoStatus status = new VideoStatus();
		status.setPrivacyStatus(privacySetting.toString().toLowerCase());
		videoObjectDefiningMetadata.setStatus(status);
		VideoSnippet snippet = new VideoSnippet();

		snippet.setTitle(title);
		snippet.setDescription(desc);
		snippet.setTags(tags);
		if (categoryId != null || categoryId != "-1") {
			snippet.setCategoryId(categoryId);
		}
		snippet.setChannelId(channelId);

		videoObjectDefiningMetadata.setSnippet(snippet);

		InputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT, content);

		YouTube.Videos.Insert videoInsert;
		try {
			videoInsert = youtube.videos().insert("snippet,statistics,status", videoObjectDefiningMetadata,
					mediaContent);
		} catch (IOException e) {
			throw new PreUploadException(e);
		}

		MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

		uploader.setDirectUploadEnabled(false);

		Video returnedVideo;
		try {
			returnedVideo = videoInsert.execute();
		} catch (IOException e) {
			throw new UploadException(e);
		}

		if (playlistId != null) {
			try {
				insertPlaylistItem(playlistId, returnedVideo.getId());
			} catch (IOException e) {
				logger.error(e);
			}
		}
		return returnedVideo.getId();
	}

	/**
	 * Upload the user-selected video to the user's YouTube channel. The code
	 * looks for the video in the application's project folder and uses OAuth
	 * 2.0 to authorize the API request.
	 *
	 * @param args
	 *            command line args (not used).
	 * @throws IOException
	 * @throws AuthorizeException
	 * @throws SecretsStoreException
	 * @throws ClientSecrectsException
	 * @throws ReleaseException
	 */
	public void release(String youtubeId, PrivacySetting privacySetting, String username)
			throws ClientSecrectsException, SecretsStoreException, AuthorizeException, IOException, ReleaseException {

		// Authorize the request.
		Credential credential = auth.authorize(youtubeAppName, username);

		// This object is used to make YouTube Data API requests.
		youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
				.setApplicationName(Auth.APP_NAME).build();

		// Call the YouTube Data API's youtube.videos.list method to
		// retrieve the resource that represents the specified video.
		YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet").setId(youtubeId);
		VideoListResponse listResponse = listVideosRequest.execute();

		// Since the API request specified a unique video ID, the API
		// response should return exactly one video. If the response does
		// not contain a video, then the specified video ID was not found.
		List<Video> videoList = listResponse.getItems();
		if (videoList.isEmpty()) {
			throw new ReleaseException("VideoID konnte auf Youtube nicht gefunden werden: " + youtubeId);
		}

		// Extract the snippet from the video resource.
		Video video = videoList.get(0);
		VideoStatus status = new VideoStatus();
		status.setPrivacyStatus(privacySetting.toString().toLowerCase());
		video.setStatus(status);

		// Update the video resource by calling the videos.update() method.
		YouTube.Videos.Update updateVideosRequest = youtube.videos().update("snippet,status", video);
		updateVideosRequest.execute();

	}

	/**
	 * Create a playlist item with the specified video ID and add it to the
	 * specified playlist.
	 *
	 * @param playlistId
	 *            assign to newly created playlistitem
	 * @param videoId
	 *            YouTube video id to add to playlistitem
	 */
	private static String insertPlaylistItem(String playlistId, String videoId) throws IOException {

		// Define a resourceId that identifies the video being added to the
		// playlist.
		ResourceId resourceId = new ResourceId();
		resourceId.setKind("youtube#video");
		resourceId.setVideoId(videoId);

		// Set fields included in the playlistItem resource's "snippet" part.
		PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
		// playlistItemSnippet.setTitle("First video in the test playlist");
		playlistItemSnippet.setPlaylistId(playlistId);
		playlistItemSnippet.setResourceId(resourceId);

		// Create the playlistItem resource and set its snippet to the
		// object created above.
		PlaylistItem playlistItem = new PlaylistItem();
		playlistItem.setSnippet(playlistItemSnippet);

		// Call the API to add the playlist item to the specified playlist.
		// In the API call, the first argument identifies the resource parts
		// that the API response should contain, and the second argument is
		// the playlist item being inserted.
		YouTube.PlaylistItems.Insert playlistItemsInsertCommand = youtube.playlistItems()
				.insert("snippet,contentDetails", playlistItem);
		PlaylistItem returnedPlaylistItem = playlistItemsInsertCommand.execute();

		return returnedPlaylistItem.getId();
	}
}