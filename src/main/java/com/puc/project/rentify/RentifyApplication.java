package com.puc.project.rentify;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RentifyApplication {
	public static void main(String[] args) {
		SpringApplication.run(RentifyApplication.class, args);
	}
}
