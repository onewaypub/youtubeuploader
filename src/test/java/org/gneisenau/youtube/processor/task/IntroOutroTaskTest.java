package org.gneisenau.youtube.processor.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.gneisenau.youtube.handler.video.FfmpegHandler;
import org.gneisenau.youtube.handler.youtube.exceptions.VideoMergeException;
import org.gneisenau.youtube.handler.youtube.exceptions.VideoTranscodeException;
import org.gneisenau.youtube.message.MailSendService;
import org.gneisenau.youtube.model.UserSettings;
import org.gneisenau.youtube.model.UserSettingsRepository;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.test.util.TestConfigurationContext;
import org.gneisenau.youtube.utils.IOService;
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
public class IntroOutroTaskTest {

	@InjectMocks
	@Autowired
	private IntroOutroTask task;

	@Mock
	private UserSettingsRepository userSettingsDAO;

	@Mock
	private MailSendService mailService;

	@Mock
	private ApplicationEventPublisher publisher;

	@Mock
	private FfmpegHandler videoProcessor;

	@Mock
	protected IOService ioService;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		when(ioService.getTemporaryFolder()).thenReturn("");
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
		File intro = new File("intro.mp4");
		File outro = new File("outro.mp4");
		File srcVideo = new File("dest.mp4");
		FileUtils.copyFile(original, intro);
		FileUtils.copyFile(original, outro);
		FileUtils.copyFile(original, srcVideo);
		Video v = new Video();
		v.setVideo(srcVideo.getAbsolutePath());
		assertEquals(ChainAction.CONTINUE, task.process(v));
		assertFalse(srcVideo.exists());
		intro.delete();
		outro.delete();
	}

	@Test(expected = NullPointerException.class)
	public void testProcessWhenVideoIsNull() throws Exception {
		task.process(null);
	}

	@Test(expected = NullPointerException.class)
	public void testProcessWhenVideoPathIsNull() throws Exception {
		Video v = new Video();
		v.setVideo(null);
		task.process(v);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessWhenVideoPathIsEmpty() throws Exception {
		Video v = new Video();
		v.setVideo("");
		task.process(v);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessWhenVideoNotExist() throws Exception {
		Video v = new Video();
		v.setVideo("test.mp4");
		task.process(v);
	}

	@Test
	public void testProcessWithIOException() throws Exception {
		File original = new File(IntroOutroTaskTest.class.getResource("/SampleVideo_1080x720_1mb.mp4").getPath());
		File intro = new File("intro.mp4");
		File outro = new File("outro.mp4");
		File srcVideo = new File("dest.mp4");
		FileUtils.copyFile(original, intro);
		FileUtils.copyFile(original, outro);
		FileUtils.copyFile(original, srcVideo);
		Video v = new Video();
		v.setVideo(srcVideo.getAbsolutePath());
		doThrow(IOException.class).when(videoProcessor).merge(anyString(), any(File.class), any(File.class),
				any(File.class));
		try {
			task.process(v);
			fail("expected TaskException not thrown");
		} catch (TaskException e) {
			assertTrue(srcVideo.exists());
			intro.delete();
			outro.delete();
			srcVideo.delete();
		}
	}

	@Test
	public void testProcessWithVideoMergeException() throws Exception {
		File original = new File(IntroOutroTaskTest.class.getResource("/SampleVideo_1080x720_1mb.mp4").getPath());
		File intro = new File("intro.mp4");
		File outro = new File("outro.mp4");
		File srcVideo = new File("dest.mp4");
		FileUtils.copyFile(original, intro);
		FileUtils.copyFile(original, outro);
		FileUtils.copyFile(original, srcVideo);
		Video v = new Video();
		v.setVideo(srcVideo.getAbsolutePath());
		doThrow(VideoMergeException.class).when(videoProcessor).merge(anyString(), any(File.class), any(File.class),
				any(File.class));
		try {
			task.process(v);
			fail("expected TaskException not thrown");
		} catch (TaskException e) {
			assertTrue(srcVideo.exists());
			intro.delete();
			outro.delete();
			srcVideo.delete();
		}
	}

	@Test
	public void testProcessWithVideoTranscodeException() throws Exception {
		File original = new File(IntroOutroTaskTest.class.getResource("/SampleVideo_1080x720_1mb.mp4").getPath());
		File intro = new File("intro.mp4");
		File outro = new File("outro.mp4");
		File srcVideo = new File("dest.mp4");
		FileUtils.copyFile(original, intro);
		FileUtils.copyFile(original, outro);
		FileUtils.copyFile(original, srcVideo);
		Video v = new Video();
		v.setVideo(srcVideo.getAbsolutePath());
		doThrow(VideoTranscodeException.class).when(videoProcessor).merge(anyString(), any(File.class), any(File.class),
				any(File.class));
		try {
			task.process(v);
			fail("expected TaskException not thrown");
		} catch (TaskException e) {
			assertTrue(srcVideo.exists());
			intro.delete();
			outro.delete();
			srcVideo.delete();
		}
	}

}
