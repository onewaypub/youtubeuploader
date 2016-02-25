package org.gneisenau.youtube.handler.youtube;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gneisenau.youtube.model.Video;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class YouTubeUtilsTest {
	
	@InjectMocks
	@Autowired
	private YouTubeUtils utils;

	@Mock
	private YoutubeHandler handler;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetTagsList() {
		Video v = new Video();
		List<String> tagsList = utils.getTagsList(v);
		assertEquals(new ArrayList<String>(), tagsList);
		v.setTags("tag1,tag2 , tag3 ");
		tagsList = utils.getTagsList(v);
		assertEquals(3, tagsList.size());
		assertEquals("tag1", tagsList.get(0));
		assertEquals("tag2", tagsList.get(1));
		assertEquals("tag3", tagsList.get(2));
	}

	@Test(expected = NullPointerException.class)
	public void testGetTagsListWithNull() {
		utils.getTagsList(null);
	}

	@Test
	public void testCreateDescription() {
		Video v = new Video();
		String description = utils.createDescription(v);
		assertEquals("\n\nhttps://www.facebook.com/pages/PeachesLp/781275711939550\nhttps://twitter.com/Peaches_LP", description);
		v.setDescription("TestDesc");
		v.setShorttitle("ShortTitle");
		v.setTitle("Title");
		v.setGenre("Genre");
		v.setDeveloper("Developer");
		v.setPublisher("Publisher");
		v.setPublished("Today");
		description = utils.createDescription(v);
		assertEquals("TestDesc\n\nTitel: ShortTitle\nGenre: Genre\nEntwickler: Developer\nPublisher: Publisher\nVeröffentlichung: Today\n\nhttps://www.facebook.com/pages/PeachesLp/781275711939550\nhttps://twitter.com/Peaches_LP", description);
	}

	@Test
	public void testGetCategoryId() {
		Map<String,String> map = new HashMap<String, String>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		when(handler.getCategories()).thenReturn(map);
		assertEquals("key2", utils.getCategoryId("value2"));
		assertEquals("valueNotFound", utils.getCategoryId("valueNotFound"));
	}

	@Test
	public void testGetPaylistId() {
		Map<String,String> map = new HashMap<String, String>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		when(handler.getPlaylists(anyString())).thenReturn(map);
		assertEquals("key2", utils.getPaylistId("value2", "username"));
		assertEquals("valueNotFound", utils.getPaylistId("valueNotFound", "username"));
	}

	@Test
	public void testGetCategoryDisplayName() {
		Map<String,String> map = new HashMap<String, String>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		when(handler.getCategories()).thenReturn(map);
		assertEquals("value2", utils.getCategoryDisplayName("key2"));
		assertEquals("valueNotFound", utils.getCategoryDisplayName("valueNotFound"));
	}

	@Test
	public void testGetPlaylistDisplayName() {
		Map<String,String> map = new HashMap<String, String>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		when(handler.getPlaylists(anyString())).thenReturn(map);
		assertEquals("value2", utils.getPlaylistDisplayName("key2", "username"));
		assertEquals("valueNotFound", utils.getPlaylistDisplayName("valueNotFound", "username"));
	}

}
