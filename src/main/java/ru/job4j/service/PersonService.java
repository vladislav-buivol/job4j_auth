package ru.job4j.service;

import org.springframework.stereotype.Service;
import ru.job4j.domain.Person;
import ru.job4j.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Optional<Person> findById(int id) {
        return personRepository.findById(id);
    }

    public Person save(Person person) {
        personRepository.save(person);
        return person;
    }

    public void delete(Person person) {
        personRepository.delete(person);
    }

    public Person findByUsername(String username) {
        return personRepository.findFirstByLogin(username);
    }
}
