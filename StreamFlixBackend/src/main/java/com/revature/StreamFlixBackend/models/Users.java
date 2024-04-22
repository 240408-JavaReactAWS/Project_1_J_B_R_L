package com.revature.StreamFlixBackend.models;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Entity
@Table(name="users")
@Component
public class Users {
    @Id
    @Column(name = "userId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userId;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private double balance;
    @Column(nullable = false)
    private boolean isAdmin;

    public Users() {

    }
    public Users(int userId, String username, String password, String name, String email, double balance, boolean isAdmin) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.balance = balance;
        this.isAdmin = isAdmin;
    }

    public Users(String username, String password, String name, String email, double balance, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.balance = balance;
        this.isAdmin = isAdmin;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return userId == users.userId && isAdmin == users.isAdmin && Objects.equals(username, users.username) && Objects.equals(name, users.name) && Objects.equals(email, users.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, name, email, isAdmin);
    }

    @Override
    public String toString() {
        return "Users{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", balance=" + balance +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
