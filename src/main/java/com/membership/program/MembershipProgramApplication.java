package com.membership.program;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class MembershipProgramApplication {

	public static void main(String[] args) {
		SpringApplication.run(MembershipProgramApplication.class, args);
	}

}
