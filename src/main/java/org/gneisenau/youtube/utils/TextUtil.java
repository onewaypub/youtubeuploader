package org.gneisenau.youtube.utils;

import org.dozer.DozerBeanMapper;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.to.VideoTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TextUtil {
	
	@Autowired
	private DozerBeanMapper mapper;
	
	public String replacePlaceholder(String text, Video v){
		text = text.replaceAll("%%CATEGORY%%", v.getCategory());
		text = text.replaceAll("%%DESCRIPTION%%", v.getDescription());
		text = text.replaceAll("%%DEVELOPER%%", v.getDeveloper());
		text = text.replaceAll("%%GENRE%%", v.getGenre());
		text = text.replaceAll("%%PUBLISHED%%", v.getPublished());
		text = text.replaceAll("%%PUBLISHER%%", v.getPublisher());
		text = text.replaceAll("%%SHORTTITLE%%", v.getShorttitle());
		text = text.replaceAll("%%TITLE%%", v.getTitle());
		text = text.replaceAll("%%USERNAME%%", v.getUsername());
		text = text.replaceAll("%%RELEASEDATE%%", v.getReleaseDate().toString());
		return text;
	}

	public String replacePlaceholder(String text, VideoTO vDTO){
		Video v = mapper.map(vDTO, Video.class);
		return replacePlaceholder(text, v);
	}

}
