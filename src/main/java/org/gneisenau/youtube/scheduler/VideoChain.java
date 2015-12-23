package org.gneisenau.youtube.scheduler;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.gneisenau.youtube.model.Video;
import org.gneisenau.youtube.model.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class VideoChain {
	@Autowired
	private List<AbstractVideoProcessor> videoProcessingChain;
	@Autowired
	private VideoRepository videoDAO;

	@PostConstruct
	public void init() {
		Collections.sort(videoProcessingChain, AnnotationAwareOrderComparator.INSTANCE);
	}

	@Transactional
	public void execute(List<Video> videos) {
		for (Video v : videos) {
			for (AbstractVideoProcessor chainItem : videoProcessingChain) {
				chainItem.process(v);
				videoDAO.persist(v);
				videoDAO.flush();
			}
		}
	}
}
