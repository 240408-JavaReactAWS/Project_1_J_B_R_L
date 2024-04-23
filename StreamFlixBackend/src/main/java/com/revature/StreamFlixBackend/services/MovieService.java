package com.revature.StreamFlixBackend.services;


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

    public List<Movie> getMoviesByUsername(String username) {
        Optional<Users> user = userDAO.findByUsername(username);
        if (user.isEmpty()) {
            throw new NoSuchElementException("No user was found");
        }
        return movieDAO.getMoviesByUser(user.get());
    }

    public List<Movie> getMoviesByUserId(String username, int id) {
        Optional<Users> user = userDAO.findByUsername(username);
        if (user.isEmpty()) {
            throw new NoSuchElementException("Username not found");
        }
        if (!user.get().isAdmin()) {
            throw new IllegalArgumentException("User is not admin");
        }
        Optional<Users> getUser = userDAO.findById(id);
        if (getUser.isEmpty()) {
            throw new NoSuchElementException("User id was not found");
        }
        return movieDAO.getMoviesByUser(getUser.get());
    }

    public Movie getMovieById(int id) throws MovieNotFoundException {
        return movieDAO.findById(id).orElseThrow(() -> new MovieNotFoundException("Movie not found!"));
    }

    public List<Movie> getAllMovies() {
        return movieDAO.findAll();
    }
}
