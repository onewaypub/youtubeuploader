package org.gneisenau.youtube.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.persistence.NoResultException;

import org.gneisenau.youtube.test.util.TestConfigurationContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

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
	public void testGetListOfUserconnectionForProviderId() {
		List<Userconnection> entry = repo.getListOfUserconnectionForProviderId("youtube");
		assertEquals(1, entry.size());
	}

	@Test
	public void testGetListOfUserconnectionForProviderIdWithException() {
		List<Userconnection> entry = repo.getListOfUserconnectionForProviderId("yutube");
		assertEquals(0, entry.size());
	}

	@Test
	public void testGetUserconnectionForProviderId() {
		Userconnection entry = repo.getUserconnectionForProviderId("2", "youtube");
		assertNotNull(entry);
	}

	@Test(expected = NoResultException.class)
	public void testGetUserconnectionForProviderIdWithException() {
		repo.getUserconnectionForProviderId("1", "youtube");
	}

}
