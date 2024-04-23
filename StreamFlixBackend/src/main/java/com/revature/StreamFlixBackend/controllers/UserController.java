package com.revature.StreamFlixBackend.controllers;

import com.revature.StreamFlixBackend.models.Users;
import com.revature.StreamFlixBackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
}
