package br.com.bank.investment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import io.github.cdimascio.dotenv.Dotenv;

@EnableDiscoveryClient
@EnableR2dbcRepositories
@SpringBootApplication
public class InvestmentServiceApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
			.filename(".env") // ou ".env.docker"
			.ignoreIfMissing()
			.load();

		String env = dotenv.get("SPRING_ENV", "local");

		if (env.equals("docker")) {
			System.setProperty("PG_URL_INVESTMENT", dotenv.get("PG_URL_INVESTMENT_DOCKER"));
		} else {
			System.setProperty("PG_URL_INVESTMENT", dotenv.get("PG_URL_INVESTMENT"));
		}

		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
		System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));
		System.setProperty("PG_USER", dotenv.get("PG_USER"));
		System.setProperty("PG_PASSWORD", dotenv.get("PG_PASSWORD"));

		SpringApplication.run(InvestmentServiceApplication.class, args);
	}

}
