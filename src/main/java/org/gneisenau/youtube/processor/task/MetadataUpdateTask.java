package org.gneisenau.youtube.processor.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.NotFoundException;
import org.gneisenau.youtube.handler.video.exceptions.UpdateException;
import org.gneisenau.youtube.handler.youtube.VideoHandler;
import org.gneisenau.youtube.model.PrivacySetting;
import org.gneisenau.youtube.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 3)
public class MetadataUpdateTask extends AbstractYoutubeTask {

	@Autowired
	protected VideoHandler vidUploader;

	@Autowired
	public MetadataUpdateTask(ApplicationEventPublisher publisher) {
		super(publisher);
	}

	@Override
	public int process(Video v) {
		if (StringUtils.isBlank(v.getYoutubeId())) {
			return CONTINUE;
		}
		try {
			List<String> tags = new ArrayList<String>();
			CollectionUtils.addAll(tags, v.getTags().split(","));
			vidUploader.updateMetadata(PrivacySetting.Private, v.getYoutubeId(), tags, v.getTitle(), createDescription(v), v.getCategoryId(), v.getCategoryId(), v.getUsername(), false);
		}  catch (AuthorizeException e) {
			handleError(v, "Authorisierung bei Youtube fehlgeschlagen", e);
			return VideoTask.STOP;
		} catch (UpdateException e) {
			handleError(v, "Authorisierung bei Youtube fehlgeschlagen", e);
			return VideoTask.STOP;
		} catch (NotFoundException e) {
			handleError(v, "Video konnte nicht mehr gefunden werden", e);
			return VideoTask.STOP;
		} 
		return VideoTask.CONTINUE;
	}
	
	private String createDescription(Video v) {
		String desc = v.getDescription() + "\n\nTitel: " + v.getShorttitle() + "\nGenre: " + v.getGenre()
				+ "\nEntwickler: " + v.getDeveloper() + "\nPublisher: " + v.getPublisher() + "\nVeröffentlichung: "
				+ v.getPublished() + "\n\nhttps://www.facebook.com/pages/PeachesLp/781275711939550"
				+ "\nhttps://twitter.com/Peaches_LP";
		return desc;
	}


}
