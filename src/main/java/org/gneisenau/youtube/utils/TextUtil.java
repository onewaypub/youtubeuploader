package org.gneisenau.youtube.utils;

import java.text.SimpleDateFormat;

import org.dozer.DozerBeanMapper;
import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.to.VideoTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TextUtil {

	public String replacePlaceholder(String text, Video v) {
		SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		// text = text.replaceAll("%%CATEGORY%%", v.getCategory());
		text = text.replaceAll("%%DESCRIPTION%%", v.getDescription());
		text = text.replaceAll("%%DEVELOPER%%", v.getDeveloper());
		text = text.replaceAll("%%GENRE%%", v.getGenre());
		text = text.replaceAll("%%PUBLISHED%%", v.getPublished());
		text = text.replaceAll("%%PUBLISHER%%", v.getPublisher());
		text = text.replaceAll("%%SHORTTITLE%%", v.getShorttitle());
		text = text.replaceAll("%%TITLE%%", v.getTitle());
		text = text.replaceAll("%%USERNAME%%", v.getUsername());
		text = text.replaceAll("%%RELEASEDATE%%", dt.format(v.getReleaseDate()));
		return text;
	}


}
