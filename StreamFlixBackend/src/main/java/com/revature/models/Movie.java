package com.revature.models;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "movies")
@Component
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int movieId;
    private String name;
    private double price;

    public Movie() {
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
