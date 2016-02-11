package org.gneisenau.youtube.handler.video;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.gneisenau.youtube.handler.youtube.exceptions.VideoMergeException;
import org.gneisenau.youtube.handler.youtube.exceptions.VideoTranscodeException;
import org.gneisenau.youtube.test.util.TestConfigurationContext;
import org.gneisenau.youtube.utils.IOService;
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
@ActiveProfiles("dev")
public class FfmpegHandlerTest {
	
	@InjectMocks
	@Autowired
	private FfmpegHandler handler;
	
	@Mock
	private IOService iosService;
	
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

	@Test
	public void testTranscode() throws ExecuteException, IOException {
        when(iosService.executeCommandLineWithReturn(any(String.class), any(ProgressAwareFFmpegOutputfilterStream.class))).thenReturn("");;
        File srcFile = new File(FilenameUtils.getPath(TestConfigurationContext.class.getResource("/").getPath()) + "SampleVideo_1080x720_1mb.mp4");
        File destFile = new File(FileUtils.getTempDirectoryPath() + System.currentTimeMillis() + ".mp4");
        FileUtils.copyFile(srcFile, destFile);
        File ouputFile = new File(FileUtils.getTempDirectoryPath() + System.currentTimeMillis() + ".mp4");
       
        String transcode = handler.transcode(destFile, ouputFile, 1L);
        assertEquals(ouputFile.getAbsolutePath(), transcode);
        assertFalse(destFile.exists());
        
        ouputFile.delete();
	}

	@Test
	public void testMerge() throws ExecuteException, IOException, VideoTranscodeException, VideoMergeException {
        when(iosService.executeCommandLineWithReturn(any(String.class), any(ProgressAwareFFmpegOutputfilterStream.class))).thenReturn(FileUtils.getTempDirectoryPath() + System.currentTimeMillis() + ".mp4");;
        when(iosService.findFFMPEG()).thenReturn("");
        File intro = new File(FilenameUtils.getPath(TestConfigurationContext.class.getResource("/").getPath()) + "SampleVideo_1080x720_1mb.mp4");
        File outro = new File(FilenameUtils.getPath(TestConfigurationContext.class.getResource("/").getPath()) + "SampleVideo_1080x720_1mb.mp4");
        File srcFile = new File(FilenameUtils.getPath(TestConfigurationContext.class.getResource("/").getPath()) + "SampleVideo_1080x720_1mb.mp4");
        File main = new File(FileUtils.getTempDirectoryPath() + System.currentTimeMillis() + ".mp4");
        FileUtils.copyFile(srcFile, main);
        File target = new File(FileUtils.getTempDirectoryPath() + System.currentTimeMillis() + ".mp4");
        handler.merge(target.getAbsolutePath(), intro, main, outro);
        assertFalse(main.exists());
        assertTrue(intro.exists());
        assertTrue(outro.exists());
	}

}
