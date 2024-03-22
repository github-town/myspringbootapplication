package com.myspringboot.myspringbootfirstapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
public class MySpringbootFirstApplication {

	public static void main(String[] args) {
		SpringApplication.run(MySpringbootFirstApplication.class, args);
	}

	@LoadBalanced
	@Bean
	public RestTemplate restTemplateMy(RestTemplateBuilder builder) {
		builder.setConnectTimeout(Duration.ofSeconds(60));
		builder.setReadTimeout(Duration.ofSeconds(60));
		return builder.build();
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
}
