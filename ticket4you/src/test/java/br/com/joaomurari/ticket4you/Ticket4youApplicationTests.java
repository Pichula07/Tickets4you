package br.com.joaomurari.ticket4you;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Ticket4youApplicationTests {

	@BeforeAll
	static void setUp() {
		System.setProperty("EVENT_API", "localhost:8081");
		System.setProperty("TICKET_API", "localhost:8082");
	}

	@Test
	void contextLoads() {
	}

}
