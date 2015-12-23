package org.gneisenau.youtube.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class VideoRepository {

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public List<Video> findAll() {
		Query query = em.createQuery("from Video");
		return query.getResultList();
	}

	public void delete(Long id) {
		Video v = findById(id);
		em.remove(v);
	}
	
	public Video findById(Long id){
		Video v = em.find(Video.class, id);
		return v;
	}

	public void persist(Video video) {
		em.persist(video);
	}
	
	public void flush(){
		em.flush();
	}
	
	@SuppressWarnings("unchecked")
	public List<Video> findAllWaitForUpload(){
		Query query = em.createQuery("from Video where state = :state");
		query.setParameter("state", State.WaitForUpload);
		return query.getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<Video> findAllWaitForPorcessing(){
		Query query = em.createQuery("from Video where state = :state");
		query.setParameter("state", State.WaitForProcessing);
		return query.getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<Video> findAllWaitForListing(){
		Query query = em.createQuery("from Video where state = :state and releaseDate >= CURDATE()");
		query.setParameter("state", State.WaitForListing);
		List<Video> videoList = query.getResultList();
		return videoList;
	}
}
