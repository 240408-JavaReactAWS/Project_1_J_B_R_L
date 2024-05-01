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

/*
 * This class is a controller for the Movie model. It handles all the requests related to the Movie model.
 * It has methods to add, update, delete, and get movies. It also has a method to buy a movie.
 * This is a Rest Controller that listens for HTTP requests on the /movies path.
 * @Author: Ryan Sherk, Luis Garcia, Jeff Gomez, Brian Bollivar
 */
@CrossOrigin(origins = "http://localhost:3000",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH,RequestMethod.PUT, RequestMethod.DELETE},
        allowCredentials = "true")
@RestController
@RequestMapping("movies")
@ResponseBody
public class MovieController {

    /* This is a MovieService object that is used to interact with the database. */
    private final MovieService movieService;
    /* This is a UserService object that is used to interact with the database. */
    private UserService userService;


    /*
     * This is a constructor for the MovieController class. It is used to instantiate a new MovieController object.
     * It is Autowired using Spring to inject the MovieService and UserService objects.
     */
    @Autowired
    public MovieController(MovieService movieService, UserService userService) {
        this.movieService = movieService;
        this.userService = userService;
    }


    /*
     * This method is used to create a new movie. It listens for POST requests on the /movies path.
     * The Request body should contain a JSON object with the movie's name, price, url, snapshot, and description.
     * The session should contain a user object with the user's information and the user must be an admin.
     * @Return A response entity with the added movie object and a status code of 201 if the movie was created successfully.
     */
    @PostMapping
    public ResponseEntity<?> createMovie(HttpSession session, @RequestBody Movie movie) {
        Movie addedMovie;
        try {
            Users admin = (Users) session.getAttribute("user");
            if (admin == null || !admin.isAdmin()) {
                throw new UnauthorizedException("You are not authorized to add a movie");
            }
            addedMovie = movieService.addMovie(movie);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (InvalidMovieException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(201).body(addedMovie);
    }

    /*
     * This method is used to get a movie by its id. It listens for GET requests on the /movies/{id} path.
     * The id is a path variable that is used to identify the movie.
     * @Return A response entity with the movie object and a status code of 200 if the movie was found successfully.
     */
    @GetMapping("{id}")
    public ResponseEntity<Movie> getMovieByIdHandler(@PathVariable int id) {
        Movie returnMovie = movieService.getMovieById(id);
        return ResponseEntity.ok(returnMovie);
    }

    /*
     * This method is used to get all movies. It listens for GET requests on the /movies path.
     * The session should contain a user object with the user's information.
     * @Return A response entity with a list of all movies and a status code of 200 if the movies were found successfully.
     */
    @GetMapping()
    public ResponseEntity<List<Movie>> getAllMoviesHandler(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Movie> returnMovies = movieService.getAllMovies();
        return ResponseEntity.ok(returnMovies);
    }

    /*
     * This method is used to get all movies that the user does not own. It listens for GET requests on the /movies/store path.
     * The session should contain a user object with the user's information.
     * @Return A response entity with a list of all movies that the user does not own and a status code of 200 if the movies were found successfully.
     */
    @GetMapping("store")
    public ResponseEntity<List<Movie>> getAllUnownedMoviesHandler(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Movie> returnMovies = movieService.getUnownedMovies(user.getUsername());
        return ResponseEntity.ok(returnMovies);
    }

    /*
     * This is an Exception Handler for the MovieNotFoundException thrown by movieService.
     * @Return It returns a response body with the exception message and a status code of 404.
     */
    @ExceptionHandler(MovieNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody String handleMessageNotFound(MovieNotFoundException e)
    {
        return e.getMessage();
    }

    /*
     * This method is used to update a movie. It listens for PUT requests on the /movies/{id} path.
     * The id is a path variable that is used to identify the movie.
     * The Request body should contain a JSON object with the updated movie's name, price, url, snapshot, and description.
     * The session should contain a user object with the user's information and the user must be an admin.
     * @Return A response entity with the updated movie object and a status code of 200 if the movie was updated successfully.
     */
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

    /*
     * This method is used to delete a movie. It listens for DELETE requests on the /movies/{id} path.
     * The id is a path variable that is used to identify the movie.
     * The session should contain a user object with the user's information and the user must be an admin.
     * @Return A response entity with a message and a status code of 200 if the movie was deleted successfully.
     */
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

    /*
     * This method is used to buy a movie. It listens for POST requests on the /movies/buy/{id} path.
     * The id is a path variable that is used to identify the movie.
     * The session should contain a user object with the user's information.
     * @Return A response entity with the movie object and a status code of 200 if the movie was bought successfully.
     */
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

    /*
     * This is an Exception Handler for the InsufficientFundsException thrown by movieService.
     * @Return It returns a response body with the exception message and a status code of 402.
     */
    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public @ResponseBody String handleInsufficientFundsException(InsufficientFundsException e) {
        return e.getMessage();
    }

    /*
     * This is an Exception Handler for the UserNotFoundException thrown by movieService.
     * @Return It returns a response body with the exception message and a status code of 400.
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleUserNotFoundException(UserNotFoundException e) {
        return e.getMessage();
    }

    /*
     * This is an Exception Handler for the AlreadyOwnedException thrown by movieService.
     * @Return It returns a response body with the exception message and a status code of 400.
     */
    @ExceptionHandler(AlreadyOwnedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleAlreadyOwnedException(AlreadyOwnedException e) {
        return e.getMessage();
    }

    /*
     * This is an Exception Handler for the UnauthorizedException thrown by movieService.
     * @Return It returns a response body with the exception message and a status code of 401.
     */
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
