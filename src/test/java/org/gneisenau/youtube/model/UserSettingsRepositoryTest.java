package org.gneisenau.youtube.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.gneisenau.youtube.test.util.TestConfigurationContext;
import org.gneisenau.youtube.utils.SecurityUtil;
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
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigurationContext.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
@DatabaseSetup("/usersetting_entries.xml")
@ActiveProfiles("test")
public class UserSettingsRepositoryTest {

	@InjectMocks
	@Autowired
	private UserSettingsRepository repo;

	@Mock
	private SecurityUtil secUtil;

	@PersistenceContext
	private EntityManager em;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testFindAll() {
		List<UserSettings> all = repo.findAll();
		assertEquals(2, all.size());
	}

	@Test
	public void testFindById() {
		UserSettings item = repo.findById(2L);
		assertEquals(new Long(2), item.getId());
		assertEquals("test2@localhost", item.getMailTo());
	}

	@Test
	@Transactional
	public void testFindByUserName() {
		UserSettings item = repo.findOrCreateByUserName("test1");
		assertEquals(new Long(1), item.getId());
		assertEquals("test1@localhost", item.getMailTo());
	}

	@Test
	@Transactional
	public void testFindByLoggedInUser() {
		when(secUtil.getPrincipal()).thenReturn("test1");
		UserSettings item = repo.findOrCreateByLoggedInUser();
		assertEquals(new Long(1), item.getId());
		assertEquals("test1@localhost", item.getMailTo());
	}


}
