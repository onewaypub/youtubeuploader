/*
 * Copyright (c) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.gneisenau.youtube.handler.youtube;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.dozer.DozerBeanMapper;
import org.gneisenau.youtube.model.Userconnection;
import org.gneisenau.youtube.model.UserconnectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.Maps;
import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.DataStore;

@Service
public class DatabaseDataStoreFactory extends AbstractDataStoreFactory {

	private static final Logger LOGGER = Logger.getLogger(DatabaseDataStoreFactory.class.getName());

	/** Directory to store data. */
	private final UserconnectionRepository repo;

	/**
	 * @param dataDirectory
	 *            data directory
	 */
	public DatabaseDataStoreFactory(UserconnectionRepository dataDirectory) throws IOException {
		this.repo = dataDirectory;
	}

	@Override
	protected <V extends Serializable> DataStore<V> createDataStore(String appname) throws IOException {
		return (DataStore<V>) new DatabaseDataStore(repo, appname);
	}


}
