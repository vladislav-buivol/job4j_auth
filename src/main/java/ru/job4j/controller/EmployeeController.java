package ru.job4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.domain.Employee;
import ru.job4j.domain.Person;
import ru.job4j.service.EmployeeService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private RestTemplate rest;

    private static final String ACCOUNT_API = "http://localhost:8080/person/";
    private static final String ACCOUNT_API_ID = "http://localhost:8080/person/{id}";
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/")
    public List<Employee> findAll() {
        return this.employeeService.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Employee> findById(@PathVariable int id) {
        var employee = this.employeeService.findById(id);
        if (employee.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
        }
        return new ResponseEntity<Employee>(employee.get(), HttpStatus.OK);

    }

    @PostMapping("/")
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        if (employee == null || employee.getAccounts() == null) {
            throw new NullPointerException("Employee or account mustn't be empty");
        }
        Set<Person> accounts = new HashSet<>();
        for (Person account : employee.getAccounts()) {
            accounts.add(rest.postForObject(ACCOUNT_API, account, Person.class));
        }
        employee.setAccounts(accounts);
        return new ResponseEntity<Employee>(
                this.employeeService.save(employee),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Employee employee) {
        this.employeeService.save(employee);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/addAccounts/{id}")
    public ResponseEntity<Void> addAccountsToEmployee(@PathVariable int id,
                                                      @RequestBody List<Person> accounts) {
        if (employeeService.findById(id).isPresent()) {
            Employee employee = employeeService.findById(id).get();
            Set<Person> employeeAccounts = employee.getAccounts();
            for (Person account : accounts) {
                employeeAccounts.add(rest.postForObject(ACCOUNT_API, account, Person.class));
            }
            employee.setAccounts(employeeAccounts);
            this.employeeService.save(employee);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Employee employee = new Employee();
        employee.setId(id);
        this.employeeService.delete(employee);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/deleteAccounts/{id}")
    public ResponseEntity<Void> deleteEmployeeAccount(@PathVariable int id,
                                                      @RequestBody List<Person> accounts) {
        if (employeeService.findById(id).isPresent()) {
            Employee employee = employeeService.findById(id).get();
            Set<Person> accountsToDelete = new HashSet<>();
            for (Person account : accounts) {
                Person acc = rest.getForObject(ACCOUNT_API_ID, Person.class, account.getId());
                if (account.getId() != 0) {
                    accountsToDelete.add(acc);
                }
            }
            employee.getAccounts().removeAll(accountsToDelete);
            this.employeeService.save(employee);
        }
        return ResponseEntity.ok().build();
    }
}
