package org.gneisenau.youtube.handler.youtube;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.gneisenau.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YouTubeUtils {

	@Autowired
	private YoutubeHandler youtubeHandler;

	public List<String> getTagsList(Video v) {
		List<String> tags = new ArrayList<String>();
		if(StringUtils.isNotBlank(v.getTags())){
			CollectionUtils.addAll(tags, v.getTags().split(","));
		}
		return tags;
	}
	
	public String createDescription(Video v) {
		String desc = v.getDescription() + "\n\nTitel: " + v.getShorttitle() + "\nGenre: " + v.getGenre()
				+ "\nEntwickler: " + v.getDeveloper() + "\nPublisher: " + v.getPublisher() + "\nVeröffentlichung: "
				+ v.getPublished() + "\n\nhttps://www.facebook.com/pages/PeachesLp/781275711939550"
				+ "\nhttps://twitter.com/Peaches_LP";
		return desc;
	}

	public String getCategoryId(String category) {
		Map<String, String> categories = youtubeHandler.getCategories();
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
		Map<String, String> playlists = youtubeHandler.getPlaylists(username);
		if (playlists.containsValue(playlist)) {
			for (Entry<String, String> e : playlists.entrySet()) {
				if (e.getValue().equals(playlist)) {
					return e.getKey();
				}
			}
		}
		return playlist;
	}
	
	public String getCategoryDisplayName(String youtubeCategoryId){
		Map<String, String> categories = youtubeHandler.getCategories();
		if (categories.containsKey(youtubeCategoryId)) {
			for (Entry<String, String> e : categories.entrySet()) {
				if (e.getKey().equals(youtubeCategoryId)) {
					return e.getValue();
				}
			}
		}
		return youtubeCategoryId;
	}


	public String getPlaylistDisplayName(String youtubePlaylistId, String username){
		Map<String, String> playlists = youtubeHandler.getPlaylists(username);
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
