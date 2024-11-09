package com.nbcamp.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NbcampOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NbcampOrderServiceApplication.class, args);
    }

}
