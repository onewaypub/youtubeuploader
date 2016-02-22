package org.gneisenau.youtube.handler.youtube;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.UploadException;
import org.gneisenau.youtube.handler.youtube.util.Auth;
import org.gneisenau.youtube.handler.youtube.util.YoutubeFactory;
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
	public void testUpload() throws AuthorizeException, IOException, UploadException {
		pushAuthorizationMock();
		pushUploadImageResponse();
		pushUploadImage2Response();
		ByteArrayInputStream content = new ByteArrayInputStream("test".getBytes());
		String imgUrl = handler.upload("2", content, "username", "test".length());
		assertEquals("http://test.de/test", imgUrl);
	}

	@Test(expected = NullPointerException.class)
	public void testUploadNoVideoId() throws AuthorizeException, IOException, UploadException {
		ByteArrayInputStream content = new ByteArrayInputStream("test".getBytes());
		String imgUrl = handler.upload(null, content, "username", "test".length());
		assertEquals("http://test.de/test", imgUrl);
	}
	@Test(expected = IllegalArgumentException.class)
	public void testUploadEmptyVideoId() throws AuthorizeException, IOException, UploadException {
		ByteArrayInputStream content = new ByteArrayInputStream("test".getBytes());
		String imgUrl = handler.upload("", content, "username", "test".length());
		assertEquals("http://test.de/test", imgUrl);
	}

	@Test(expected = NullPointerException.class)
	public void testUploadNoUserame() throws AuthorizeException, IOException, UploadException {
		ByteArrayInputStream content = new ByteArrayInputStream("test".getBytes());
		String imgUrl = handler.upload("1", content, null, "test".length());
		assertEquals("http://test.de/test", imgUrl);
	}
	@Test(expected = IllegalArgumentException.class)
	public void testUploadEmptyUserame() throws AuthorizeException, IOException, UploadException {
		ByteArrayInputStream content = new ByteArrayInputStream("test".getBytes());
		String imgUrl = handler.upload("1", content, "", "test".length());
		assertEquals("http://test.de/test", imgUrl);
	}

	@Test(expected = NullPointerException.class)
	public void testUploadNoContent() throws AuthorizeException, IOException, UploadException {
		String imgUrl = handler.upload("1", null, "username", "test".length());
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
