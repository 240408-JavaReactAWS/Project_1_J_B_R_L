package com.revature.StreamFlixBackend.controllers;

import com.revature.StreamFlixBackend.exceptions.*;
import com.revature.StreamFlixBackend.models.Movie;
import com.revature.StreamFlixBackend.services.MovieService;

import com.revature.StreamFlixBackend.models.Users;
import com.revature.StreamFlixBackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
@ResponseBody
public class UserController {

    @Autowired
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
  
    @PostMapping("login")
    public ResponseEntity<Users> loginUser(@RequestBody Users user) {
        Users loginUser;

        if (user.getUsername() == null || user.getPassword() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        loginUser = userService.loginUser(user.getUsername(), user.getPassword());
        return new ResponseEntity<>(loginUser, HttpStatus.OK);
    }

    @GetMapping("/movies")
    public ResponseEntity<List<Movie>> getPurchasedMovies(@RequestHeader(name = "user", required = false) String username) {
        if (username == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Movie> movies = userService.getMoviesByUsername(username);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<List<Movie>> getPurchasedMoviesById(@RequestHeader(name = "user", required = false) String username,
                                                              @PathVariable int id) {
        if (username == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Movie> movies = userService.getMoviesByUserId(username, id);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }


    @PatchMapping(value = "/users/{id}")
    public ResponseEntity<Users> resetPassword(@PathVariable int id, @RequestBody Users user) {
        Users updatedUser = userService.resetUserPassword(id, user);
        if (updatedUser != null) {
            return ResponseEntity.ok().body(updatedUser);
        } else {
            return ResponseEntity.status(400).build();
        }
    }

    @PostMapping("register")
    public ResponseEntity<Users> registerUserHandler(@RequestBody Users user) {
        return new ResponseEntity<>(userService.registerUser(user), HttpStatus.CREATED);
    }

    @ExceptionHandler(InvalidRegistrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleInvalidRegistration(InvalidRegistrationException e)
    {
        return e.getMessage();
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody String handleUserAlreadyExists(UserAlreadyExistsException e)
    {
        return e.getMessage();
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleInvalidPasswordException(InvalidPasswordException e) {
        return e.getMessage();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody String handleNotAuthorizedException(UnauthorizedException e) {
        return e.getMessage();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleUserNotFoundException(UserNotFoundException e) {
        return e.getMessage();
    }
}
