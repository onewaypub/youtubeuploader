package org.gneisenau.youtube.handler.youtube;

import static org.junit.Assert.*;

import org.gneisenau.youtube.handler.youtube.util.YoutubeFactory;
import org.gneisenau.youtube.test.util.TestConfigurationContext;
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
public class YoutubeHandlerTest {

	@InjectMocks
	@Autowired
	private YoutubeHandler handler;

	@Mock
	private YoutubeFactory youtubeFactory;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetPlaylists() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCategories() {
		fail("Not yet implemented");
	}

}
