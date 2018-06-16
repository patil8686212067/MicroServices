package com.user;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {
	    SecurityAutoConfiguration.class})
public class UserserviceApplication {

	public static void main(String[] args) 
	{
		 Locale.setDefault(Locale.US);
		SpringApplication.run(UserserviceApplication.class, args);
	}
}
