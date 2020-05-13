package com.evil.inc.subscriptionnews.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@SpringBootApplication(scanBasePackages = "com.evil.inc.subscriptionnews")
public class SubscriptionNewsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubscriptionNewsApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
