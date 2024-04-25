package com.revature.StreamFlixBackend.services;



import com.revature.StreamFlixBackend.exceptions.InsufficientFundsException;
import com.revature.StreamFlixBackend.exceptions.UnauthorizedException;

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

    public Movie addMovie(String username, Movie movie) {
        Optional<Users> adminOpt = userDAO.findByUsername(username);
        if(adminOpt.isEmpty()) {
            return null;
        }
        Users admin = adminOpt.get();
        if(!admin.isAdmin()) {
            return null;
        }
        if (movie.getName() == null || movie.getName().isEmpty()) {
            return null;
        }
        if (movie.getPrice() < 0.00) {
            return null;
        }
        if (movie.getUrl() == null || movie.getUrl().isEmpty()) {
            return null;
        }
        if (movie.getDescription() == null) {
            movie.setDescription("");
        }
        return movieDAO.save(movie);
    }

    public Movie getMovieById(int id) throws MovieNotFoundException {
        return movieDAO.findById(id).orElseThrow(() -> new MovieNotFoundException("Movie not found!"));
    }

    public List<Movie> getAllMovies() {
        return movieDAO.findAll();
    }


    public Movie updateMovie(int movieId, Movie updatedMovie, Users currentUser) throws UnauthorizedException {
        if (!currentUser.isAdmin()) {
            throw new UnauthorizedException("Only admins can update movies.");
        }

        Optional<Movie> existingMovie = movieDAO.findById(movieId);
        if (existingMovie.isPresent()) {
            Movie movie = existingMovie.get();
            movie.setName(updatedMovie.getName());
            movie.setPrice(updatedMovie.getPrice());
            movie.setUrl(updatedMovie.getUrl()); // Update url
            movie.setDescription(updatedMovie.getDescription()); // Update description
            // Update other fields as needed

            return movieDAO.save(movie);
        } else {
            throw new RuntimeException("Movie not found with id: " + movieId);
        }
    }

    public boolean deleteMovie(int movieId, Users currentUser) throws UnauthorizedException, MovieNotFoundException {
        if (!currentUser.isAdmin()) {
            throw new UnauthorizedException("Only admins can delete movies.");
        }

        Optional<Movie> existingMovie = movieDAO.findById(movieId);
        if (existingMovie.isPresent()) {
            try {
                movieDAO.delete(existingMovie.get());
                return true; // Movie deleted successfully
            } catch (Exception e) {
                throw new RuntimeException("An error occurred while deleting the movie.", e);
            }
        } else {
            throw new MovieNotFoundException("Movie not found with id: " + movieId);
        }
    }

    public List<Movie> getUnownedMovies(String username) {
        Users user = userDAO.findByUsername(username).orElseThrow(() -> new UserNotFoundException("This user doesn't exist!"));
        return movieDAO.findDistinctByUserNotContaining(user).orElseThrow(() -> new MovieNotFoundException("You have all the movies!"));

    }
    ///random

    public Movie buyMovie(String username, int id) throws UserNotFoundException, MovieNotFoundException, InsufficientFundsException {
        Users user = userDAO.findByUsername(username).orElseThrow(() -> new UserNotFoundException("This user doesn't exist!"));
        Movie movie = movieDAO.findById(id).orElseThrow(() -> new MovieNotFoundException("Movie not found with id:" + id));
        List<Movie> list = user.getMovies();
        list.add(movie);
        user.setMovies(list);
        double balance = user.getBalance();
        if (balance - movie.getPrice() < 0) {
            throw new InsufficientFundsException("Insufficient Funds");
        }
        balance -= movie.getPrice();
        user.setBalance(balance);
        userDAO.save(user);
        return movie;
    }
}


