package org.gneisenau.youtube.utils;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.model.State;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.test.util.TestConfigurationContext;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class TextUtilTest {

	@Test
	public void testReplacePlaceholderStringVideo() {
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
		
		SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		String text = "%%DESCRIPTION%%,%%DEVELOPER%%,%%GENRE%%,%%PUBLISHED%%,"
				+ "%%PUBLISHER%%,%%SHORTTITLE%%,%%TITLE%%,%%RELEASEDATE%%";
		String targettext = "description,developer,genre,published,"
				+ "publisher,shorttitle,title," + dt.format(d);

		TextUtil util = new TextUtil();
		assertEquals(targettext, util.replacePlaceholder(text, v));
	}


}
