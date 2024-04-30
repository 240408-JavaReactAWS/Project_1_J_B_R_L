package com.revature.StreamFlixBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * This is the main class for the StreamFlix Spring Boot application. It is the entry point for the application.
 * This application has a RESTful API that allows users to interact with a database of movies and users.
 * @Author: Ryan Sherk, Luis Garcia, Jeff Gomez, Brian Bollivar
 */
@SpringBootApplication
public class StreamFlixBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(StreamFlixBackendApplication.class, args);
	}

}
