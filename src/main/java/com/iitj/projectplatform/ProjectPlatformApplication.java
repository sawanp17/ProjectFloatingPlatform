package com.iitj.projectplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

//@SpringBootApplication
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })

public class ProjectPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectPlatformApplication.class, args);
	}

}
