package com.credit_suisse.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration.class
})
public class CreditSuisseTrialApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(CreditSuisseTrialApplication.class, args);
    }
}