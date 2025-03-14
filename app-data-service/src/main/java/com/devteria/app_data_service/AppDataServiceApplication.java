package com.devteria.app_data_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling  // This enables scheduled tasks

public class AppDataServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppDataServiceApplication.class, args);
	}

}
