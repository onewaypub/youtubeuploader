package org.gneisenau.youtube.handler.youtube;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.NotFoundException;
import org.gneisenau.youtube.handler.video.exceptions.UpdateException;
import org.gneisenau.youtube.handler.video.exceptions.UploadException;
import org.gneisenau.youtube.handler.youtube.util.Auth;
import org.gneisenau.youtube.handler.youtube.util.YoutubeFactory;
import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.test.util.TestConfigurationContext;
import org.gneisenau.youtube.test.util.YoutTubeMockHttpTransport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential;
import com.google.api.client.http.HttpStatusCodes;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigurationContext.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
@ActiveProfiles("test")
public class VideoHandlerTest {

	@Autowired
	private VideoHandler handler;
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
	public void testUpload() throws AuthorizeException, UploadException, IOException {
		URL resource = VideoHandlerTest.class.getResource("/youtubeResponses/videoInsert.json");
		String string = FileUtils.readFileToString(new File(resource.getPath()));
		httpTransport.addResponse(HttpMethod.POST, HttpStatusCodes.STATUS_CODE_OK,
				"https://www.googleapis.com/upload/youtube/v3/videos?part=snippet,statistics,status&uploadType=resumable",
				string);
		httpTransport.addResponse(HttpMethod.PUT, HttpStatusCodes.STATUS_CODE_OK,
				"https://www.googleapis.com/upload/youtube/v3/videos?part=snippet,statistics,status&uploadType=resumable",
				string);
		pushAuthorizationMock();
		ByteArrayInputStream content = new ByteArrayInputStream("test".getBytes());
		String id = handler.upload(PrivacySetting.Private, "testTitle", content, "test");
		assertEquals("1", id);
	}

	@Test
	public void testUpdateMetadata() throws IOException, AuthorizeException, UpdateException, NotFoundException {
		pushVideoListResponse();
		pushVideoUpdateMetadataResponse();
		pushAuthorizationMock();
		String id = handler.updateMetadata(PrivacySetting.Private, "1", new ArrayList<String>(), "title", "desc",
				"channelId", "categoryId", "username", true);
		assertEquals("1", id);
	}

	@Test
	public void testRelease() throws IOException, AuthorizeException, UpdateException, NotFoundException {
		pushVideoListResponse();
		pushVideoUpdatePrivacyResponse();
		pushAuthorizationMock();
		handler.release("1", PrivacySetting.Public, "username");
	}

	@Test
	public void testInsertPlaylistItem() throws IOException, AuthorizeException {
		pushVideoListResponse();
		pushInsertPlaylistResponse();
		pushAuthorizationMock();
		handler.insertPlaylistItem("2", "1", "username");
	}

	private void pushVideoUpdateMetadataResponse() throws IOException {
		URL video = VideoHandlerTest.class.getResource("/youtubeResponses/videoUpdate.json");
		String videostr = FileUtils.readFileToString(new File(video.getPath()));
		httpTransport.addResponse(HttpMethod.PUT, HttpStatusCodes.STATUS_CODE_OK,
				"https://www.googleapis.com/youtube/v3/videos?part=snippet", videostr);
	}

	private void pushVideoUpdatePrivacyResponse() throws IOException {
		URL video = VideoHandlerTest.class.getResource("/youtubeResponses/videoUpdate.json");
		String videostr = FileUtils.readFileToString(new File(video.getPath()));
		httpTransport.addResponse(HttpMethod.PUT, HttpStatusCodes.STATUS_CODE_OK,
				"https://www.googleapis.com/youtube/v3/videos?part=status,snippet", videostr);
	}

	private void pushVideoListResponse() throws IOException {
		URL list = VideoHandlerTest.class.getResource("/youtubeResponses/videoList.json");
		String liststr = FileUtils.readFileToString(new File(list.getPath()));
		httpTransport.addResponse(HttpMethod.GET, HttpStatusCodes.STATUS_CODE_OK,
				"https://www.googleapis.com/youtube/v3/videos?id=1&part=snippet", liststr);
	}

	private void pushInsertPlaylistResponse() throws IOException {
		URL list = VideoHandlerTest.class.getResource("/youtubeResponses/playlistItemResponse.json");
		String liststr = FileUtils.readFileToString(new File(list.getPath()));
		httpTransport.addResponse(HttpMethod.POST, HttpStatusCodes.STATUS_CODE_OK,
				"https://www.googleapis.com/youtube/v3/playlistItems?part=snippet,contentDetails", liststr);
	}

	private void pushAuthorizationMock() throws AuthorizeException {
		Credential creds = new MockGoogleCredential.Builder().build();
		when(auth.authorize(anyString(), anyString())).thenReturn(creds);
	}


}
