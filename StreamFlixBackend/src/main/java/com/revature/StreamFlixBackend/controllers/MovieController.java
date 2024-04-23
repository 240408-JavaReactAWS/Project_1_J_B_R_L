package com.revature.StreamFlixBackend.controllers;

import com.revature.StreamFlixBackend.models.Users;
import com.revature.StreamFlixBackend.exceptions.MovieNotFoundException;
import com.revature.StreamFlixBackend.models.Movie;
import com.revature.StreamFlixBackend.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("movies")
@ResponseBody
public class MovieController {
  
    private final MovieService movieService;

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

    @GetMapping("/getUser/{id}")
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

    @GetMapping("{id}")
    public ResponseEntity<Movie> getMovieByIdHandler(@PathVariable int id) {
        Movie returnMovie = movieService.getMovieById(id);
        return ResponseEntity.ok(returnMovie);
    }

    @GetMapping()
    public ResponseEntity<List<Movie>> getAllMoviesHandler() {
        List<Movie> returnMovies = movieService.getAllMovies();
        return ResponseEntity.ok(returnMovies);
    }

    @GetMapping("store")
    public ResponseEntity<List<Movie>> getAllUnownedMoviesHandler(@RequestHeader(name = "user", required = false) String username) {
        List<Movie> returnMovies = movieService.getUnownedMovies(username);
        return ResponseEntity.ok(returnMovies);
    }

    @ExceptionHandler(MovieNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody String handleMessageNotFound(MovieNotFoundException e)
    {
        return e.getMessage();
    }


}
