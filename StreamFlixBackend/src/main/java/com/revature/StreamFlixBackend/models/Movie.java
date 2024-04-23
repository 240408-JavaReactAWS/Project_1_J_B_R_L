package com.revature.StreamFlixBackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "movies")
@Component
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int movieId;
    private String name;
    private double price;

    @ManyToMany
    @JoinTable(
            name="orders",
            joinColumns = {@JoinColumn(name = "movie_id")},
            inverseJoinColumns = {@JoinColumn(name="user_id")})
    @JsonIgnore
    private List<Users> user;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return movieId == movie.movieId && Double.compare(price, movie.price) == 0 && Objects.equals(name, movie.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, name, price);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
