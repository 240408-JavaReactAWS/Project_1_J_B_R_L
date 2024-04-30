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

@Service
public class MovieService {
    private final MovieDAO movieDAO;
    private final UserDAO userDAO;


    @Autowired
    public MovieService(MovieDAO movieDAO, UserDAO userDAO) {
        this.movieDAO = movieDAO;
        this.userDAO = userDAO;
    }

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


