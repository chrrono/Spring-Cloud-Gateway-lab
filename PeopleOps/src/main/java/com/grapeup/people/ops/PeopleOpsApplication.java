package com.grapeup.people.ops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
public class PeopleOpsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PeopleOpsApplication.class, args);
	}

}

@RestController
class EmployeeController {

	private static List<Employee> listOfEmployees = List.of(
			new Employee("Jan Kowal", "Java developer", 31, "Production"),
			new Employee("Anna Fiołek", "Accounting Manager", 35, "Accounting"),
			new Employee("Mateusz Kot", "Technical Leader", 27, "Production"),
			new Employee("Zuzanna Truskawka", "Recruiter", 22, "HR")
		);

	@GetMapping(path="/employees/v1", produces= MediaType.APPLICATION_JSON_VALUE)
	public List<Employee> getEmployees() {
		return listOfEmployees.subList(0,2);
	}

	@GetMapping(path="/employees/v2", produces= MediaType.APPLICATION_JSON_VALUE)
	public List<Employee> getEmployeesV2() {
		return listOfEmployees.subList(2,4);
	}

	@GetMapping(path = "/employee/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getEmployeeById(@PathVariable String id) {
		int idNumber;
		try {
			idNumber = Integer.parseInt(id);
		} catch (NumberFormatException ex) {
			return new ResponseEntity<>("Bad id parameter. Must be a number", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity(listOfEmployees.get(idNumber % 4), HttpStatus.OK);
	}

}

