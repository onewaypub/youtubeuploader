package org.gneisenau.youtube.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.gneisenau.youtube.config.SchedulerConfig;
import org.gneisenau.youtube.config.SecurityConfig;
import org.gneisenau.youtube.config.SocialConfig;
import org.gneisenau.youtube.config.WebSocketSecurityConfig;
import org.gneisenau.youtube.test.util.TestConfigurationContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigurationContext.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
@DatabaseSetup("/userconnection_entries.xml")
@ActiveProfiles("dev")
public class UserconnectionRepositoryTest {

	@Autowired
	private UserconnectionRepository repo;

	@Test
	public void testFindByProviderId() {
		List<Userconnection> entry = repo.getListOfUserconnectionForProviderId("youtube");
		assertEquals(1, entry.size());
	}

}
