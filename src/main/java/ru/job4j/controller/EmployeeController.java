package ru.job4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.domain.Employee;
import ru.job4j.domain.Person;
import ru.job4j.markers.Operation;
import ru.job4j.service.EmployeeService;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
    public ResponseEntity<List<Employee>> findAll() {
        return new ResponseEntity<>(this.employeeService.findAll(), HttpStatus.OK);
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
    public ResponseEntity<Employee> create(
            @Validated(Operation.OnCreate.class) @RequestBody Employee employee) {
        if (employee == null || employee.getAccounts() == null) {
            throw new NullPointerException("Employee or account mustn't be empty");
        }
        Set<Person> accounts = new HashSet<>();
        String accessToken =
                employeeService.getAuthorization();
        for (Person account : employee.getAccounts()) {
            HttpHeaders headers = employeeService.getHeaderWithToken(accessToken);
            HttpEntity<Person> req = new HttpEntity<>(account, headers);
            ResponseEntity<Person> acc =
                    rest.exchange(ACCOUNT_API, HttpMethod.POST, req, Person.class);
            if (!acc.getStatusCode().isError()) {
                accounts.add(acc.getBody());
            }
        }
        employee.setAccounts(accounts);
        return new ResponseEntity<Employee>(
                this.employeeService.save(employee),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(
            @Validated(Operation.OnUpdate.class) @RequestBody Employee employee) {
        this.employeeService.save(employee);
        return ResponseEntity.ok().build();
    }

    @Validated
    @PutMapping("/addAccounts/{id}")
    public ResponseEntity<Void> addAccountsToEmployee(@PathVariable int id,
                                                      @Validated(Operation.OnCreate.class)
                                                      @RequestBody List<Person> accounts) {
        if (employeeService.findById(id).isPresent()) {
            Employee employee = employeeService.findById(id).get();
            Set<Person> employeeAccounts = employee.getAccounts();
            String accessToken =
                    employeeService.getAuthorization();
            for (Person account : accounts) {
                HttpHeaders headers = employeeService.getHeaderWithToken(accessToken);
                HttpEntity<Person> req = new HttpEntity<>(account, headers);
                ResponseEntity<Person> acc =
                        rest.exchange(ACCOUNT_API, HttpMethod.POST, req, Person.class);
                if (!acc.getStatusCode().isError()) {
                    employeeAccounts.add(acc.getBody());
                }
            }
            employee.setAccounts(employeeAccounts);
            this.employeeService.save(employee);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Validated(Operation.OnDelete.class) @PathVariable int id) {
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
            String accessToken =
                    employeeService.getAuthorization();
            for (Person account : accounts) {
                HttpEntity<String> entity = new HttpEntity<String>("body",
                        employeeService.getHeaderWithToken(accessToken));
                ResponseEntity<Person> acc =
                        rest.exchange(ACCOUNT_API_ID, HttpMethod.GET, entity, Person.class,
                                account.getId());
                if (account.getId() != 0 && !acc.getStatusCode().isError()) {
                    accountsToDelete.add(acc.getBody());
                }
            }
            employee.getAccounts().removeAll(accountsToDelete);
            this.employeeService.save(employee);
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/patch")
    public Employee patch(@Validated(Operation.OnUpdate.class) @RequestBody Employee employee)
            throws InterruptedException, InvocationTargetException, IllegalAccessException {
        Optional<Employee> emp = employeeService.findById(employee.getId());
        if (emp.isPresent()) {
            Employee current = emp.get();
            current.patch(employee);
            employeeService.save(current);
            return current;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
    }
}
