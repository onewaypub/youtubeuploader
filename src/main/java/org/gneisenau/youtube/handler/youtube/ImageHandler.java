/*
 * Copyright (c) 2013 Google Inc.
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.UploadException;
import org.gneisenau.youtube.handler.youtube.util.Auth;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Thumbnails.Set;
import com.google.api.services.youtube.model.ThumbnailSetResponse;

/**
 * This sample uses MediaHttpUploader to upload an image and then calls the
 * API's youtube.thumbnails.set method to set the image as the custom thumbnail
 * for a video.
 *
 * @author Ibrahim Ulukaya
 */
@Service
public class ImageHandler {

	@Autowired
	private VideoRepository videoDAO;
	@Autowired
	private Auth auth;
	@Value("${youtube.app.name}")
	private String youtubeAppName;
	@Autowired
	private HttpTransport httpTransport; 
	@Autowired
	private JsonFactory jsonFactory;

	/**
	 * Define a global instance of a Youtube object, which will be used to make
	 * YouTube Data API requests.
	 */
	private static YouTube youtube;

	/**
	 * Define a global variable that specifies the MIME type of the image being
	 * uploaded.
	 */
	private static final String IMAGE_FILE_FORMAT = "image/png";

	/**
	 * Prompt the user to specify a video ID and the path for a thumbnail image.
	 * Then call the API to set the image as the thumbnail for the video.
	 *
	 * @param args
	 *            command line args (not used).
	 * @throws PreUploadException
	 * @throws AuthorizeException
	 * @throws UploadException
	 */
	public void upload(final Long id, String videoId, InputStream imageFile, String username, long length)
			throws AuthorizeException, UploadException {

		// Authorize the request.
		Credential credential;
		try {
			credential = auth.authorize(youtubeAppName, username);
		} catch (Exception e) {
			throw new AuthorizeException(e);
		}

		// This object is used to make YouTube Data API requests.
		youtube = new YouTube.Builder(httpTransport, jsonFactory, credential)
				.setApplicationName(Auth.APP_NAME).build();

		// Create an object that contains the thumbnail image file's
		// contents.
		InputStreamContent mediaContent = new InputStreamContent(IMAGE_FILE_FORMAT, new BufferedInputStream(imageFile));
		mediaContent.setLength(length);

		// Create an API request that specifies that the mediaContent
		// object is the thumbnail of the specified video.
		Set thumbnailSet;
		try {
			thumbnailSet = youtube.thumbnails().set(videoId, mediaContent);
		} catch (IOException e) {
			throw new UploadException(e);
		}

		// Set the upload type and add an event listener.
		MediaHttpUploader uploader = thumbnailSet.getMediaHttpUploader();

		// Indicate whether direct media upload is enabled. A value of
		// "True" indicates that direct media upload is enabled and that
		// the entire media content will be uploaded in a single request.
		// A value of "False," which is the default, indicates that the
		// request will use the resumable media upload protocol, which
		// supports the ability to resume an upload operation after a
		// network interruption or other transmission failure, saving
		// time and bandwidth in the event of network failures.
		uploader.setDirectUploadEnabled(false);

		// Upload the image and set it as the specified video's thumbnail.
		ThumbnailSetResponse setResponse;
		try {
			setResponse = thumbnailSet.execute();
		} catch (IOException e) {
			throw new UploadException(e);
		}

		// Print the URL for the updated video's thumbnail image.
		Video video = videoDAO.findById(id);
		video.setThumbnailUrl(setResponse.getItems().get(0).getDefault().getUrl());

	}
}