package com.revature.StreamFlixBackend.controllers;

import com.revature.StreamFlixBackend.exceptions.MovieNotFoundException;
import com.revature.StreamFlixBackend.models.Movie;
import com.revature.StreamFlixBackend.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("movies")
@ResponseBody
public class MovieController {
    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Movie> getMovieByIdHandler(@PathVariable int id) {
        Movie returnMovie = movieService.getMovieById(id);
        return ResponseEntity.ok(returnMovie);
    }

    @ExceptionHandler(MovieNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody String handleMessageNotFound(MovieNotFoundException e)
    {
        return e.getMessage();
    }
}
