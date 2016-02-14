package org.gneisenau.youtube.handler.youtube;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.UploadException;
import org.gneisenau.youtube.handler.youtube.util.YoutubeFactory;
import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.model.VideoRepository;
import org.gneisenau.youtube.test.util.TestConfigurationContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
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

	@InjectMocks
	@Autowired
	private ImageHandler handler;

	@Mock
	private YoutubeFactory youtubeFactory;
	@Mock
	private VideoRepository videoDAO;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	class MyHttpTransport extends HttpTransport {

		@Override
		protected LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

	}

	class MyHttpRequestInitializer implements HttpRequestInitializer {

		@Override
		public void initialize(HttpRequest request) throws IOException {
		}
	}

	class MyInputStreamContent extends AbstractInputStreamContent {

		public MyInputStreamContent(String type) {
			super(type);
		}

		@Override
		public long getLength() throws IOException {
			return 0;
		}

		@Override
		public boolean retrySupported() {
			return false;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream("test".getBytes());
		}

	}

	class MySet extends Set {

		private ThumbnailSetResponse response;

		public MySet(Thumbnails thumbnails, String videoId, MediaHttpUploader uploader, ThumbnailSetResponse response)
				throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
			thumbnails.super(videoId);
			this.response = response;
			Field field = this.getClass().getDeclaredField("str");
			field.setAccessible(true);
			field.set(this, uploader);
		}

		@Override
		public ThumbnailSetResponse execute() throws IOException {
			return response;
		}

	}

	@Test
	public void testUpload() throws AuthorizeException, UploadException, IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		YouTube youTube = Mockito.mock(YouTube.class);
		Videos vs = mock(Videos.class);
		Thumbnails t = mock(Thumbnails.class);
		ThumbnailSetResponse response = new ThumbnailSetResponse();
		List<ThumbnailDetails> items = new ArrayList<ThumbnailDetails>();
		ThumbnailDetails d = new ThumbnailDetails();
		Thumbnail thumbnail = new Thumbnail();
		thumbnail.setUrl("http://test.de");
		d.setDefault(thumbnail);
		items.add(d);
		response.setItems(items);
		MediaHttpUploader uploader = new MediaHttpUploader(new MyInputStreamContent("test"), new MyHttpTransport(),
				new MyHttpRequestInitializer());

		org.gneisenau.youtube.model.Video v = new org.gneisenau.youtube.model.Video();

		when(youtubeFactory.getYoutube(anyString())).thenReturn(youTube);
		when(youTube.videos()).thenReturn(vs);
		when(youTube.thumbnails()).thenReturn(t);
		Set s = new MySet(t, "test", uploader, response);
		when(t.set(anyString(), any(AbstractInputStreamContent.class))).thenReturn(s);
		when(s.getMediaHttpUploader()).thenReturn(uploader);
		when(s.execute()).thenReturn(response);
		when(videoDAO.findById(any(Long.class))).thenReturn(v);
		ByteArrayInputStream content = new ByteArrayInputStream("test".getBytes());
		handler.upload(1L, "test", content, "testuser", "test".length());
		assertEquals("http://test.de", v.getThumbnailUrl());
	}

}
