package com.revature.StreamFlixBackend.controllers;

import com.revature.StreamFlixBackend.exceptions.*;
import com.revature.StreamFlixBackend.models.Users;
import com.revature.StreamFlixBackend.models.Movie;
import com.revature.StreamFlixBackend.services.MovieService;
import com.revature.StreamFlixBackend.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
@CrossOrigin(origins = "http://localhost:3000",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH,RequestMethod.PUT, RequestMethod.DELETE},
        allowCredentials = "true")
@RestController
@RequestMapping("movies")
@ResponseBody
public class MovieController {
  
    private final MovieService movieService;
    private UserService userService;


    @Autowired
    public MovieController(MovieService movieService, UserService userService) {
        this.movieService = movieService;
        this.userService = userService;
    }


    @PostMapping
    public ResponseEntity<Movie> createMovie(HttpSession session, @RequestBody Movie movie) {
        Movie addedMovie;
        try {
            Users admin = (Users) session.getAttribute("user");
            if (admin == null || !admin.isAdmin()) {
                throw new UnauthorizedException("You are not authorized to add a movie");
            }
            addedMovie = movieService.addMovie(movie);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(201).body(addedMovie);
    }

    @GetMapping("{id}")
    public ResponseEntity<Movie> getMovieByIdHandler(@PathVariable int id) {
        Movie returnMovie = movieService.getMovieById(id);
        return ResponseEntity.ok(returnMovie);
    }

    @GetMapping()
    public ResponseEntity<List<Movie>> getAllMoviesHandler(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Movie> returnMovies = movieService.getAllMovies();
        return ResponseEntity.ok(returnMovies);
    }

    @GetMapping("store")
    public ResponseEntity<List<Movie>> getAllUnownedMoviesHandler(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Movie> returnMovies = movieService.getUnownedMovies(user.getUsername());
        return ResponseEntity.ok(returnMovies);
    }

    @ExceptionHandler(MovieNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody String handleMessageNotFound(MovieNotFoundException e)
    {
        return e.getMessage();
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable int id, @RequestBody Movie updatedMovie, HttpSession session) {
        try {
            Users admin = (Users) session.getAttribute("user");
            if (admin == null || !admin.isAdmin()) {
                throw new UnauthorizedException("You are not authorized to update a movie");
            }
            //Users currentUser = userService.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            Movie movie = movieService.updateMovie(id, updatedMovie, admin);
            return ResponseEntity.ok(movie);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable int id, HttpSession session) {
        try {
            Users admin = (Users) session.getAttribute("user");
            if (admin == null || !admin.isAdmin()) {
                throw new UnauthorizedException("You are not authorized to delete a movie");
            }
            //Users currentUser = userService.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            boolean deleted = movieService.deleteMovie(id, admin);
            return deleted ? ResponseEntity.ok("Movie deleted successfully") : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete the movie");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (MovieNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the movie.");
        }
    }

    @PostMapping("/buy/{id}")
    public ResponseEntity<Movie> buyMovie(HttpSession session, @PathVariable int id) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Movie movie = movieService.buyMovie(user.getUsername(), id);
        session.setAttribute("user", userService.findByUsername(user.getUsername()).orElseThrow(()->new UserNotFoundException("User not found")));
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public @ResponseBody String handleInsufficientFundsException(InsufficientFundsException e) {
        return e.getMessage();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleUserNotFoundException(UserNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(AlreadyOwnedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleAlreadyOwnedException(AlreadyOwnedException e) {
        return e.getMessage();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody String handleUnauthorizedException(UnauthorizedException e) {
        return e.getMessage();

    }

//    @ExceptionHandler(MovieNotFoundException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public @ResponseBody String handleMovieNotFoundException(MovieNotFoundException e) {
//        return e.getMessage();
//    }

}
