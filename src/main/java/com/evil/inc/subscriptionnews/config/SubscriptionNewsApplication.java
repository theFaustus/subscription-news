package com.evil.inc.subscriptionnews.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.evil.inc.subscriptionnews")
public class SubscriptionNewsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubscriptionNewsApplication.class, args);
	}

}
