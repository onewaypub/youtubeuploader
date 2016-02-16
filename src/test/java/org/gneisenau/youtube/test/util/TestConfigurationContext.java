package org.gneisenau.youtube.test.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.io.FilenameUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;

@Configuration
@ComponentScan(basePackages = "org.gneisenau.youtube")
@PropertySource("/test.properties")
@Profile("test")
public class TestConfigurationContext {

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() {
		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		Resource resource = new PathResource(
				FilenameUtils.getPath(TestConfigurationContext.class.getResource("/").getPath()) + "test.properties");
		propertySourcesPlaceholderConfigurer.setLocations(resource);
		return propertySourcesPlaceholderConfigurer;
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
		dataSource.setUrl("jdbc:hsqldb:mem:paging");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}

	@Bean
	public DozerBeanMapper getDozerMapper() {
		List<String> mappingFiles = new ArrayList<String>();
		mappingFiles.add("dozerMapping.xml");
		DozerBeanMapper beanMapper = new DozerBeanMapper(mappingFiles);
		return beanMapper;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		transactionManager.setPersistenceUnitName("jpaData");
		return transactionManager;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] { "org.gneisenau.youtube.model" });

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());

		return em;
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		properties.setProperty("hibernate.show_sql", "false");
		properties.setProperty("hibernate.format_sql", "false");
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		return properties;
	}

	@Bean
	public HttpTransport getHttpTransport() {
		return new MockHttpTransport() {
			@Override
			public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
				return new MockLowLevelHttpRequest() {
					@Override
					public LowLevelHttpResponse execute() throws IOException {
						MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
						response.addHeader("custom_header", "value");
						response.setStatusCode(404);
						response.setContentType(Json.MEDIA_TYPE);
						response.setContent("{\"error\":\"not found\"}");
						return response;
					}
				};
			}
		};
	}

	@Bean
	public JsonFactory getJsonFactory() {
		return new JacksonFactory();
	}

}
