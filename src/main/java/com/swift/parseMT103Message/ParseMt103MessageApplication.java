package com.swift.parseMT103Message;

import com.swift.parseMT103Message.repository.MessageRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class ParseMt103MessageApplication {

	public static void main(String[] args) {

		SpringApplication.run(ParseMt103MessageApplication.class, args);
		System.out.println("MT103 Parsing Starts here");
	}

}
