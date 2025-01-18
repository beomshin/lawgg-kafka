package com.kr.kafka.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories(basePackages = DBConfig.REPOSITORY_PACKAGE)
@Slf4j
public class DBConfig {


    public static final String REPOSITORY_PACKAGE = "com.kr.kafka.repository.store";
    private final String ENTITY_PACKAGE = "com.kr.kafka.entity";
    private final String PERSISTENCE_UNIT = "entityManager";

    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;

    @Value("${spring.datasource.driver-class-name}")
    private String className;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(className);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setPoolName("hikari-lawgg-spring");
        hikariConfig.setConnectionTestQuery("SELECT 1 FROM DUAL");
        hikariConfig.setConnectionInitSql("SELECT NOW() FROM DUAL");
        hikariConfig.setMaximumPoolSize(20);
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, EntityManagerFactoryBuilder builder, ConfigurableListableBeanFactory beanFactory) {
        Map<String, Object> properties = hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
        LocalContainerEntityManagerFactoryBean bean = builder
                .dataSource(dataSource)
                .packages(ENTITY_PACKAGE)
                .persistenceUnit(PERSISTENCE_UNIT)
                .properties(properties)
                .build();
        bean.getJpaPropertyMap().put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(beanFactory)); // convert bean 등록 허용
        return bean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }


}
