package com.revature.StreamFlixBackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/*
 * Users class for StreamFlixBackend
 * This class is used to create the users entity and the users table in the database.
 */
@Entity
@Table(name="users")
@Component
public class Users {
    /* userId is the primary key for the users table */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userId;
    /* username is the username of the user */
    @Column(unique = true, nullable = false)
    private String username;
    /* password is the password of the user */
    @Column(nullable = false)
    private String password;
    /* name is the name of the user */
    @Column(nullable = false)
    private String name;
    /* email is the email of the user */
    @Column(unique = true, nullable = false)
    private String email;
    /* balance is the balance of the user */
    @Column(nullable = false)
    @Value("0")
    private double balance;
    /* isAdmin is a boolean that determines if the user is an admin */
    @Column(nullable = false)
    private boolean isAdmin;

    /*
     * Sets the userId
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /*
     * Gets the list of movies that the user has ordered
     * @return the list of movies that the user has ordered
     */
    public List<Movie> getMovies() {
        return movies;
    }

    /*
     * Sets the list of movies that the user has ordered
     * @param movies the list of movies that the user has ordered
     */
    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    /* movies is the list of movies that the user has ordered */
    @ManyToMany
    @JoinTable(
            name="orders",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name="movie_id")})
    @JsonIgnore
    private List<Movie> movies;

    /*
     * No args constructor required by JPA
     */
    public Users() {

    }

    /*
     * Constructor for Users
     * @param userId the user id
     * @param username the username
     * @param password the password
     * @param name the name
     * @param email the email
     * @param balance the balance
     * @param isAdmin the isAdmin
     */
    public Users(int userId, String username, String password, String name, String email, double balance, boolean isAdmin) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.balance = balance;
        this.isAdmin = isAdmin;
    }

    /*
     * Constructor for Users
     * @param username the username
     * @param password the password
     * @param name the name
     * @param email the email
     * @param balance the balance
     * @param isAdmin the isAdmin
     */
    public Users(String username, String password, String name, String email, double balance, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.balance = balance;
        this.isAdmin = isAdmin;
    }

    /*
     * Get the userId
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /*
     * Gets the username
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /*
     * Sets the username
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /*
     * Gets the password
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /*
     * Sets the password
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /*
     * Gets the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /*
     * Sets the name
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * Gets the email
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /*
     * Sets the email
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /*
     * Gets the balance
     * @return the balance
     */
    public double getBalance() {
        return balance;
    }

    /*
     * Sets the balance
     * @param balance the balance to set
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /*
     * Gets the isAdmin
     * @return the isAdmin
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /*
     * Sets the isAdmin
     * @param isAdmin the isAdmin to set
     */
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    /*
     * Equals method for Users that checks the userId, username, name, email, and isAdmin
     * @param o the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return userId == users.userId && isAdmin == users.isAdmin && Objects.equals(username, users.username) && Objects.equals(name, users.name) && Objects.equals(email, users.email);
    }

    /*
     * Hashcode method for Users that hashes the userId, username, name, email, and isAdmin
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, username, name, email, isAdmin);
    }

    /*
     * To string method for Users that returns the userId, username, password, name, email, balance, and isAdmin
     * @return the string representation of the Users object
     */
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
