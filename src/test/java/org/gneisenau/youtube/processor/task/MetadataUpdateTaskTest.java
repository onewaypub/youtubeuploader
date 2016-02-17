package org.gneisenau.youtube.processor.task;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.NotFoundException;
import org.gneisenau.youtube.handler.video.exceptions.UpdateException;
import org.gneisenau.youtube.handler.youtube.VideoHandler;
import org.gneisenau.youtube.handler.youtube.YouTubeUtils;
import org.gneisenau.youtube.handler.youtube.YoutubeHandler;
import org.gneisenau.youtube.handler.youtube.exceptions.VideoMergeException;
import org.gneisenau.youtube.message.MailSendService;
import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.model.UserSettings;
import org.gneisenau.youtube.model.UserSettingsRepository;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.test.util.TestConfigurationContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
public class MetadataUpdateTaskTest {

	@InjectMocks
	@Autowired
	private MetadataUpdateTask task;

	@InjectMocks
	@Autowired
	private YouTubeUtils youtubeUtils;

	@Mock
	protected VideoHandler vidUploader;

	@Mock
	private YoutubeHandler youtubeHandler;

	@Mock
	private UserSettingsRepository userSettingsDAO;

	@Mock
	private MailSendService mailService;

	@Mock
	private ApplicationEventPublisher publisher;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		UserSettings s = new UserSettings();
		s.setNotifyErrorState(true);
		s.setNotifyProcessedState(true);
		s.setNotifyReleaseState(true);
		s.setNotifyUploadState(true);
		when(userSettingsDAO.findByUserName(anyString())).thenReturn(s);
	}

	@Test
	public void testProcess() {
		Video v = new Video();
		v.setPrivacySetting(PrivacySetting.Private);
		v.setYoutubeId("test");
		v.setTags("tag1,tag2");
		v.setTitle("title");
		v.setDescription("descrition");
		v.setChannelId("channelId");
		v.setCategoryId("catagory");
		v.setUsername("username");
		v.setShorttitle("shorttitle");
		v.setGenre("genre");
		v.setDeveloper("developer");
		v.setPublisher("publisher");
		v.setPublished("12.12.2015");

		assertEquals(VideoTask.CONTINUE, task.process(v));
	}

	@Test
	public void testProcessWithNullYoutubeId() {
		Video v = new Video();
		v.setPrivacySetting(PrivacySetting.Private);
		v.setYoutubeId(null);
		v.setTags("tag1,tag2");
		v.setTitle("title");
		v.setDescription("descrition");
		v.setChannelId("channelId");
		v.setCategoryId("catagory");
		v.setUsername("username");
		v.setShorttitle("shorttitle");
		v.setGenre("genre");
		v.setDeveloper("developer");
		v.setPublisher("publisher");
		v.setPublished("12.12.2015");

		assertEquals(VideoTask.CONTINUE, task.process(v));
	}

	@Test
	public void testProcessWithEmptyYoutubeId() {
		Video v = new Video();
		v.setPrivacySetting(PrivacySetting.Private);
		v.setYoutubeId("");
		v.setTags("tag1,tag2");
		v.setTitle("title");
		v.setDescription("descrition");
		v.setChannelId("channelId");
		v.setCategoryId("catagory");
		v.setUsername("username");
		v.setShorttitle("shorttitle");
		v.setGenre("genre");
		v.setDeveloper("developer");
		v.setPublisher("publisher");
		v.setPublished("12.12.2015");

		assertEquals(VideoTask.CONTINUE, task.process(v));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessWithEmptyUsername() {
		Video v = new Video();
		v.setPrivacySetting(PrivacySetting.Private);
		v.setYoutubeId("1");
		v.setTags("tag1,tag2");
		v.setTitle("title");
		v.setDescription("descrition");
		v.setChannelId("channelId");
		v.setCategoryId("catagory");
		v.setUsername("");
		v.setShorttitle("shorttitle");
		v.setGenre("genre");
		v.setDeveloper("developer");
		v.setPublisher("publisher");
		v.setPublished("12.12.2015");

		task.process(v);
	}

	@Test(expected = NullPointerException.class)
	public void testProcessWithNullUsername() {
		Video v = new Video();
		v.setPrivacySetting(PrivacySetting.Private);
		v.setYoutubeId("1");
		v.setTags("tag1,tag2");
		v.setTitle("title");
		v.setDescription("descrition");
		v.setChannelId("channelId");
		v.setCategoryId("catagory");
		v.setUsername(null);
		v.setShorttitle("shorttitle");
		v.setGenre("genre");
		v.setDeveloper("developer");
		v.setPublisher("publisher");
		v.setPublished("12.12.2015");

		task.process(v);
	}

	@Test(expected = NullPointerException.class)
	public void testProcessWithNullVideo() {
		task.process(null);
	}

	@Test
	public void testProcessWithOnlyMandatory() {
		Video v = new Video();
		v.setPrivacySetting(PrivacySetting.Private);
		v.setYoutubeId("test");
		v.setTitle("title");
		v.setUsername("username");

		assertEquals(VideoTask.CONTINUE, task.process(v));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessWithAuthorizeException() throws AuthorizeException, UpdateException, NotFoundException {
		Video v = new Video();
		v.setPrivacySetting(PrivacySetting.Private);
		v.setYoutubeId("test");
		v.setTitle("title");
		v.setUsername("username");
		doThrow(AuthorizeException.class).when(vidUploader).updateMetadata(any(PrivacySetting.class), anyString(),
				any(List.class), anyString(), anyString(), anyString(), anyString(), anyString(), eq(false));
		assertEquals(VideoTask.STOP, task.process(v));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessWithUpdateException() throws AuthorizeException, UpdateException, NotFoundException {
		Video v = new Video();
		v.setPrivacySetting(PrivacySetting.Private);
		v.setYoutubeId("test");
		v.setTitle("title");
		v.setUsername("username");
		doThrow(UpdateException.class).when(vidUploader).updateMetadata(any(PrivacySetting.class), anyString(),
				any(List.class), anyString(), anyString(), anyString(), anyString(), anyString(), eq(false));
		assertEquals(VideoTask.STOP, task.process(v));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessWithNotFoundException() throws AuthorizeException, UpdateException, NotFoundException {
		Video v = new Video();
		v.setPrivacySetting(PrivacySetting.Private);
		v.setYoutubeId("test");
		v.setTitle("title");
		v.setUsername("username");
		doThrow(NotFoundException.class).when(vidUploader).updateMetadata(any(PrivacySetting.class), anyString(),
				any(List.class), anyString(), anyString(), anyString(), anyString(), anyString(), eq(false));
		assertEquals(VideoTask.STOP, task.process(v));
	}

}
