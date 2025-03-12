package com.dinhduong.jobhunter;

import org.springframework.boot.SpringApplication;

public class TestJobhunterApplication {

	public static void main(String[] args) {
		SpringApplication.from(JobhunterApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
