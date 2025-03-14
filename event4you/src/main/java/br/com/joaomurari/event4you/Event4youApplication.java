package br.com.joaomurari.event4you;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class Event4youApplication {

	public static void main(String[] args) {
		SpringApplication.run(Event4youApplication.class, args);
	}

}
