package org.gneisenau.youtube.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

@Configuration
@EnableSocial
@PropertySource("file:${user.home}/youtubeuploader.properties")
public class SocialConfig implements SocialConfigurer {

	@Autowired
	private DataSource dataSource;
	@Value("${twitter.appId}")
	private String twitterConsumerKey;
	@Value("${twitter.appSecret}")
	private String twitterConsumerSecret;
	@Value("${facebook.appId}")
	private String facebookAppId;
	@Value("${facebook.appSecret}")
	private String facebookAppSecret;
	@Value("${google.appId}")
	private String googleAppId;
	@Value("${google.appSecret}")
	private String googleAppSecret;

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		return new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
	}

	@Bean
	public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator,
			ConnectionRepository connectionRepository) {
		return new ConnectController(connectionFactoryLocator, connectionRepository);
	}

	@Override
	public UserIdSource getUserIdSource() {
		return new AuthenticationNameUserIdSource();
	}

	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig, Environment env) {
		cfConfig.addConnectionFactory(new TwitterConnectionFactory(twitterConsumerKey, twitterConsumerSecret));
		cfConfig.addConnectionFactory(new FacebookConnectionFactory(facebookAppId, facebookAppSecret));
		cfConfig.addConnectionFactory(new GoogleConnectionFactory(googleAppId, googleAppSecret));
	}

}
