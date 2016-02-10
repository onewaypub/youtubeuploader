package org.gneisenau.youtube.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.social.config.annotation.EnableSocial;

@Configuration
@PropertySource("file:${user.home}/youtubeuploader.properties")
@Profile("prod")
public class PropertyConfig {

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() {
		String userhome = System.getProperty("user.home");
		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		Resource resource = new PathResource(userhome + "/youtubeuploader.properties");
		propertySourcesPlaceholderConfigurer.setLocations(resource);
		return propertySourcesPlaceholderConfigurer;
	}

}
