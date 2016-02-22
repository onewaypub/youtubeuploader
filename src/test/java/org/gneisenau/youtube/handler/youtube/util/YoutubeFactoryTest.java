package org.gneisenau.youtube.handler.youtube.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.test.util.TestConfigurationContext;
import org.gneisenau.youtube.test.util.YoutTubeMockHttpTransport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigurationContext.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
@ActiveProfiles("test")
public class YoutubeFactoryTest {

	@InjectMocks
	@Autowired
	private YoutubeFactory factory;
	@Mock
	private Auth auth;
	@Autowired
	private YoutTubeMockHttpTransport httpTransport;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetYoutube() throws AuthorizeException {
		Credential creds = new MockGoogleCredential.Builder().build();
		when(auth.authorize(anyString(), anyString())).thenReturn(creds);
		assertNotNull(factory.getYoutube("test"));
	}

	@Test(expected = NullPointerException.class)
	public void testGetYoutubeEmptyUser() throws AuthorizeException {
		factory.getYoutube(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetYoutubeNullUser() throws AuthorizeException {
		factory.getYoutube("");
	}

	@Test
	public void testGetYoutubeUnknownUser() throws AuthorizeException {
		when(auth.authorize(anyString(), anyString())).thenReturn(null);
		assertNull(factory.getYoutube("unknown2"));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = AuthorizeException.class)
	public void testGetYoutubeUnauthorizedUser() throws AuthorizeException {
		when(auth.authorize(anyString(), anyString())).thenThrow(IOException.class);
		assertNull(factory.getYoutube("unknown"));
	}

}
