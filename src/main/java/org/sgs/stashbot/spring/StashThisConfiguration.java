package org.sgs.stashbot.spring;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;


@Configuration
@EnableTransactionManagement
@PropertySource(value = {"classpath:org/sgs/stashbot/stashbot.properties", "classpath:org/sgs/stashbot/security.properties"})
@ComponentScan({"org.sgs.stashbot.model", "org.sgs.stashbot.service.impl", "org.sgs.stashbot.dao.impl",
        "org.sgs.stashbot.spring", "org.sgs.stashbot.app", "org.sgs.stashbot.util"})
public class StashThisConfiguration {
    private Environment env;




    @Bean
    public Credentials getCredentials() {
        return new Credentials(AuthenticationMethod.SCRIPT,
                env.getRequiredProperty("reddit.username"),
                env.getRequiredProperty("reddit.password"),
                env.getRequiredProperty("reddit.clientId"),
                env.getRequiredProperty("reddit.clientSecret"), null, //deviceId, not used by us
                env.getRequiredProperty("reddit.redirectUrl"));
    }


    @Bean
    public RedditClient getRedditClient() {
        UserAgent userAgent = UserAgent.of("desktop", "org.sgs.stashbot", env.getProperty("stashbot.version"), "StashThis");
        return new RedditClient(userAgent);
    }


    @Bean(name = "botsRedditUsername")
    public String getBotsRedditUsername() {
        return env.getRequiredProperty("reddit.username");
    }


    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

}
