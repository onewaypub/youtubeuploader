package org.gneisenau.youtube.handler.youtube;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.dozer.DozerBeanMapper;
import org.gneisenau.youtube.model.Userconnection;
import org.gneisenau.youtube.model.UserconnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.Maps;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreUtils;

@Component
public class DatabaseDataStore implements DataStore<StoredCredential> {

	@Autowired
	private UserconnectionRepository dataDirectory;
	private String id = "youtube";

	public DatabaseDataStore() {
		super();
	}

	@PostConstruct
	public void init() throws IOException {
		List<Userconnection> credentials = dataDirectory.getListOfUserconnectionForProviderId(id);
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

	/** Data store map from the key to the value. */
	HashMap<String, StoredCredential> keyValueMap = Maps.newHashMap();

	public final Set<String> keySet() throws IOException {
		return Collections.unmodifiableSet(keyValueMap.keySet());
	}

	public final Collection<StoredCredential> values() throws IOException {
		return keyValueMap.values();
	}

	public final StoredCredential get(String key) throws IOException {
		return keyValueMap.get(key);
	}

	public final DataStore<StoredCredential> set(String key, StoredCredential value) throws IOException {
		keyValueMap.put(key, value);
		save();
		return this;
	}

	public DataStore<StoredCredential> delete(String key) throws IOException {
		keyValueMap.remove(key);
		save();
		return this;
	}

	public final DataStore<StoredCredential> clear() throws IOException {
		keyValueMap.clear();
		save();
		return this;
	}

	@Override
	public boolean containsKey(String key) throws IOException {
		return keyValueMap.containsKey(key);
	}

	@Override
	public boolean containsValue(StoredCredential value) throws IOException {
		return keyValueMap.containsValue(value);
	}

	@Override
	public boolean isEmpty() throws IOException {
		return keyValueMap.isEmpty();
	}

	@Override
	public int size() throws IOException {
		return keyValueMap.size();
	}

	@Override
	public String toString() {
		return DataStoreUtils.toString(this);
	}

	@Transactional
	void save() {
		for (Entry<String, StoredCredential> entry : keyValueMap.entrySet()) {
			Userconnection c = new Userconnection();
			c.setUserId(entry.getKey());
			c.setProviderId(getId());
			c.setRank(1);
			c.setAccessToken(entry.getValue().getAccessToken());
			c.setRefreshToken(entry.getValue().getRefreshToken());
			c.setExpireTime(entry.getValue().getExpirationTimeMilliseconds());
			Userconnection persistedCreds = dataDirectory.getUserconnectionForProviderId(c.getUserId(), getId());
			if (persistedCreds == null) {
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
		return null;
	}

	@Override
	public String getId() {
		return id;
	}

}
