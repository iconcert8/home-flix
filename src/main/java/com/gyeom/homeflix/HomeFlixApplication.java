package com.gyeom.homeflix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HomeFlixApplication {

    static Logger log = LoggerFactory.getLogger(HomeFlixApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(HomeFlixApplication.class, args);
        log.info("Start Home-Flix. DDoo-Doong");
    }
}