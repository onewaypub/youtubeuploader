package org.gneisenau.youtube.model;

import static org.junit.Assert.assertEquals;

import java.sql.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.time.DateUtils;
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
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfigurationContext.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
@DatabaseSetup("/video_entries.xml")
@ActiveProfiles("test")
public class VideoRepositoryTest {

	@Autowired
	private VideoRepository repo;

	@PersistenceContext
	private EntityManager em;

	@Test
	@Transactional
	public void testFindAllWaitForUpload() {
		Video v = repo.findById(1L);
		v.setState(State.WaitForUpload);
		List<Video> list = repo.findAllWaitForUpload();
		assertEquals(1, list.size());
		assertEquals(new Long(1), list.get(0).getId());
		
		v = repo.findById(1L);
		v.setState(State.WaitForProcessing);
		list = repo.findAllWaitForUpload();
		assertEquals(0, list.size());
	}

	@Test
	@Transactional
	public void testFindAllWaitForPorcessing() {
		Video v = repo.findById(1L);
		v.setState(State.WaitForProcessing);
		List<Video> list = repo.findAllWaitForPorcessing();
		assertEquals(2, list.size());
		assertEquals(new Long(1), list.get(0).getId());
		assertEquals(new Long(2), list.get(1).getId());
		
		v = repo.findById(1L);
		v.setState(State.WaitForUpload);
		list = repo.findAllWaitForPorcessing();
		assertEquals(1, list.size());
		assertEquals(new Long(2), list.get(0).getId());
	}

	@Test
	@Transactional
	public void testFindAllWaitForListing() {
		Video v = repo.findById(1L);
		v.setReleaseDate(DateUtils.addMilliseconds(new Date(System.currentTimeMillis()), 120000));
		v.setState(State.WaitForListing);
		repo.persist(v);
		repo.flush();
		
		List<Video> list = repo.findAllWaitForListing();
		assertEquals(0, list.size());

		v.setReleaseDate(DateUtils.addMilliseconds(new Date(System.currentTimeMillis()), -120000));
		repo.persist(v);
		repo.flush();

		list = repo.findAllWaitForListing();
		assertEquals(1, list.size());

		v.setReleaseDate(DateUtils.addMilliseconds(new Date(System.currentTimeMillis()), 1));
		repo.persist(v);
		repo.flush();

		list = repo.findAllWaitForListing();
		assertEquals(1, list.size());

		v.setReleaseDate(DateUtils.addMilliseconds(new Date(System.currentTimeMillis()), -1));
		repo.persist(v);
		repo.flush();

		list = repo.findAllWaitForListing();
		assertEquals(1, list.size());

		v.setReleaseDate(new Date(System.currentTimeMillis()));
		repo.persist(v);
		repo.flush();

		list = repo.findAllWaitForListing();
		assertEquals(1, list.size());

	}

}
