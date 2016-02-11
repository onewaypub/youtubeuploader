package org.gneisenau.youtube.handler.youtube;

import static org.junit.Assert.*;

import org.gneisenau.youtube.handler.video.FfmpegHandler;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.UploadException;
import org.gneisenau.youtube.handler.youtube.util.YoutubeFactory;
import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.test.util.TestConfigurationContext;
import org.gneisenau.youtube.utils.IOService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Videos;
import com.google.api.services.youtube.YouTube.Videos.Insert;
import com.google.api.services.youtube.model.Video;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigurationContext.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
@ActiveProfiles("dev")
public class VideoHandlerTest {

	@InjectMocks
	@Autowired
	private VideoHandler handler;

	@Mock
	private YoutubeFactory youtubeFactory;

	@Test
	public void testUpload() throws AuthorizeException, UploadException, IOException {
		YouTube youTube = Mockito.mock(YouTube.class);
		Videos v = mock(Videos.class);
		Insert i = mock(Insert.class);
		when(youTube.videos()).thenReturn(v);
		when(v.insert(anyString(), any(Video.class), any(AbstractInputStreamContent.class))).thenReturn(i);
		when(youtubeFactory.getYoutube(anyString())).thenReturn(youTube);
		ByteArrayInputStream content = new ByteArrayInputStream("test".getBytes());
		String id = handler.upload(PrivacySetting.Private, "testTitle", content, "test");
		assertEquals("1", id);
	}

	@Test
	public void testUpdateMetadata() {
		fail("Not yet implemented");
	}

	@Test
	public void testRelease() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertPlaylistItem() {
		fail("Not yet implemented");
	}

}
