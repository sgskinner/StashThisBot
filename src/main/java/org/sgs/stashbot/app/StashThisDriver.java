package org.sgs.stashbot.app;


import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = {"org.sgs.stashbot.*"})
@EnableJpaRepositories(basePackages = "org.sgs.stashbot.*")
@EntityScan(basePackages = {"org.sgs.stashbot.*"})
public class StashThisDriver implements CommandLineRunner {
    private StashThisService stashThisService;


    @Override
    public void run(String... args) {
        stashThisService.run();
    }


    public static void main(String... sgs) {
        new SpringApplicationBuilder(StashThisService.class)
                .bannerMode(Banner.Mode.OFF)
                .run(sgs);
    }


    @Autowired
    public void setStashThisService(StashThisService stashThisService) {
        this.stashThisService = stashThisService;
    }

}
