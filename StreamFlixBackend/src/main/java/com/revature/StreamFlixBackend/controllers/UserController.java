package com.revature.StreamFlixBackend.controllers;

import  java.util.Date;
import com.revature.StreamFlixBackend.exceptions.*;
import com.revature.StreamFlixBackend.models.Movie;

import com.revature.StreamFlixBackend.models.OneTimePassword;
import com.revature.StreamFlixBackend.models.Users;
import com.revature.StreamFlixBackend.services.EmailService;
import com.revature.StreamFlixBackend.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH,RequestMethod.PUT, RequestMethod.DELETE},
        allowCredentials = "true")
@RequestMapping("users")
@ResponseBody
public class UserController {


    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }
  
    @PostMapping("login")
    public ResponseEntity<Users> loginUser(@RequestBody Users user, HttpSession session) {
        Users loginUser;
        try {
            if (user.getUsername() == null || user.getPassword() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            loginUser = userService.loginUser(user.getUsername(), user.getPassword());
            session.setAttribute("user", loginUser);
        } catch (UserNotFoundException | InvalidPasswordException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(loginUser, HttpStatus.OK);
    }

    @GetMapping("session")
    public ResponseEntity<Users> getSessionUser(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("admin")
    public ResponseEntity<Boolean> isAdmin(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if(!user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(user.isAdmin(), HttpStatus.OK);
    }

    @PostMapping("logout")
    public ResponseEntity<Void> logoutUser(HttpSession session) {
        session.removeAttribute("user");
        session.invalidate();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/myMovies")
    public ResponseEntity<List<Movie>> getPurchasedMovies(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Movie> movies = userService.getMoviesByUser(user);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<List<Movie>> getPurchasedMoviesByUserId(HttpSession session, @PathVariable int id) {
        Users admin = (Users) session.getAttribute("user");
        if (admin == null || !admin.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
//        Users user = userService.getUserById(id);
//        if (user == null || id <= 0) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
        List<Movie> movies = userService.getMoviesByUserId(id);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @PostMapping("{email}/forgotPassword")
    public ResponseEntity<String> forgotPassword(@PathVariable String email) {
        Users user;
        try {
            user = userService.getUserByEmail(email);
            if (user == null) {
                throw new UserNotFoundException("This email is not registered");
            }
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        int otp = createOTP();
        OneTimePassword otpRecord = new OneTimePassword(otp, new Date(System.currentTimeMillis() + 100000), user);
        userService.saveOTP(otpRecord);
        String message = "Your OTP is: " + otp + " and it will expire in 100 seconds." + "\n" +
                            "Please go to the following link to reset your password: " +
                            "http://localhost:3000/reset-password?email=" + email;
        emailService.sendEmail(email, "StreamFlix Password Reset [Time Sensitive]", message);

        return new ResponseEntity<>("You should be receiving an email soon", HttpStatus.OK);
    }

    //Reset password using Http sessions and email service
    @PostMapping(value = "{email}/verifyEmail/{otp}")
    public ResponseEntity<?> verifyEmail(@PathVariable String email, @PathVariable int otp, HttpSession session) {
        Users verifiedUser;
        try {
            verifiedUser = userService.verifyEmail(otp, email);
        } catch (InvalidOTPException | UserNotFoundException | OTPExpirationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
        if (verifiedUser == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            session.setAttribute("user", verifiedUser);
            return new ResponseEntity<>(verifiedUser, HttpStatus.OK);
        }
    }

    @PatchMapping("resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody Users user, HttpSession session) {
        Users updatedUser;
        try {
            Users currentUser = (Users) session.getAttribute("user");
            updatedUser = userService.resetPassword(currentUser, user);
            session.setAttribute("user", updatedUser);
        } catch (InvalidPasswordException | UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PatchMapping("admin/setAdmin/{id}")
    public ResponseEntity<?> setAdmin(@PathVariable int id, HttpSession session) {
        Users admin = (Users) session.getAttribute("user");
        if (admin == null || !admin.isAdmin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to set an admin");
        }
        Users user;
        try {
            user = userService.setAdmin(id);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }



    @PatchMapping("addMoney")
    public ResponseEntity<Users> addMoneyHandler(HttpSession session, @RequestBody Users userWithMoney) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Users updatedUser = userService.addMoney(user.getUsername(), userWithMoney.getBalance());
        session.setAttribute("user", updatedUser);
        if (updatedUser != null) {
            return ResponseEntity.ok().body(updatedUser);
        } else {
            return ResponseEntity.status(400).build();
        }
    }


    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Users> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<Users> registerUserHandler(@RequestBody Users user, HttpSession session) {
        Users newUser;
        try {
            newUser = userService.registerUser(user);
            session.setAttribute("user", newUser);
        } catch (InvalidRegistrationException | UserAlreadyExistsException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
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
    private int createOTP() {
        return (int) (Math.random() * 9000) + 1000;
    }
}
