package org.gneisenau.youtube.test.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "org.gneisenau.youtube")
@PropertySource("/test.properties")
public class TestConfigurationContext {

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() {
		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		Resource resource = new PathResource(FilenameUtils.getPath(TestConfigurationContext.class.getResource("/").getPath()) + "test.properties");
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
	
//    /**
//     * Mock beans for all services. The beans are marked  to ensure they take precedence over the implementations for autowiring.
//     */
//    @Configuration
//    @Profile("mockedServices")
//    public static class ServiceMocks {
//        @Bean
//        @Primary
//        public InvoiceService mockedInvoiceService() {
//            return createMock(InvoiceService.class);
//        }
//
//        @Bean
//        @Primary
//        public CustomerService mockedCustomerService() {
//            return createMock(CustomerService.class);
//        }
//
//        @Bean
//        @Primary
//        public ReportService mockedReportService() {
//            return createMock(ReportService.class);
//        }
//    }

}
