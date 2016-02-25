package org.gneisenau.youtube.handler.youtube;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.gneisenau.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YouTubeUtils {

	@Autowired
	private YoutubeHandler youtubeHandler;

	public List<String> getTagsList(Video v) {
		Validate.notNull(v);
		List<String> tags = new ArrayList<String>();
		if (StringUtils.isNotBlank(v.getTags())) {
			String[] split = v.getTags().split(",");
			for(String s : split){
				s = s.trim();
				tags.add(s);
			}
		}
		return tags;
	}

	public String createDescription(Video v) {
		Validate.notNull(v, "No video given");
		String desc = "";
		if (StringUtils.isNotBlank(v.getDescription())) {
			desc = desc + v.getDescription();
		}
		if (StringUtils.isNotBlank(v.getShorttitle())) {
			desc = desc + "\n\nTitel: " + v.getShorttitle();
		}
		if (StringUtils.isNotBlank(v.getGenre())) {
			desc = desc + "\nGenre: " + v.getGenre();
		}
		if (StringUtils.isNotBlank(v.getDeveloper())) {
			desc = desc + "\nEntwickler: " + v.getDeveloper();
		}
		if (StringUtils.isNotBlank(v.getPublisher())) {
			desc = desc + "\nPublisher: " + v.getPublisher();
		}
		if (StringUtils.isNotBlank(v.getPublished())) {
			desc = desc + "\nVer\u00F6ffentlichung: " + v.getPublished();
		}
		desc = desc + "\n\nhttps://www.facebook.com/pages/PeachesLp/781275711939550";
		desc = desc + "\nhttps://twitter.com/Peaches_LP";
		return desc;
	}

	public String getCategoryId(String category) {
		Map<String, String> categories = youtubeHandler.getCategories();
		if(categories == null){
			return category;
		}
		if (categories.containsValue(category)) {
			for (Entry<String, String> e : categories.entrySet()) {
				if (e.getValue().equals(category)) {
					return e.getKey();
				}
			}
		}
		return category;
	}

	public String getPaylistId(String playlist, String username) {
		Validate.notEmpty(username);
		Map<String, String> playlists = youtubeHandler.getPlaylists(username);
		if(playlists == null){
			return playlist;
		}
		if (playlists.containsValue(playlist)) {
			for (Entry<String, String> e : playlists.entrySet()) {
				if (e.getValue().equals(playlist)) {
					return e.getKey();
				}
			}
		}
		return playlist;
	}

	public String getCategoryDisplayName(String youtubeCategoryId) {
		Map<String, String> categories = youtubeHandler.getCategories();
		if(categories == null){
			return youtubeCategoryId;
		}
		if (categories.containsKey(youtubeCategoryId)) {
			for (Entry<String, String> e : categories.entrySet()) {
				if (e.getKey().equals(youtubeCategoryId)) {
					return e.getValue();
				}
			}
		}
		return youtubeCategoryId;
	}

	public String getPlaylistDisplayName(String youtubePlaylistId, String username) {
		Validate.notEmpty(username);
		Map<String, String> playlists = youtubeHandler.getPlaylists(username);
		if(playlists == null){
			return youtubePlaylistId;
		}
		if (playlists.containsKey(youtubePlaylistId)) {
			for (Entry<String, String> e : playlists.entrySet()) {
				if (e.getKey().equals(youtubePlaylistId)) {
					return e.getValue();
				}
			}
		}
		return youtubePlaylistId;
	}

}
