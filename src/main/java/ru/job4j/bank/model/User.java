package ru.job4j.bank.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class User extends Id {

    private String passport;

    private String username;

    private List<Account> accounts = new CopyOnWriteArrayList<>();

    public User(String passport, String username) {
        this.passport = passport;
        this.username = username;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}