package org.gneisenau.youtube.handler.youtube;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gneisenau.youtube.model.Video;
import org.springframework.stereotype.Service;

@Service
public class YouTubeUtils {

	public List<String> getTagsList(Video v) {
		List<String> tags = new ArrayList<String>();
		CollectionUtils.addAll(tags, v.getTags().split(","));
		return tags;
	}
	
	public String createDescription(Video v) {
		String desc = v.getDescription() + "\n\nTitel: " + v.getShorttitle() + "\nGenre: " + v.getGenre()
				+ "\nEntwickler: " + v.getDeveloper() + "\nPublisher: " + v.getPublisher() + "\nVeröffentlichung: "
				+ v.getPublished() + "\n\nhttps://www.facebook.com/pages/PeachesLp/781275711939550"
				+ "\nhttps://twitter.com/Peaches_LP";
		return desc;
	}


}
