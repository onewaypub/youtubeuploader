package org.gneisenau.youtube.handler.youtube.util;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.collections4.list.UnmodifiableList;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.common.collect.Lists;

/**
 * Shared class used by every sample. Contains methods for authorizing a user
 * and caching credentials.
 */
@Service
public class Auth {

	public static final String APP_NAME = "YoutubeUploader";
	public static final String APPROVAL_PROMPT_FORCE = "force";
	public static final String ACCESS_TYPE_OFFLINE = "offline";
	private static final String CREDENTIALS_DIRECTORY = ".oauth-credentials";

	@Value("${tomcat.home.dir}")
	private String tomcatHomeDir;
	@Autowired
	private HttpTransport httpTransport;
	@Autowired
	private JsonFactory jsonFactory;
	@Value("${youtube.client.secret}")
	private String clientSecretJson;

	public static final List<String> SCOPES = new UnmodifiableList<String>(
			Lists.newArrayList(YouTubeScopes.YOUTUBE, YouTubeScopes.YOUTUBE_UPLOAD));

	public synchronized Credential authorize(String credentialDatastore, String username) throws AuthorizeException {

		GoogleAuthorizationCodeFlow flow = createGoogleAuthorizationCodeFlow(credentialDatastore);
		Credential credential;
		try {
			credential = flow.loadCredential(username);
		} catch (IOException e) {
			throw new AuthorizeException(e);
		}
		if (credential != null && (credential.getRefreshToken() != null || credential.getExpiresInSeconds() > 60)) {
			return credential;
		} else {
			return null;
		}
	}

	private GoogleClientSecrets initializeClientSecrets() throws AuthorizeException {
		Reader clientSecretReader = new StringReader(clientSecretJson);

		GoogleClientSecrets clientSecrets;
		try {
			clientSecrets = GoogleClientSecrets.load(jsonFactory, clientSecretReader);
		} catch (IOException e) {
			throw new AuthorizeException(e);
		}
		return clientSecrets;
	}

	public GoogleAuthorizationCodeFlow createGoogleAuthorizationCodeFlow(String credentialDatastore)
			throws AuthorizeException {
		GoogleClientSecrets clientSecrets = initializeClientSecrets();
		// This creates the credentials datastore at
		// ~/.oauth-credentials/${credentialDatastore}
		FileDataStoreFactory fileDataStoreFactory;
		DataStore<StoredCredential> datastore;
		try {
			fileDataStoreFactory = new FileDataStoreFactory(new File(tomcatHomeDir + "/" + CREDENTIALS_DIRECTORY));
			datastore = fileDataStoreFactory.getDataStore(credentialDatastore);
		} catch (IOException e) {
			throw new AuthorizeException(e);
		}
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory,
				clientSecrets, Auth.SCOPES).setAccessType(ACCESS_TYPE_OFFLINE).setApprovalPrompt(APPROVAL_PROMPT_FORCE)
						.setCredentialDataStore(datastore).build();
		return flow;
	}

}