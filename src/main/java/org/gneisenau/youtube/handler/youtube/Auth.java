package org.gneisenau.youtube.handler.youtube;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.collections4.list.UnmodifiableList;
import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.message.MailSendService;
import org.gneisenau.youtube.model.UserSettingsRepository;
import org.gneisenau.youtube.model.UserconnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
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
@PropertySource("file:${user.home}/youtubeuploader.properties")
public class Auth {

	@Value("${tomcat.home.dir}")
	private String tomcatHomeDir;
	public static final String APP_NAME = "YoutubeUploader";
	@Value("${youtube.client.secret}")
	private String clientSecretJson;
	public static final String APPROVAL_PROMPT_FORCE = "force";
	public static final String ACCESS_TYPE_OFFLINE = "offline";
	public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	public static final JsonFactory JSON_FACTORY = new JacksonFactory();
	@Autowired
	private DatabaseDataStoreFactory dbFactory;

	public static final List<String> SCOPES = new UnmodifiableList<String>(
			Lists.newArrayList(YouTubeScopes.YOUTUBE, YouTubeScopes.YOUTUBE_UPLOAD));

	public synchronized Credential authorize(String credentialDatastore, String username) throws AuthorizeException {

		GoogleClientSecrets clientSecrets = initializeClientSecrets();

		DataStore<StoredCredential> datastore;
		try {
			datastore = dbFactory.getDataStore(credentialDatastore);
		} catch (IOException e) {
			throw new AuthorizeException(e);
		}
		
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES).setAccessType(ACCESS_TYPE_OFFLINE).setApprovalPrompt(APPROVAL_PROMPT_FORCE)
						.setCredentialDataStore(datastore).build();

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
			clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);
		} catch (IOException e) {
			throw new AuthorizeException(e);
		}
		return clientSecrets;
	}

	public GoogleAuthorizationCodeFlow createGoogleAuthorizationCodeFlow() throws AuthorizeException {
		GoogleClientSecrets clientSecrets = initializeClientSecrets();
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, Auth.JSON_FACTORY,
				clientSecrets, Auth.SCOPES).setAccessType(ACCESS_TYPE_OFFLINE).setApprovalPrompt(APPROVAL_PROMPT_FORCE)
						.build();
		return flow;
	}

}