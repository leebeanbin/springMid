package com.sparta.springmid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class SpringMidApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMidApplication.class, args);
    }

}
