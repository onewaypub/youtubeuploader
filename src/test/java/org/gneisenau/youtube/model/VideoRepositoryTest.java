package org.gneisenau.youtube.model;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.List;

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
@ActiveProfiles("dev")
@Transactional
public class VideoRepositoryTest {

	@Autowired
	private VideoRepository repo;

	@Test
	public void testFindById() {
	}

	@Test
	public void testFindAllWaitForUpload() {
		List<Video> list = repo.findAllWaitForUpload();
		assertEquals(1, list.size());
	}

	@Test
	public void testFindAllWaitForPorcessing() {
		List<Video> list = repo.findAllWaitForPorcessing();
		assertEquals(1, list.size());
	}

	@Test
	public void testFindAllWaitForListing() {
		Video v = new Video();
		v.setAgeRestricted(false);
		v.setCategoryId("1");
		v.setChannelId("1");
		v.setDescription("testDesc");
		v.setDeveloper("testDev");
		v.setGenre("testGenre");
		v.setPlaylistId("1");
		v.setPrivacySetting(PrivacySetting.Private);
		v.setPublished("today");
		v.setPublisher("1");
		v.setReleaseDate(DateUtils.addMilliseconds(new Date(System.currentTimeMillis()), 1000));
		v.setShorttitle("1");
		v.setState(State.WaitForListing);
		v.setTags("1");
		v.setTitle("1");
		v.setUsername("1");
		v.setVideo("1");
		v.setYoutubeId("1");
		repo.persist(v);

		List<Video> list = repo.findAllWaitForListing();
		assertEquals(0, list.size());

		v.setReleaseDate(DateUtils.addMilliseconds(new Date(System.currentTimeMillis()), -1000));
		repo.persist(v);

		list = repo.findAllWaitForListing();
		assertEquals(1, list.size());

		v.setReleaseDate(DateUtils.addMilliseconds(new Date(System.currentTimeMillis()), 1));
		repo.persist(v);

		list = repo.findAllWaitForListing();
		assertEquals(1, list.size());

		v.setReleaseDate(DateUtils.addMilliseconds(new Date(System.currentTimeMillis()), -1));
		repo.persist(v);

		list = repo.findAllWaitForListing();
		assertEquals(0, list.size());

		v.setReleaseDate(new Date(System.currentTimeMillis()));
		repo.persist(v);

		list = repo.findAllWaitForListing();
		assertEquals(1, list.size());

	}

}
