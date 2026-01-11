package com.example.demo;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@GetMapping("/")
	String hello() {
		return "Hello World!";
	}

//	@Bean
//	ApplicationRunner runner(JdbcClient db) {
//		return args -> {
//			var count = db
//					.sql("select 1")
//					.query(Integer.class)
//					.single()
//					.intValue();
//			System.out.println("count: " + count);
//		};
//	}

}
