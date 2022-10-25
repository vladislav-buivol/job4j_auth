package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.domain.Person;
import ru.job4j.exception.FailedToSaveException;
import ru.job4j.markers.Operation;
import ru.job4j.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService persons;
    private final BCryptPasswordEncoder encoder;

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PersonController.class.getSimpleName());

    private final ObjectMapper objectMapper;

    public PersonController(PersonService persons,
                            BCryptPasswordEncoder encoder,
                            ObjectMapper objectMapper) {
        this.persons = persons;
        this.encoder = encoder;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Person>> findAll() {
        return new ResponseEntity<>(this.persons.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = this.persons.findById(id);
        if (person.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found");
        }
        return new ResponseEntity<Person>(person.get(), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(
            @Validated(Operation.OnCreate.class) @RequestBody Person person) {
        Optional<Person> p = this.persons.findById(person.getId());
        if (p.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Person already exist.");
        }
        return new ResponseEntity<Person>(
                this.persons.save(person),
                HttpStatus.CREATED);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        this.persons.save(person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        this.persons.delete(person);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public void signUp(@RequestBody Person person) {
        if (person == null || person.getLogin() == null) {
            throw new NullPointerException("Person and login cannot be empty");
        }
        if (persons.findByUsername(person.getLogin()) == null) {
            person.setPassword(encoder.encode(person.getPassword()));
            this.persons.save(person);
        }
    }

    @ExceptionHandler(value = {FailedToSaveException.class})
    public void exceptionHandler(Exception e, HttpServletRequest request,
                                 HttpServletResponse response) throws
            IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() {
            {
                put("message", e.getMessage());
                put("type", e.getClass());
            }
        }));
        LOGGER.error(e.getLocalizedMessage());
    }

    @PatchMapping("/patch")
    public Person patch(@RequestBody Person person)
            throws InterruptedException, InvocationTargetException, IllegalAccessException {
        Optional<Person> personDataFromDb = persons.findById(person.getId());
        if (personDataFromDb.isPresent()) {
            Person current = personDataFromDb.get();
            current.patch(person);
            persons.save(current);
            return current;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found");
    }
}