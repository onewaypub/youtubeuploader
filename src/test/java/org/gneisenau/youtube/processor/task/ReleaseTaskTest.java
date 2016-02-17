package org.gneisenau.youtube.processor.task;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.NotFoundException;
import org.gneisenau.youtube.handler.video.exceptions.UpdateException;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigurationContext.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
@ActiveProfiles("test")
public class ReleaseTaskTest {

	@InjectMocks
	@Autowired
	private ReleaseTask task;

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
	public void testProcess() {
		Video v = new Video();
		v.setYoutubeId("test");
		v.setReleaseDate(new Date());
		v.setUsername("username");
		assertEquals(PublishTask.CONTINUE,task.process(v));
	}

	@Test
	public void testProcessYoutubeIdIsNull() {
		Video v = new Video();
		v.setYoutubeId(null);
		v.setReleaseDate(new Date());
		v.setUsername("username");
		assertEquals(PublishTask.CONTINUE,task.process(v));
	}

	@Test
	public void testProcessYoutubeIdIsEmpty() {
		Video v = new Video();
		v.setYoutubeId("");
		v.setReleaseDate(new Date());
		v.setUsername("username");
		assertEquals(PublishTask.CONTINUE,task.process(v));
	}

	@Test
	public void testProcessReleaseDateIsNull() {
		Video v = new Video();
		v.setYoutubeId("1");
		v.setReleaseDate(null);
		v.setUsername("username");
		assertEquals(PublishTask.CONTINUE,task.process(v));
	}

	@Test(expected = NullPointerException.class)
	public void testProcessVideoIsNull() {
		task.process(null);
	}

	@Test(expected = NullPointerException.class)
	public void testProcessUsernameIsNull() {
		Video v = new Video();
		v.setUsername(null);
		task.process(v);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessUsernameIsEmpty() {
		Video v = new Video();
		v.setUsername("");
		task.process(v);
	}

	@Test
	public void testProcessAuthorizedException() throws AuthorizeException, UpdateException, NotFoundException {
		Video v = new Video();
		v.setYoutubeId("test");
		v.setReleaseDate(new Date());
		v.setUsername("username");
		doThrow(AuthorizeException.class).when(vidUploader).release(anyString(), any(PrivacySetting.class), anyString());
		assertEquals(PublishTask.STOP,task.process(v));
	}

	@Test
	public void testProcessNotFoundException() throws AuthorizeException, UpdateException, NotFoundException {
		Video v = new Video();
		v.setYoutubeId("test");
		v.setReleaseDate(new Date());
		v.setUsername("username");
		doThrow(NotFoundException.class).when(vidUploader).release(anyString(), any(PrivacySetting.class), anyString());
		assertEquals(PublishTask.STOP,task.process(v));
	}

	@Test
	public void testProcessUpdateException() throws AuthorizeException, UpdateException, NotFoundException {
		Video v = new Video();
		v.setYoutubeId("test");
		v.setReleaseDate(new Date());
		v.setUsername("username");
		doThrow(UpdateException.class).when(vidUploader).release(anyString(), any(PrivacySetting.class), anyString());
		assertEquals(PublishTask.STOP,task.process(v));
	}


}
