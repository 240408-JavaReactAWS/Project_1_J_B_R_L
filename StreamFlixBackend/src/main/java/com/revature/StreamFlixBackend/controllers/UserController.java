package com.revature.StreamFlixBackend.controllers;

import com.revature.StreamFlixBackend.models.Movie;
import com.revature.StreamFlixBackend.services.MovieService;

import com.revature.StreamFlixBackend.exceptions.InvalidRegistrationException;
import com.revature.StreamFlixBackend.exceptions.UserAlreadyExistsException;
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
        try {
            loginUser = userService.loginUser(user.getUsername(), user.getPassword());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(loginUser, HttpStatus.OK);
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
}
