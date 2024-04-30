package com.revature.StreamFlixBackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/*
 * Movie class for StreamFlixBackend
 * This class is used to create the movie entity and the movies table in the database.
 * @Author: Ryan Sherk, Luis Garcia, Jeff Gomez, Brian Bollivar
 */
@Entity
@Table(name = "movies")
@Component
public class Movie {
    /* movieId is the primary key for the movies table */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int movieId;
    /* name is the name of the movie */
    private String name;
    /* price is the price of the movie */
    private double price;
    /* url is the url of the movie */
    @Column(length = 3000)
    private String url;
    /* snapshot is the Thumbnail of the movie */
    @Column(length = 3000)
    private String snapshot;
    /* description is the description of the movie */
    @Column(length = 3000)   
    private String description;

    /* user is the list of users that have ordered the movie */
    @ManyToMany
    @JoinTable(
            name="orders",
            joinColumns = {@JoinColumn(name = "movie_id")},
            inverseJoinColumns = {@JoinColumn(name="user_id")})
    @JsonIgnore
    private List<Users> user;

    /*
     * No args constructor required by JPA
     */
    public Movie() {
    }

    /*
     * Get the movieId
     * @return the movieId
     */
    public int getMovieId() {
        return movieId;
    }

    /*
     * Set the movieId
     * @param movieId the movieId to set
     */
    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    /*
     * Get the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /*
     * Set the name
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * Get the price
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /*
     * Set the price
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /*
     * Get the url
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /*
     * Set the url
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /*
     * Get the snapshot
     * @return the snapshot
     */
    public String getSnapshot() {
        return snapshot;
    }

    /*
     * Set the snapshot
     * @param snapshot the snapshot to set
     */
    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    /*
     * Get the description
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /*
     * Set the description
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * Equals method that checks the movieId, name, price, and url
     * @param o the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return movieId == movie.movieId && Double.compare(price, movie.price) == 0 && Objects.equals(name, movie.name) && Objects.equals(url, movie.url);
    }

    /*
     * Hashcode method that hashes the movieId, name, price, and url
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(movieId, name, price, url);
    }

    /*
     * To string method that returns a string with the movieId, name, price, and url
     * @return the string
     */
    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", name='" + name + '\'' +
                ", price=" + price + '\'' +
                ", url=" + url +
                '}';
    }
}
