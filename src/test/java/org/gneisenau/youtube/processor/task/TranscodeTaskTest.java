package org.gneisenau.youtube.processor.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;
import org.gneisenau.youtube.handler.video.FfmpegHandler;
import org.gneisenau.youtube.message.MailSendService;
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
public class TranscodeTaskTest {

	@InjectMocks
	@Autowired
	private TranscodeTask task;

	@Mock
	private UserSettingsRepository userSettingsDAO;

	@Mock
	private MailSendService mailService;

	@Mock
	private ApplicationEventPublisher publisher;

	@Mock
	private FfmpegHandler videoProcessor;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		UserSettings s = new UserSettings();
		s.setNotifyErrorState(true);
		s.setNotifyProcessedState(true);
		s.setNotifyReleaseState(true);
		s.setNotifyUploadState(true);
		when(userSettingsDAO.findOrCreateByUserName(anyString())).thenReturn(s);
	}

	@Test
	public void testProcess() throws Exception {
		File original = new File(IntroOutroTaskTest.class.getResource("/SampleVideo_1080x720_1mb.mp4").getPath());
		File srcVideo = new File("dest.mp4");
		FileUtils.copyFile(original, srcVideo);
		Video v = new Video();
		v.setVideo(srcVideo.getAbsolutePath());
		v.setId(1L);
		when(videoProcessor.transcode(any(File.class), any(File.class), any(Long.class))).thenReturn("1");
		assertEquals(ChainAction.CONTINUE, task.process(v));
		assertFalse(srcVideo.exists());
		assertEquals(new File("dest_transcoded.mp4").getAbsolutePath(), v.getVideo());
	}

	@Test(expected = NullPointerException.class)
	public void testProcessVideoIsNull() throws Exception {
		Video v = new Video();
		v.setVideo(null);
		v.setId(1L);
		task.process(v);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessVideoIsEmpty() throws Exception {
		Video v = new Video();
		v.setVideo("");
		v.setId(1L);
		task.process(v);
	}

	@Test
	public void testProcessVideoNotExist() throws Exception {
		File srcVideo = new File("dest.mp4");
		Video v = new Video();
		v.setVideo(srcVideo.getAbsolutePath());
		v.setId(1L);
		try {
			task.process(v);
			fail("expected TaskException not thrown");
		} catch (TaskException e) {
			assertFalse(srcVideo.exists());
			assertEquals(new File("dest.mp4").getAbsolutePath(), v.getVideo());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessExecuteException() throws Exception {
		File original = new File(IntroOutroTaskTest.class.getResource("/SampleVideo_1080x720_1mb.mp4").getPath());
		File srcVideo = new File("dest.mp4");
		FileUtils.copyFile(original, srcVideo);
		Video v = new Video();
		v.setVideo(srcVideo.getAbsolutePath());
		v.setId(1L);
		when(videoProcessor.transcode(any(File.class), any(File.class), any(Long.class)))
				.thenThrow(ExecuteException.class);
		try {
			task.process(v);
			fail("expected TaskException not thrown");
		} catch (TaskException e) {
			assertTrue(srcVideo.exists());
			srcVideo.delete();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessIOException() throws Exception {
		File original = new File(IntroOutroTaskTest.class.getResource("/SampleVideo_1080x720_1mb.mp4").getPath());
		File srcVideo = new File("dest.mp4");
		FileUtils.copyFile(original, srcVideo);
		Video v = new Video();
		v.setVideo(srcVideo.getAbsolutePath());
		v.setId(1L);
		when(videoProcessor.transcode(any(File.class), any(File.class), any(Long.class))).thenThrow(IOException.class);
		try {
			task.process(v);
			fail("expected TaskException not thrown");
		} catch (TaskException e) {
			assertTrue(srcVideo.exists());
			srcVideo.delete();
		}
	}

}
