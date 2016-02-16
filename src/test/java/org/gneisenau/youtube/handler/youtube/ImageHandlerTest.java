package org.gneisenau.youtube.handler.youtube;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.UploadException;
import org.gneisenau.youtube.handler.youtube.util.Auth;
import org.gneisenau.youtube.handler.youtube.util.YoutubeFactory;
import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.model.VideoRepository;
import org.gneisenau.youtube.test.util.TestConfigurationContext;
import org.gneisenau.youtube.test.util.YoutTubeMockHttpTransport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Thumbnails;
import com.google.api.services.youtube.YouTube.Videos;
import com.google.api.services.youtube.YouTube.Videos.Insert;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.google.api.services.youtube.model.ThumbnailSetResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.YouTube.Thumbnails.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigurationContext.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
@ActiveProfiles("test")
public class ImageHandlerTest {


	@Autowired
	private ImageHandler handler;
	@Autowired
	private YoutTubeMockHttpTransport httpTransport;

	@InjectMocks
	@Autowired
	private YoutubeFactory factory;
	@Mock
	private Auth auth;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testUpload() throws AuthorizeException, UploadException, IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		pushAuthorizationMock();
		pushUploadImageResponse();
		pushUploadImage2Response();
		ByteArrayInputStream content = new ByteArrayInputStream("test".getBytes());
		String imgUrl = handler.upload("2", content, "username", "test".length());
		assertEquals("http://test.de/test", imgUrl);
	}

	private void pushUploadImageResponse() throws IOException {
		URL list = VideoHandlerTest.class.getResource("/youtubeResponses/thumbnailSetResponse.json");
		String liststr = FileUtils.readFileToString(new File(list.getPath()));
		httpTransport.addResponse(HttpMethod.POST, HttpStatusCodes.STATUS_CODE_OK,
				"https://www.googleapis.com/upload/youtube/v3/thumbnails/set?videoId=2&uploadType=resumable", liststr);
	}
	private void pushUploadImage2Response() throws IOException {
		URL list = VideoHandlerTest.class.getResource("/youtubeResponses/thumbnailSetResponse.json");
		String liststr = FileUtils.readFileToString(new File(list.getPath()));
		httpTransport.addResponse(HttpMethod.PUT, HttpStatusCodes.STATUS_CODE_OK,
				"https://www.googleapis.com/upload/youtube/v3/thumbnails/set?videoId=2&uploadType=resumable", liststr);
	}

	private void pushAuthorizationMock() throws AuthorizeException {
		Credential creds = new MockGoogleCredential.Builder().build();
		when(auth.authorize(anyString(), anyString())).thenReturn(creds);
	}

}
