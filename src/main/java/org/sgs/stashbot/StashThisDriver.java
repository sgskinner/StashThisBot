package org.sgs.stashbot;


import org.sgs.stashbot.app.StashThisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
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
