package ru.job4j.bank.model;

import ru.job4j.markers.Operation;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class Book {

    @NotNull(message = "Id must be non null", groups = {
            Operation.OnUpdate.class, Operation.OnCreate.class
    })
    private Integer id;

    @NotBlank(message = "Title must be not empty")
    private String title;

    @Min(value = 1, message = "Year must be more than 0")
    private int year;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        Book book = (Book) o;
        return year == book.year &&
                Objects.equals(id, book.id) &&
                Objects.equals(title, book.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, year);
    }
}