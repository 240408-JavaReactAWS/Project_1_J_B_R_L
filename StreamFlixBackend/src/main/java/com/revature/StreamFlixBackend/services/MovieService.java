package com.revature.StreamFlixBackend.services;


import com.revature.StreamFlixBackend.exceptions.UserNotFoundException;
import com.revature.StreamFlixBackend.models.Users;
import com.revature.StreamFlixBackend.exceptions.MovieNotFoundException;
import com.revature.StreamFlixBackend.models.Movie;
import com.revature.StreamFlixBackend.repos.MovieDAO;
import com.revature.StreamFlixBackend.repos.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class MovieService {
    private final MovieDAO movieDAO;

    private final UserDAO userDAO;


    @Autowired
    public MovieService(MovieDAO movieDAO, UserDAO userDAO) {
        this.movieDAO = movieDAO;
        this.userDAO = userDAO;
    }

    public Movie getMovieById(int id) throws MovieNotFoundException {
        return movieDAO.findById(id).orElseThrow(() -> new MovieNotFoundException("Movie not found!"));
    }

    public List<Movie> getAllMovies() {
        return movieDAO.findAll();
    }

    public List<Movie> getUnownedMovies(String username) {
        Users user = userDAO.findByUsername(username).orElseThrow(() -> new UserNotFoundException("This user doesn't exist!"));
        return movieDAO.findDistinctByUserNotContaining(user).orElseThrow(() -> new MovieNotFoundException("You have all the movies!"));
    }
}
