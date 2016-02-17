package org.gneisenau.youtube.processor.task;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.youtube.VideoHandler;
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
public class PlaylistUpdateTaskTest {

	@InjectMocks
	@Autowired
	private PlaylistUpdateTask task;

	@Mock
	private UserSettingsRepository userSettingsDAO;

	@Mock
	private MailSendService mailService;

	@Mock
	private ApplicationEventPublisher publisher;

	@Mock
	private VideoHandler vidUploader;

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
	public void testProcess() throws IOException, AuthorizeException {
		Video v = new Video();
		v.setPlaylistId("playlistId");
		v.setYoutubeId("test");
		v.setUsername("username");
		when(vidUploader.insertPlaylistItem(anyString(), anyString(), anyString())).thenReturn("1");
		assertEquals(VideoTask.CONTINUE, task.process(v));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessWithAuthorizeExcpetion() throws IOException, AuthorizeException {
		Video v = new Video();
		v.setPlaylistId("playlistId");
		v.setYoutubeId("test");
		v.setUsername("username");
		when(vidUploader.insertPlaylistItem(anyString(), anyString(), anyString())).thenThrow(AuthorizeException.class);
		assertEquals(VideoTask.STOP, task.process(v));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessWithIOExcpetion() throws IOException, AuthorizeException {
		Video v = new Video();
		v.setPlaylistId("playlistId");
		v.setYoutubeId("test");
		v.setUsername("username");
		when(vidUploader.insertPlaylistItem(anyString(), anyString(), anyString())).thenThrow(IOException.class);
		assertEquals(VideoTask.STOP, task.process(v));
	}

	@Test(expected = NullPointerException.class)
	public void testProcessVideoIsNull() {
		task.process(null);
	}

	@Test(expected = NullPointerException.class)
	public void testProcessUsernameIsNull() {
		Video v = new Video();
		v.setPlaylistId("playlistId");
		v.setYoutubeId("test");
		v.setUsername(null);
		task.process(v);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessUsernameIsEmpty() {
		Video v = new Video();
		v.setPlaylistId("playlistId");
		v.setYoutubeId("test");
		v.setUsername("");
		task.process(v);
	}

	@Test
	public void testProcessPlayListIdIsNull() {
		Video v = new Video();
		v.setPlaylistId(null);
		v.setYoutubeId("test");
		v.setUsername("username");
		assertEquals(VideoTask.CONTINUE, task.process(v));
	}

	@Test
	public void testProcessPlayListIdIsEmpty() {
		Video v = new Video();
		v.setPlaylistId("");
		v.setYoutubeId("test");
		v.setUsername("username");
		assertEquals(VideoTask.CONTINUE, task.process(v));
	}

	@Test
	public void testProcessYoutubeIdIsNull() {
		Video v = new Video();
		v.setPlaylistId("playlistId");
		v.setYoutubeId(null);
		v.setUsername("username");
		assertEquals(VideoTask.CONTINUE, task.process(v));
	}

	@Test
	public void testProcessYoutubeIdIsEmpty() {
		Video v = new Video();
		v.setPlaylistId("playlistId");
		v.setYoutubeId("");
		v.setUsername("username");
		assertEquals(VideoTask.CONTINUE, task.process(v));
	}


}
