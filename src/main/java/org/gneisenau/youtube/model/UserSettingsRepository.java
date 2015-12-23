package org.gneisenau.youtube.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.gneisenau.youtube.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserSettingsRepository {

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private SecurityUtil secUtil;

	@SuppressWarnings("unchecked")
	public List<UserSettings> findAll() {
		Query query = em.createQuery("from UserSettings");
		return query.getResultList();
	}

	public void delete(Long id) {
		UserSettings v = findById(id);
		em.remove(v);
	}
	
	public UserSettings findById(Long id){
		UserSettings v = em.find(UserSettings.class, id);
		return v;
	}

	public UserSettings findByUserName(String name){
		Query query = em.createQuery("from UserSettings where username = :username");
		query.setParameter("username", name);
		Object singleResult;
		try{
			singleResult = query.getSingleResult();
		} catch (NoResultException e){
			UserSettings settings = new UserSettings();
			settings.setUsername(name);
			this.persist(settings);
			singleResult = query.getSingleResult();
		}
		return (UserSettings) singleResult;
	}

	public UserSettings findByLoggedInUser(){
		return findByUserName(secUtil.getPrincipal());
	}
	

	public void persist(UserSettings video) {
		em.persist(video);
	}
	
	public void flush(){
		em.flush();
	}
	
}
