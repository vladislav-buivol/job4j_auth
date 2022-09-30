package ru.job4j.bank.model;

public abstract class Id {

    protected int id;

    public Id() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}