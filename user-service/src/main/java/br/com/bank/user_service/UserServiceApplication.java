package br.com.bank.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import io.github.cdimascio.dotenv.Dotenv;

@EnableDiscoveryClient
@EnableR2dbcRepositories
@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.load();

		System.setProperty("PG_URL_USER", dotenv.get("PG_URL_USER"));
		System.setProperty("PG_USER", dotenv.get("PG_USER"));
		System.setProperty("PG_PASSWORD", dotenv.get("PG_PASSWORD"));

		SpringApplication.run(UserServiceApplication.class, args);
	}

}
