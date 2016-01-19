package org.gneisenau.youtube.handler.youtube;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;

public class DatabaseDataStoreFactory extends AbstractDataStoreFactory {

	@Override
	protected <V extends Serializable> DataStore<V> createDataStore(String id) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	static class DatabaseDataStore<V extends Serializable> extends AbstractMemoryDataStore<V> {

		protected DatabaseDataStore(DataStoreFactory dataStoreFactory, String id) {
			super(dataStoreFactory, id);
			// TODO Auto-generated constructor stub
		}
	}

}
