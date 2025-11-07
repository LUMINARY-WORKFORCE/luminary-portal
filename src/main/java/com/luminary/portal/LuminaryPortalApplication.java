package com.luminary.portal;

import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Key;

@SpringBootApplication
public class LuminaryPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(LuminaryPortalApplication.class, args);
	}

}
