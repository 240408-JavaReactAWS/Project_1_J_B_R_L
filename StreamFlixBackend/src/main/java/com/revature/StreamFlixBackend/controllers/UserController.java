package com.revature.StreamFlixBackend.controllers;


import com.revature.StreamFlixBackend.exceptions.InvalidRegistrationException;
import com.revature.StreamFlixBackend.exceptions.UserAlreadyExistsException;
import com.revature.StreamFlixBackend.models.Users;
import com.revature.StreamFlixBackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
@ResponseBody
public class UserController {

    @Autowired
    private final UserService userService;

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
