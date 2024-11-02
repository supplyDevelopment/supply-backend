package com.example.supply;

import org.springframework.boot.SpringApplication;

public class TestSupplyApplication {

	public static void main(String[] args) {
		SpringApplication.from(SupplyApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
