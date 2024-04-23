package com.revature.StreamFlixBackend.services;

import com.revature.StreamFlixBackend.exceptions.MovieNotFoundException;
import com.revature.StreamFlixBackend.models.Movie;
import com.revature.StreamFlixBackend.repos.MovieDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    private final MovieDAO movieDAO;

    @Autowired
    public MovieService(MovieDAO movieDAO) {
        this.movieDAO = movieDAO;
    }

    public Movie getMovieById(int id) throws MovieNotFoundException {
        return movieDAO.findById(id).orElseThrow(() -> new MovieNotFoundException("Movie not found!"));
    }

    public List<Movie> getAllMovies() {
        return movieDAO.findAll();
    }
}
