package com.revature.StreamFlixBackend.services;



import com.revature.StreamFlixBackend.exceptions.*;

import com.revature.StreamFlixBackend.models.Users;
import com.revature.StreamFlixBackend.models.Movie;
import com.revature.StreamFlixBackend.repos.MovieDAO;
import com.revature.StreamFlixBackend.repos.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/*
 * MovieService class for StreamFlix
 * This class is used to take requests from the controller and perform operations on the movies table.
 */
@Service
public class MovieService {
    /* movieDAO is the MovieDAO object used to interact with the movies table */
    private final MovieDAO movieDAO;
    /* userDAO is the UserDAO object used to interact with the users table */
    private final UserDAO userDAO;


    /*
     * Constructor for MovieService
     * @param movieDAO the MovieDAO object to set
     * @param userDAO the UserDAO object to set
     */
    @Autowired
    public MovieService(MovieDAO movieDAO, UserDAO userDAO) {
        this.movieDAO = movieDAO;
        this.userDAO = userDAO;
    }

    /*
     * Adds a movie to the movies table
     * @param movie the movie to add
     * @return the movie added
     */
    public Movie addMovie(Movie movie) {
//        Optional<Users> adminOpt = userDAO.findByUsername(username);
//        if(adminOpt.isEmpty()) {
//            return null;
//        }
//        Users admin = adminOpt.get();
//        if(!admin.isAdmin()) {
//            return null;
//        }

        if (movie.getName() == null || movie.getName().isEmpty() || movie.getName().length() > 3000) {
            throw new InvalidMovieException("Invalid movie name");
        }
        if (movie.getPrice() < 0.00) {
            throw new InvalidMovieException("Invalid movie price");
        }
        if (movie.getUrl() == null || movie.getUrl().isEmpty() || movie.getUrl().length() > 3000) {
            throw new InvalidMovieException("Invalid movie url");
        }
        if (movie.getSnapshot() == null ) {
            movie.setSnapshot("");
        } else if (movie.getSnapshot().length() > 3000) {
            throw new InvalidMovieException("The Movie's Snapshot URL is too long. Please enter a shorter one.");
        }
        if (movie.getDescription() == null) {
            movie.setDescription("");
        } else if (movie.getDescription().length() > 3000) {
            throw new InvalidMovieException("Movie description is too long. Please enter a shorter one.");
        }
        return movieDAO.save(movie);
    }

    /*
     * Gets a movie by id
     * @param id the id of the movie to get
     * @return the movie found by id
     */
    public Movie getMovieById(int id) throws MovieNotFoundException {
        return movieDAO.findById(id).orElseThrow(() -> new MovieNotFoundException("Movie not found!"));
    }

    /*
     * Gets all movies
     * @return a list of all movies
     */
    public List<Movie> getAllMovies() {
        return movieDAO.findAll();
    }


    /*
     * Updates a movie by id
     * @param movieId the id of the movie to update
     * @param updatedMovie the updated movie
     * @param currentUser the current user that must be an admin
     * @return the updated movie
     */
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
            movie.setSnapshot(updatedMovie.getSnapshot()); // Update
            movie.setDescription(updatedMovie.getDescription()); // Update description


            return movieDAO.save(movie);
        } else {
            throw new RuntimeException("Movie not found with id: " + movieId);
        }
    }

    /*
     * Deletes a movie by id
     * @param movieId the id of the movie to delete
     * @param currentUser the current user that must be an admin
     * @return true if the movie was deleted successfully
     */
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

    /*
     * Gets all movies that the user doesn't own
     * @param username the username of the user
     * @return a list of movies that the user doesn't own
     */
    public List<Movie> getUnownedMovies(String username) {
        Users user = userDAO.findByUsername(username).orElseThrow(() -> new UserNotFoundException("This user doesn't exist!"));
        return movieDAO.findDistinctByUserNotContaining(user).orElseThrow(() -> new MovieNotFoundException("You have all the movies!"));

    }
    ///random

    /*
     * Buys a movie
     * @param username the username of the user
     * @param id the id of the movie to buy
     * @return the movie bought
     */
    public Movie buyMovie(String username, int id) throws UserNotFoundException, MovieNotFoundException, InsufficientFundsException {
        Users user = userDAO.findByUsername(username).orElseThrow(() -> new UserNotFoundException("This user doesn't exist!"));
        Movie movie = movieDAO.findById(id).orElseThrow(() -> new MovieNotFoundException("Movie not found with id:" + id));
        List<Movie> list = user.getMovies();
        if (list.contains(movie)) {
            throw new AlreadyOwnedException("The user already owns this movie.");
        }
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


