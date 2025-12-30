package com.multitenant.ticker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.multitenant.ticker")
public class TickerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TickerApplication.class, args);
	}

}
