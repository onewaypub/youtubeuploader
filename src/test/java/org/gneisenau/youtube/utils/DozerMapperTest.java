package org.gneisenau.youtube.utils;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.test.util.TestConfigurationContext;
import org.gneisenau.youtube.to.VideoTO;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class DozerMapperTest {

	@Autowired
	private DozerBeanMapper dozerBeanMapper;

	@Test
	public void testVideoTO2VideoEntity() throws ParseException {
		Date d = new Date();
		List<String> errors = new ArrayList<String>();
		VideoTO v = new VideoTO();
		v.setAgeRestricted(true);
		v.setCategory("category");
		v.setCategoryId("categoryId");
		v.setChannelId("channelId");
		v.setDescription("description");
		v.setDeveloper("developer");
		v.setErrors(errors);
		v.setGenre("genre");
		v.setId(100L);
		v.setPlaylistId("playlistId");
		v.setPlaylist("playlist");
		v.setPrivacySetting(PrivacySetting.Unlisted);
		v.setPublished("published");
		v.setPublisher("publisher");
		SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm"); 
		v.setReleaseDate(dt.format(d));
		v.setShorttitle("shorttitle");
		v.setState(State.OnUpload.getDisplayName());
		v.setTags("tags");
		v.setThumbnailUrl("thumbnailUrl");
		v.setTitle("title");
		v.setUsername("username");
		v.setVideo("video");
		v.setVideoUrl("videoUrl");

		Video bean = dozerBeanMapper.map(v, Video.class);

		assertEquals("category", bean.getCategoryId());
		assertEquals("channelId", bean.getChannelId());
		assertEquals("description", bean.getDescription());
		assertEquals("developer", bean.getDeveloper());
		assertEquals("genre", bean.getGenre());
		assertEquals("playlist", bean.getPlaylistId());
		assertEquals("published", bean.getPublished());
		assertEquals("publisher", bean.getPublisher());
		d = dt.parse(dt.format(d));
		assertEquals(d, bean.getReleaseDate());
		assertEquals("shorttitle", bean.getShorttitle());
		assertEquals("tags", bean.getTags());
		assertEquals("title", bean.getTitle());
		assertEquals((Long)100L, bean.getId());

		assertNull("username", bean.getUsername());
		assertNull(bean.getThumbnailUrl());
		assertNull(bean.getErrors());
		assertNull(bean.getPrivacySetting());
		assertNull(bean.getVideo());
		assertNull(bean.getVideoUrl());
		assertNull(bean.getState());
	}

	@Test
	public void testVideoEntity2VideoTO() {
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
		v.setReleaseDate(d);
		v.setShorttitle("shorttitle");
		v.setState(State.OnUpload);
		v.setTags("tags");
		v.setThumbnail("thumbnail");
		v.setThumbnailUrl("thumbnailUrl");
		v.setTitle("title");
		v.setUsername("username");
		v.setVideo("video");
		v.setVideoUrl("videoUrl");

		VideoTO bean = dozerBeanMapper.map(v, VideoTO.class);

		assertEquals("category", bean.getCategory());
		assertEquals("categoryId", bean.getCategoryId());
		assertEquals("channelId", bean.getChannelId());
		assertEquals("description", bean.getDescription());
		assertEquals("developer", bean.getDeveloper());
		assertEquals(errors, bean.getErrors());
		assertEquals("genre", bean.getGenre());
		assertEquals((Long)100L, bean.getId());
		assertEquals("playlistId", bean.getPlaylistId());
		assertEquals(PrivacySetting.Unlisted, bean.getPrivacySetting());
		assertEquals("published", bean.getPublished());
		assertEquals("publisher", bean.getPublisher());
		SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm"); 
		assertEquals(dt.format(d), bean.getReleaseDate());
		assertEquals("shorttitle", bean.getShorttitle());
		assertEquals("tags", bean.getTags());
		assertEquals("thumbnailUrl", bean.getThumbnailUrl());
		assertEquals("title", bean.getTitle());
		assertEquals("username", bean.getUsername());
		assertEquals("video", bean.getVideo());
		assertEquals("videoUrl", bean.getVideoUrl());
		assertEquals(State.OnUpload.getDisplayName(), bean.getState());
	}

}
