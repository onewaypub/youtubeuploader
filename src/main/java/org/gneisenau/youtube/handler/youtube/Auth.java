package org.gneisenau.youtube.handler.youtube;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.gneisenau.youtube.handler.video.exceptions.AuthorizeException;
import org.gneisenau.youtube.handler.video.exceptions.ClientSecrectsException;
import org.gneisenau.youtube.handler.video.exceptions.SecretsStoreException;
import org.gneisenau.youtube.message.MailSendService;
import org.gneisenau.youtube.model.UserSettingsRepository;
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

	@Autowired
	private UserSettingsRepository userSettingDAO;
	@Value("${tomcat.home.dir}")
	private String tomcatHomeDir;

	@Autowired
	private MailSendService mailService;
	public static final String APP_NAME = "YoutubeUploader";
	@Value("${redirect.host}")
	private String redirectHost;
	@Value("${youtube.client.secret}")
	private String clientSecretJson;

	/**
	 * Define a global instance of the HTTP transport.
	 */
	public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	/**
	 * Define a global instance of the JSON factory.
	 */
	public static final JsonFactory JSON_FACTORY = new JacksonFactory();

	/**
	 * This is the directory that will be used under the user's home directory
	 * where OAuth tokens will be stored.
	 */
	private static final String CREDENTIALS_DIRECTORY = ".oauth-credentials";

	// This OAuth 2.0 access scope allows for full read/write access to the
	// authenticated user's account.
	private List<String> scopes = Lists.newArrayList(YouTubeScopes.YOUTUBE,
			YouTubeScopes.YOUTUBE_UPLOAD);

	/**
	 * Authorizes the installed application to access user's protected data.
	 *
	 * @param scopes
	 *            list of scopes needed to run youtube upload.
	 * @param credentialDatastore
	 *            name of the credential datastore to cache OAuth tokens
	 * @throws ClientSecrectsException
	 * @throws SecretsStoreException
	 * @throws AuthorizeException
	 */
	public synchronized Credential authorize(String credentialDatastore, String username)
			throws ClientSecrectsException, SecretsStoreException, AuthorizeException {

		// Load client secrets.

		// Reader clientSecretReader = new
		// InputStreamReader(Auth.class.getResourceAsStream("/client_secrets.json"));
		Reader clientSecretReader = new StringReader(clientSecretJson);

		GoogleClientSecrets clientSecrets;
		try {
			clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);
		} catch (IOException e) {
			throw new ClientSecrectsException(e);
		}

		// This creates the credentials datastore at
		// ~/.oauth-credentials/${credentialDatastore}
		FileDataStoreFactory fileDataStoreFactory;
		DataStore<StoredCredential> datastore;
		try {
			fileDataStoreFactory = new FileDataStoreFactory(new File(tomcatHomeDir + "/" + CREDENTIALS_DIRECTORY));
			datastore = fileDataStoreFactory.getDataStore(credentialDatastore);
		} catch (IOException e) {
			throw new SecretsStoreException(e);
		}

		// Collection<String> scopes = new ArrayList<String>();
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, scopes).setAccessType("offline").setApprovalPrompt("force")
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
//			LocalServerReceiver localReceiver;
//			try {
//				String hostAddress = null;
//				if (redirectHost == null || redirectHost.trim() == "") {
//					hostAddress = InetAddress.getLocalHost().getHostAddress();
//				} else {
//					hostAddress = redirectHost;
//				}
//				localReceiver = new LocalServerReceiver.Builder().setHost(redirectHost).setPort(8081).build();
//			} catch (UnknownHostException e) {
//				throw new AuthorizeException(e);
//			}
//
//			try {
//				return new AuthorizationCodeMailGateway(flow, localReceiver, mailService, username).authorize(username);
//			} catch (IOException e) {
//				throw new AuthorizeException(e);
//			}
			
			
			//if not signed in show error message via event bus
			return null;
		}
	}

}