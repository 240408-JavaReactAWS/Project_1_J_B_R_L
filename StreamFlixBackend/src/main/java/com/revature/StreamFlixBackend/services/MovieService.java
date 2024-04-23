package com.revature.StreamFlixBackend.services;

import com.revature.StreamFlixBackend.repos.MovieDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovieService {
    private MovieDAO movieDAO;

    @Autowired
    public MovieService(MovieDAO movieDAO) {
        this.movieDAO = movieDAO;
    }
}
