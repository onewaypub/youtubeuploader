package org.gneisenau.youtube.model;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.gneisenau.youtube.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserconnectionRepository {

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private SecurityUtil secUtil;

	public Userconnection getUserconnectionForProviderId(String userId, String providerId) {
		Query query = em.createQuery("from UserSettings where userId = :userId and providerId = :providerId");
		query.setParameter("userId", userId);
		query.setParameter("providerId", providerId);
		Object singleResult = query.getSingleResult();

		return (Userconnection) singleResult;
	}
}
