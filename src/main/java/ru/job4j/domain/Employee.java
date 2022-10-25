package ru.job4j.domain;

import org.springframework.format.annotation.DateTimeFormat;
import ru.job4j.markers.Operation;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
public class Employee implements Patchable<Employee> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "Id must be not null", groups = {
            Operation.OnUpdate.class, Operation.OnDelete.class
    })
    @Min(value = 1, message = "Id must be greater than 0", groups = {
            Operation.OnUpdate.class, Operation.OnDelete.class})
    private int id;

    @NotBlank(message = "Name must be not empty", groups = {Operation.OnCreate.class,
            Operation.OnUpdate.class})
    private String firstName;

    @NotBlank(message = "Second name must be not null", groups = {Operation.OnCreate.class,
            Operation.OnUpdate.class})
    private String secondName;

    @NotBlank(message = "Inn must be not null", groups = {Operation.OnCreate.class,
            Operation.OnUpdate.class})
    private String inn;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Hire date must be not null", groups = {Operation.OnCreate.class,
            Operation.OnUpdate.class})
    private Date hireDate;

    @OneToMany(cascade = {CascadeType.REMOVE})
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Set<Person> accounts;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) throws ParseException {
        String pattern = "dd-MM-yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        this.hireDate = new SimpleDateFormat("dd-MM-yyyy").parse(df.format(hireDate));
    }

    public Set<Person> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Person> accounts) {
        this.accounts = accounts;
    }

    public void addAccount(Person account) {
        this.accounts.add(account);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee)) {
            return false;
        }
        Employee employee = (Employee) o;
        return Objects.equals(firstName, employee.firstName) &&
                Objects.equals(secondName, employee.secondName) &&
                Objects.equals(inn, employee.inn) &&
                Objects.equals(hireDate, employee.hireDate) &&
                Objects.equals(accounts, employee.accounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, secondName, inn, hireDate, accounts);
    }

}
