package com.grapeup.production;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@SpringBootApplication
public class ProductionApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductionApplication.class, args);
	}

}

@RestController
class ProjectsController {

	static final String CLIENTS[] = new String[]{"Porsche","Allstate","First America"};

	@Value("${production.instance}")
	String instance;

	@GetMapping(path = "/project", produces = MediaType.APPLICATION_JSON_VALUE)
	public Project getExampleProject() {
		return new Project(instance, "in-progress", CLIENTS[new Random().nextInt(3)]);
	}

	@GetMapping(path="/teams", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Team> getTeams() {
		return List.of(new Team("Java developers", 10),
				new Team("Python Developers", 5),
				new Team("Devops", 3));
	}

}
