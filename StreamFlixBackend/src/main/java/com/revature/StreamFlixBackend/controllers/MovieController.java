package com.revature.StreamFlixBackend.controllers;

import com.revature.StreamFlixBackend.services.MovieService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;


    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }
}
