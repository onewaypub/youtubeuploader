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

import org.apache.commons.lang3.Validate;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.UploadException;
import org.gneisenau.youtube.handler.youtube.util.YoutubeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.InputStreamContent;
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
	private YoutubeFactory youtubefactory;
	@Value("${youtube.app.name}")
	private String youtubeAppName;


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
	 * @return 
	 * @throws PreUploadException
	 * @throws AuthorizeException
	 * @throws UploadException
	 */
	public String upload(String videoId, InputStream imageFile, String username, long length)
			throws AuthorizeException, UploadException {
		
		Validate.notEmpty(videoId,"No videoId given");
		Validate.notEmpty(videoId,"No videoId given");
		Validate.notNull(imageFile,"No imageFile given");
		Validate.notEmpty(username,"No username given");

		YouTube youTube = youtubefactory.getYoutube(username);

		InputStreamContent mediaContent = new InputStreamContent(IMAGE_FILE_FORMAT, new BufferedInputStream(imageFile));
		mediaContent.setLength(length);

		Set thumbnailSet;
		try {
			thumbnailSet = youTube.thumbnails().set(videoId, mediaContent);
		} catch (IOException e) {
			throw new UploadException(e);
		}

		MediaHttpUploader uploader = thumbnailSet.getMediaHttpUploader();
		uploader.setDirectUploadEnabled(false);

		ThumbnailSetResponse setResponse;
		try {
			setResponse = thumbnailSet.execute();
		} catch (IOException e) {
			throw new UploadException(e);
		}

		return setResponse.getItems().get(0).getDefault().getUrl();

	}
}