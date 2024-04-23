package com.revature.StreamFlixBackend.controllers;

import com.revature.StreamFlixBackend.models.Movie;
import com.revature.StreamFlixBackend.models.Users;
import com.revature.StreamFlixBackend.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class MovieController {

    private MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/myMovies")
    public ResponseEntity<List<Movie>> getPurchasedMovies(@RequestHeader(name = "user", required = false) String username) {
        if (username == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Movie> movies = new ArrayList<Movie>();

        try {
            movies = movieService.getMoviesByUsername(username);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<List<Movie>> getPurchasedMoviesById(@RequestHeader(name = "user", required = false) String username,
                                                          @PathVariable int id) {
        if (username == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Movie> movies = new ArrayList<Movie>();

        try {
            movies = movieService.getMoviesByUserId(username, id);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(movies, HttpStatus.OK);
    }
}
