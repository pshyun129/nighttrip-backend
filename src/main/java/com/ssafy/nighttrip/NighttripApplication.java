package com.ssafy.nighttrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class NighttripApplication {

	public static void main(String[] args) {
		SpringApplication.run(NighttripApplication.class, args);
	}

}
