package org.sgs.atbot.spring;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;


@Configuration
@EnableTransactionManagement
@PropertySource(value = {"classpath:org/sgs/atbot/atbot.properties", "classpath:org/sgs/atbot/security.properties"})
@ComponentScan({"org.sgs.atbot"})
public class AtbotConfiguration {
    private final Environment environment;


    @Autowired
    public AtbotConfiguration(Environment environment) {
        this.environment = environment;
    }


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
        dataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
        dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));
        return dataSource;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws NamingException {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource());
        factoryBean.setPackagesToScan(new String[]{"org.sgs.atbot.model", "org.sgs.atbot.service", "org.sgs.atbot.service.impl"});
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        factoryBean.setJpaProperties(jpaProperties());
        return factoryBean;
    }


    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }


    @Bean
    @Autowired
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(emf);
        return txManager;
    }


    @Bean
    public Credentials getCredentials() {
        return new Credentials(AuthenticationMethod.SCRIPT,
                environment.getRequiredProperty("reddit.username"),
                environment.getRequiredProperty("reddit.password"),
                environment.getRequiredProperty("reddit.clientId"),
                environment.getRequiredProperty("reddit.clientSecret"),
                null, //deviceId, not used by us
                environment.getRequiredProperty("reddit.redirectUrl"));
    }


    @Bean
    public UserAgent getUserAgent() {
        return UserAgent.of("desktop", "org.sgs.atbot", "0.1.1", "ArchiveThisBot");
    }


    @Bean
    public RedditClient getRedditClient() {
        return new RedditClient(getUserAgent());
    }


    @Bean(name = "subredditList")
    public List<String> getSubredditList() {
        List<String> subredditList = new ArrayList<>();
        String[] rawStrings = environment.getRequiredProperty("subreddit.list").split(",");
        subredditList.addAll(Arrays.asList(rawStrings));

        return subredditList;
    }


    @Bean(name = "summonTokens")
    public List<String> getSummonTokens() {
        List<String> tokenPatterns = new ArrayList<>();
        String[] tmpPatterns = environment.getRequiredProperty("summon.token.patterns").split(",");
        tokenPatterns.addAll(Arrays.asList(tmpPatterns));

        return tokenPatterns;
    }


    @Bean(name = "botsRedditUsername")
    public String getBotsRedditUsername() {
        return environment.getRequiredProperty("reddit.username");
    }


    private Properties jpaProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
        properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
        return properties;
    }

}
