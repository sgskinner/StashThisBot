package org.sgs.stashbot;

import org.sgs.stashbot.app.StashBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = {"org.sgs.stashbot"})
@EnableJpaRepositories(basePackages = {"org.sgs.stashbot"})
@EntityScan(basePackages = {"org.sgs.stashbot"})
public class StashBotDriver implements CommandLineRunner {
    private StashBotService stashBotService;


    @Override
    public void run(String... args) {
        stashBotService.run();
    }


    public static void main(String... sgs) {
        new SpringApplicationBuilder(StashBotDriver.class)
                .bannerMode(Banner.Mode.OFF)
                .run(sgs);
    }


    @Autowired
    public void setStashThisService(StashBotService stashBotService) {
        this.stashBotService = stashBotService;
    }

}
