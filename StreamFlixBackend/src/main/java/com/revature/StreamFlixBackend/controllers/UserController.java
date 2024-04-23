package com.revature.StreamFlixBackend.controllers;

import com.revature.StreamFlixBackend.models.Movie;
import com.revature.StreamFlixBackend.models.Users;
import com.revature.StreamFlixBackend.services.MovieService;
import com.revature.StreamFlixBackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class UserController {

    UserService userService;

    @Autowired
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




}
