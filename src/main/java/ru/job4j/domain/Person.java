package ru.job4j.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String login;
    private String password;

    @ManyToOne
    private Employee employee;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Person)) {
            return false;
        }
        Person person = (Person) o;
        return id == person.id &&
                Objects.equals(login, person.login) &&
                Objects.equals(password, person.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password);
    }
}