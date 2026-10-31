package com.datapilot.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class DatapilotApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatapilotApiApplication.class, args);
	}

}
