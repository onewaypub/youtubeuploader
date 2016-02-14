package org.gneisenau.youtube.handler.youtube;

import static org.junit.Assert.*;

import org.gneisenau.youtube.handler.video.FfmpegHandler;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.NotFoundException;
import org.gneisenau.youtube.handler.video.exceptions.UpdateException;
import org.gneisenau.youtube.handler.video.exceptions.UploadException;
import org.gneisenau.youtube.handler.youtube.util.YoutubeFactory;
import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.test.util.TestConfigurationContext;
import org.gneisenau.youtube.utils.IOService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
import java.util.ArrayList;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Videos;
import com.google.api.services.youtube.YouTube.Videos.Insert;
import com.google.api.services.youtube.YouTube.Videos.List;
import com.google.api.services.youtube.YouTube.Videos.Update;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigurationContext.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
@ActiveProfiles("test")
public class VideoHandlerTest {

	@InjectMocks
	@Autowired
	private VideoHandler handler;

	@Mock
	private YoutubeFactory youtubeFactory;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

	@Test
	public void testUpload() throws AuthorizeException, UploadException, IOException {
		YouTube youTube = Mockito.mock(YouTube.class);
		Videos vs = mock(Videos.class);
		Insert i = mock(Insert.class);
		Video v = new Video();
		v.setId("1");
		
		when(youTube.videos()).thenReturn(vs);
		when(vs.insert(anyString(), any(Video.class), any(AbstractInputStreamContent.class))).thenReturn(i);
		when(i.execute()).thenReturn(v);
		when(youtubeFactory.getYoutube(anyString())).thenReturn(youTube);
		ByteArrayInputStream content = new ByteArrayInputStream("test".getBytes());
		String id = handler.upload(PrivacySetting.Private, "testTitle", content, "test");
		assertEquals("1", id);
	}

	@Test
	public void testUpdateMetadata() throws IOException, AuthorizeException, UpdateException, NotFoundException {
		YouTube youTube = Mockito.mock(YouTube.class);
		Videos vs = mock(Videos.class);
		Update u = mock(Update.class);
		List l = mock(List.class);
		Video v = new Video();
		v.setSnippet(new VideoSnippet());
		VideoListResponse vlr = new VideoListResponse();
		v.setId("1");
		ArrayList<Video> videoList = new ArrayList<Video>();
		videoList.add(v);
		
		when(youTube.videos()).thenReturn(vs);
		when(vs.list("snippet")).thenReturn(l);
		when(l.setId("1")).thenReturn(l);
		vlr.setItems(videoList);
		when(l.execute()).thenReturn(vlr);
		when(vs.update(anyString(), any(Video.class))).thenReturn(u);
		when(u.execute()).thenReturn(v);
		when(youtubeFactory.getYoutube(anyString())).thenReturn(youTube);
		String id = handler.updateMetadata(PrivacySetting.Private, "1", new ArrayList<String>(), "title", "desc", "channelId", "categoryId", "username", true);
		assertEquals("1", id);
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
