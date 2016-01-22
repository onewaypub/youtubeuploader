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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.Maps;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.store.AbstractDataStore;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.DataStoreUtils;

class AbstractMemoryDataStore extends AbstractDataStore<StoredCredential> {

  /** Lock on access to the store. */
  private final Lock lock = new ReentrantLock();

  /** Data store map from the key to the value. */
  HashMap<String, StoredCredential> keyValueMap = Maps.newHashMap();

  /**
   * @param dataStoreFactory data store factory
   * @param id data store ID
   */
  protected AbstractMemoryDataStore(DataStoreFactory dataStoreFactory, String id) {
    super(dataStoreFactory, id);
  }

  public final Set<String> keySet() throws IOException {
    lock.lock();
    try {
      return Collections.unmodifiableSet(keyValueMap.keySet());
    } finally {
      lock.unlock();
    }
  }

  public final Collection<StoredCredential> values() throws IOException {
    lock.lock();
    try {
      return keyValueMap.values();
    } finally {
      lock.unlock();
    }
  }

  public final StoredCredential get(String key) throws IOException {
    if (key == null) {
      return null;
    }
    lock.lock();
    try {
      return keyValueMap.get(key);
    } finally {
      lock.unlock();
    }
  }

  public final DataStore<StoredCredential> set(String key, StoredCredential value) throws IOException {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(value);
    lock.lock();
    try {
      keyValueMap.put(key, value);
      save();
    } finally {
      lock.unlock();
    }
    return this;
  }

  public DataStore<StoredCredential> delete(String key) throws IOException {
    if (key == null) {
      return this;
    }
    lock.lock();
    try {
      keyValueMap.remove(key);
      save();
    } finally {
      lock.unlock();
    }
    return this;
  }

  public final DataStore<StoredCredential> clear() throws IOException {
    lock.lock();
    try {
      keyValueMap.clear();
      save();
    } finally {
      lock.unlock();
    }
    return this;
  }

  @Override
  public boolean containsKey(String key) throws IOException {
    if (key == null) {
      return false;
    }
    lock.lock();
    try {
      return keyValueMap.containsKey(key);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean containsValue(StoredCredential value) throws IOException {
    if (value == null) {
      return false;
    }
    lock.lock();
    try {
      return keyValueMap.containsValue(value);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean isEmpty() throws IOException {
    lock.lock();
    try {
      return keyValueMap.isEmpty();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public int size() throws IOException {
    lock.lock();
    try {
      return keyValueMap.size();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Persist the key-value map into storage at the end of {@link #set}, {@link #delete(String)}, and
   * {@link #clear()}.
   */
  @SuppressWarnings("unused")
  void save() throws IOException {
  }

  @Override
  public String toString() {
    return DataStoreUtils.toString(this);
  }
}
