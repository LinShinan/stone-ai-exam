package com.stone.aiexam;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFileStorage
@SpringBootApplication
public class StoneAiExamApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoneAiExamApplication.class, args);
	}

}
