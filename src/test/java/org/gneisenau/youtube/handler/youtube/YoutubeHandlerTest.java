package org.gneisenau.youtube.handler.youtube;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
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
public class YoutubeHandlerTest {

	@Autowired
	private YoutubeHandler handler;
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
	public void testGetPlaylists() throws AuthorizeException, IOException {
		pushAuthorizationMock();
		pushGetPlaylistResponse();
		Map<String, String> playlist = handler.getPlaylists("username");
		assertEquals(1, playlist.size());
		assertEquals("1", playlist.entrySet().iterator().next().getKey());
		assertEquals("title", playlist.entrySet().iterator().next().getValue());
	}

	@Test(expected = NullPointerException.class)
	public void testGetPlaylistsUsernameIsNull() throws AuthorizeException, IOException {
		handler.getPlaylists(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPlaylistsUsernameIsEmpty() throws AuthorizeException, IOException {
		handler.getPlaylists("");
	}

	@Test
	public void testGetCategories() {
		//TODO: Not testing at the moment
	}

	private void pushGetPlaylistResponse() throws IOException {
		URL video = VideoHandlerTest.class.getResource("/youtubeResponses/getPlaylistResponse.json");
		String videostr = FileUtils.readFileToString(new File(video.getPath()));
		httpTransport.addResponse(HttpMethod.GET, HttpStatusCodes.STATUS_CODE_OK,
				"https://www.googleapis.com/youtube/v3/playlists?fields=etag,eventId,items(contentDetails,etag,id,kind,player,snippet,status),kind,nextPageToken,pageInfo,prevPageToken,tokenPagination&maxResults=10&mine=true&part=id,snippet,contentDetails", videostr);
	}

	private void pushAuthorizationMock() throws AuthorizeException {
		Credential creds = new MockGoogleCredential.Builder().build();
		when(auth.authorize(anyString(), anyString())).thenReturn(creds);
	}

}
