package org.gneisenau.youtube.utils;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gneisenau.youtube.handler.youtube.YouTubeUtils;
import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.test.util.TestConfigurationContext;
import org.gneisenau.youtube.to.VideoTO;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigurationContext.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
@ActiveProfiles("test")
public class BeanMapperTest {

	@InjectMocks
	@Autowired
	private BeanMapper mapper;
	
	@Mock
	private YouTubeUtils utils;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		when(utils.getPaylistId(anyString(), anyString())).thenReturn("1");
		when(utils.getCategoryId(anyString())).thenReturn("1");
		when(utils.getPlaylistDisplayName(anyString(), anyString())).thenReturn("1");
		when(utils.getCategoryDisplayName(anyString())).thenReturn("1");
	}
	
	@Test
	public void testCreateVideoVideoTOString() {
		Date d = new Date();
		List<String> errors = new ArrayList<String>();
		VideoTO to = new VideoTO();
		to.setAgeRestricted(true);
		to.setCategory("category");
		to.setCategoryId("categoryId");
		to.setChannelId("channelId");
		to.setDescription("description");
		to.setDeveloper("developer");
		to.setErrors(errors);
		to.setGenre("genre");
		to.setId(100L);
		to.setPlaylistId("playlistId");
		to.setPlaylist("playlist");
		to.setPrivacySetting(PrivacySetting.Unlisted);
		to.setPublished("published");
		to.setPublisher("publisher");
		SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm"); 
		to.setReleaseDate(dt.format(d));
		to.setShorttitle("shorttitle");
		to.setState(State.OnUpload.getDisplayName());
		to.setTags("tags");
		to.setThumbnailUrl("thumbnailUrl");
		to.setTitle("title");
		to.setUsername("username");
		to.setVideo("video");
		to.setVideoUrl("videoUrl");
		Video video = mapper.createVideo(to, "username");
	}

	@Test
	public void testCreateVideoVideoString() {
		Date d = new Date();
		List<String> errors = new ArrayList<String>();
		Video v = new Video();
		v.setAgeRestricted(true);
		v.setCategoryId("categoryId");
		v.setChannelId("channelId");
		v.setDescription("description");
		v.setDeveloper("developer");
		v.setErrors(errors);
		v.setGenre("genre");
		v.setId(100L);
		v.setPlaylistId("playlistId");
		v.setPrivacySetting(PrivacySetting.Unlisted);
		v.setPublished("published");
		v.setPublisher("publisher");
		SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm"); 
		v.setReleaseDate(d);
		v.setShorttitle("shorttitle");
		v.setState(State.OnUpload);
		v.setTags("tags");
		v.setThumbnailUrl("thumbnailUrl");
		v.setTitle("title");
		v.setUsername("username");
		v.setVideo("video");
		v.setVideoUrl("videoUrl");
		VideoTO video = mapper.createVideo(v, "username");
	}

	@Test
	public void testCopyVideoVideoTOVideoString() {
		Date d = new Date();
		List<String> errors = new ArrayList<String>();
		Video v = new Video();
		v.setAgeRestricted(true);
		v.setCategoryId("categoryId");
		v.setChannelId("channelId");
		v.setDescription("description");
		v.setDeveloper("developer");
		v.setErrors(errors);
		v.setGenre("genre");
		v.setId(100L);
		v.setPlaylistId("playlistId");
		v.setPrivacySetting(PrivacySetting.Unlisted);
		v.setPublished("published");
		v.setPublisher("publisher");
		SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm"); 
		v.setReleaseDate(d);
		v.setShorttitle("shorttitle");
		v.setState(State.OnUpload);
		v.setTags("tags");
		v.setThumbnailUrl("thumbnailUrl");
		v.setTitle("title");
		v.setUsername("username");
		v.setVideo("video");
		v.setVideoUrl("videoUrl");
		VideoTO to = new VideoTO();
		mapper.copyVideo(v, to, "username");
	}

	@Test
	public void testCopyVideoVideoVideoTOString() {
		Date d = new Date();
		List<String> errors = new ArrayList<String>();
		VideoTO to = new VideoTO();
		to.setAgeRestricted(true);
		to.setCategory("category");
		to.setCategoryId("categoryId");
		to.setChannelId("channelId");
		to.setDescription("description");
		to.setDeveloper("developer");
		to.setErrors(errors);
		to.setGenre("genre");
		to.setId(100L);
		to.setPlaylistId("playlistId");
		to.setPlaylist("playlist");
		to.setPrivacySetting(PrivacySetting.Unlisted);
		to.setPublished("published");
		to.setPublisher("publisher");
		SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm"); 
		to.setReleaseDate(dt.format(d));
		to.setShorttitle("shorttitle");
		to.setState(State.OnUpload.getDisplayName());
		to.setTags("tags");
		to.setThumbnailUrl("thumbnailUrl");
		to.setTitle("title");
		to.setUsername("username");
		to.setVideo("video");
		to.setVideoUrl("videoUrl");
		Video v = new Video();
		mapper.copyVideo(to, v, "username");
	}

}
