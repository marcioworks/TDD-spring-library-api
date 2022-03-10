package br.com.marcioss.libraryapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class LibrayApiApplication {

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	@Scheduled(cron = "0 21 13 1/1 * ?")
	public void scheduleTest(){
		System.out.println(" TASK SCHEDULE RUNNING...");
	}

	public static void main(String[] args) {
		SpringApplication.run(LibrayApiApplication.class, args);
	}

}
