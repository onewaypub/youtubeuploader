package org.gneisenau.youtube.handler.youtube;

import java.io.IOException;
import java.io.Serializable;

import org.gneisenau.youtube.model.Userconnection;
import org.gneisenau.youtube.model.UserconnectionRepository;
import org.gneisenau.youtube.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.DataStore;

@Service
public class DatabaseDataStoreFactory extends AbstractDataStoreFactory {

	@Autowired
	private UserconnectionRepository userConnectionRepo;
	@Autowired
	private SecurityUtil secUtil;

	@Override
	protected <V extends Serializable> DataStore<V> createDataStore(String appName) throws IOException {
		Userconnection uc = userConnectionRepo.getUserconnectionForProviderId(secUtil.getPrincipal(), "youtube");
		StoredCredential creds = new StoredCredential();
		creds.setAccessToken(uc.getAccessToken());
		creds.setExpirationTimeMilliseconds(uc.getExpireTime());
		creds.setRefreshToken(uc.getRefreshToken());

		DataStore<StoredCredential> dataStore = new AbstractMemoryDataStore<StoredCredential>(this, secUtil.getPrincipal());
		dataStore.set(secUtil.getPrincipal(), creds);
		
		return (DataStore<V>) dataStore;
	}

}
