package ru.job4j.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.domain.Person;
import ru.job4j.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService persons;
    private final BCryptPasswordEncoder encoder;

    public PersonController(PersonService persons,
                            BCryptPasswordEncoder encoder) {
        this.persons = persons;
        this.encoder = encoder;
    }

    @GetMapping("/all")
    public List<Person> findAll() {
        return this.persons.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = this.persons.findById(id);
        return new ResponseEntity<Person>(
                person.orElse(new Person()),
                person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        if (this.persons.findById(person.getId()).isEmpty()) {
            return new ResponseEntity<Person>(
                    this.persons.save(person),
                    HttpStatus.CREATED
            );
        }
        return new ResponseEntity<Person>(
                person,
                HttpStatus.ACCEPTED
        );
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
        if (person != null && person.getLogin().length() > 0
                && persons.findByUsername(person.getLogin()) == null) {
            person.setPassword(encoder.encode(person.getPassword()));
            this.persons.save(person);
        }
    }
}