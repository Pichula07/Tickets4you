package br.com.joaomurari.ticket4you;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class Ticket4youApplication {

	public static void main(String[] args) {
		SpringApplication.run(Ticket4youApplication.class, args);
	}

}
