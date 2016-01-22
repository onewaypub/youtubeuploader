package org.gneisenau.youtube.handler.youtube;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import org.dozer.DozerBeanMapper;
import org.gneisenau.youtube.model.Userconnection;
import org.gneisenau.youtube.model.UserconnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.Maps;

@Service
public class DatabaseDataStore extends AbstractMemoryDataStore {

	/** File to store data. */
	private final List<Userconnection> credentials;
	@Autowired
	private final UserconnectionRepository dataDirectory;
	
	DatabaseDataStore(UserconnectionRepository repository, String id)
			throws IOException {
		super(new DatabaseDataStoreFactory(repository), id);
		this.dataDirectory = repository;
		this.credentials = repository.getListOfUserconnectionForProviderId(id);
		// create new file (if necessary)
		if (credentials == null || credentials.size() == 0) {
			keyValueMap = Maps.newHashMap();
		} else {
			for (Userconnection uc : credentials) {
				StoredCredential c = new StoredCredential();
				c.setAccessToken(uc.getAccessToken());
				c.setExpirationTimeMilliseconds(uc.getExpireTime());
				c.setRefreshToken(uc.getRefreshToken());
				keyValueMap.put(uc.getUserId(), c);
			}
		}
	}

	@Transactional
	@Override
	void save() throws IOException {
		for(Entry<String, StoredCredential> entry : keyValueMap.entrySet()){
			Userconnection c = new Userconnection();
			c.setUserId(entry.getKey());
			c.setProviderId(getId());
			c.setRank(1);
			c.setAccessToken(entry.getValue().getAccessToken());
			c.setRefreshToken(entry.getValue().getRefreshToken());
			c.setExpireTime(entry.getValue().getExpirationTimeMilliseconds());
			Userconnection persistedCreds = dataDirectory.getUserconnectionForProviderId(c.getUserId(), getId());
			if(persistedCreds == null){
				dataDirectory.persist(persistedCreds);
			} else {
				DozerBeanMapper mapper = new DozerBeanMapper();
				mapper.map(c, persistedCreds);
				dataDirectory.persist(persistedCreds);
			}
		}
	}

	@Override
	public DatabaseDataStoreFactory getDataStoreFactory() {
		return (DatabaseDataStoreFactory) super.getDataStoreFactory();
	}
}
