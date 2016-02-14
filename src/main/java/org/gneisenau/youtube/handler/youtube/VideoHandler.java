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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.NotFoundException;
import org.gneisenau.youtube.handler.video.exceptions.UpdateException;
import org.gneisenau.youtube.handler.video.exceptions.UploadException;
import org.gneisenau.youtube.handler.youtube.util.YoutubeFactory;
import org.gneisenau.youtube.model.PrivacySetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

@Service
public class VideoHandler {

	@Autowired
	private YoutubeFactory youtubefactory;

	private static final Logger logger = Logger.getLogger(VideoHandler.class);

	private static final String VIDEO_FILE_FORMAT = "video/*";

	public String upload(PrivacySetting privacySetting, String title, InputStream content, String username) throws AuthorizeException, UploadException {

		Video videoObjectDefiningMetadata = new Video();

		setVideoStatus(privacySetting, videoObjectDefiningMetadata);

		VideoSnippet snippet = new VideoSnippet();
		setMetadata(new ArrayList<String>(), title, "", "", "", snippet);
		videoObjectDefiningMetadata.setSnippet(snippet);

		InputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT, content);

		Video returnedVideo = insert(videoObjectDefiningMetadata, mediaContent, username);

		return returnedVideo.getId();
	}

	public String updateMetadata(PrivacySetting privacySetting, String youtubeId, List<String> tags,
			String title, String desc, String channelId, String categoryId, String username,
			boolean ageRestricted) throws AuthorizeException, UpdateException, NotFoundException {

		Video video = getVideoFromYoutube(youtubeId, username);

		VideoSnippet videoSnippet = video.getSnippet();

		setMetadata(tags, title, desc, channelId, categoryId, videoSnippet);

		Video returnedVideo = updateVideo(video, username);

		return returnedVideo.getId();
	}

	public void release(String youtubeId, PrivacySetting privacySetting, String username)
			throws AuthorizeException, UpdateException, NotFoundException {

		Video video = getVideoFromYoutube(youtubeId, username);

		setVideoStatus(privacySetting, video);

		YouTube.Videos.Update updateVideosRequest;
		try {
			updateVideosRequest = youtubefactory.getYoutube(username).videos().update("status", video);
			updateVideosRequest.execute();
		} catch (IOException e) {
			throw new UpdateException(e);
		}

	}

	public String insertPlaylistItem(String playlistId, String videoId, String username) throws IOException, AuthorizeException {
	
		ResourceId resourceId = new ResourceId();
		resourceId.setKind("youtube#video");
		resourceId.setVideoId(videoId);
	
		PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
		playlistItemSnippet.setPlaylistId(playlistId);
		playlistItemSnippet.setResourceId(resourceId);
	
		PlaylistItem playlistItem = new PlaylistItem();
		playlistItem.setSnippet(playlistItemSnippet);
	
		YouTube.PlaylistItems.Insert playlistItemsInsertCommand = youtubefactory.getYoutube(username).playlistItems()
				.insert("snippet,contentDetails", playlistItem);
		PlaylistItem returnedPlaylistItem = playlistItemsInsertCommand.execute();
	
		return returnedPlaylistItem.getId();
	}

	private Video insert(Video videoObjectDefiningMetadata, InputStreamContent mediaContent, String username) throws UploadException, AuthorizeException {
		YouTube.Videos.Insert videoInsert;
		try {
			videoInsert = youtubefactory.getYoutube(username).videos().insert("snippet,statistics,status", videoObjectDefiningMetadata,
					mediaContent);
		} catch (IOException e) {
			throw new UploadException(e);
		}

		Video returnedVideo;
		try {
			returnedVideo = videoInsert.execute();
		} catch (IOException e) {
			throw new UploadException(e);
		}
		return returnedVideo;
	}

	private Video updateVideo(Video video, String username) throws UpdateException, AuthorizeException {
		Update videoUpdate;
		try {
			videoUpdate = youtubefactory.getYoutube(username).videos().update("snippet", video);
		} catch (IOException e) {
			throw new UpdateException(e);
		}

		Video returnedVideo;
		try {
			returnedVideo = videoUpdate.execute();
		} catch (IOException e) {
			throw new UpdateException(e);
		}
		return returnedVideo;
	}

	private void setMetadata(List<String> tags, String title, String desc, String channelId, String categoryId,
			VideoSnippet snippet) {
		title = StringUtils.stripToEmpty(title);
		desc = StringUtils.stripToEmpty(desc);
		channelId = StringUtils.stripToEmpty(channelId);
		categoryId = StringUtils.stripToEmpty(categoryId);
			
		snippet.setTitle(title);
		snippet.setDescription(desc);
		snippet.setChannelId(channelId);
		snippet.setTags(tags);
		if (categoryId != null && categoryId != "-1") {
			snippet.setCategoryId(categoryId);
		}
	}

	private void setVideoStatus(PrivacySetting privacySetting, Video video) {
		VideoStatus status = new VideoStatus();
		status.setPrivacyStatus(privacySetting.toString().toLowerCase());
		video.setStatus(status);
	}

	private Video getVideoFromYoutube(String youtubeId, String username) throws NotFoundException, AuthorizeException {
		YouTube.Videos.List listVideosRequest;
		List<Video> videoList;
		try {
			listVideosRequest = youtubefactory.getYoutube(username).videos().list("snippet").setId(youtubeId);
			VideoListResponse listResponse = listVideosRequest.execute();
			videoList = listResponse.getItems();
		} catch (IOException e) {
			throw new NotFoundException(e);
		}

		if (videoList.isEmpty()) {
			throw new NotFoundException("VideoID konnte auf Youtube nicht gefunden werden: " + youtubeId);
		}

		Video video = videoList.get(0);
		return video;
	}
}