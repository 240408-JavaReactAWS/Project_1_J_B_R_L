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

/*
 * This class is a RestController that handles all the endpoints related to the User entity.
 * It uses the UserService to interact with the database and perform the necessary operations.
 * It also uses the EmailService to send emails to users.
 * @Author: Ryan Sherk, Luis Garcia, Jeff Gomez, Brian Bollivar
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH,RequestMethod.PUT, RequestMethod.DELETE},
        allowCredentials = "true")
@RequestMapping("users")
@ResponseBody
public class UserController {


    /* The UserService object that will be used to interact with the database */
    private final UserService userService;
    /* The EmailService object that will be used to send emails */
    private final EmailService emailService;

    /*
     * This constructor is used to inject the UserService and EmailService objects into the UserController
     */
    @Autowired
    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    /*
     * This method is used to handle the login of a user.
     * It takes in a Users object and a HttpSession object.
     * @Return ResponseEntity<Users> - The user that was logged in
     */
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

    /*
     * This method is used to get the user that is currently logged in.
     * It takes in a HttpSession object.
     * @Return ResponseEntity<Users> - The user that is currently logged in
     */
    @GetMapping("session")
    public ResponseEntity<Users> getSessionUser(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /*
     * This method is used to check if the user that is currently logged in is an admin.
     * It takes in a HttpSession object with a user.
     * @Return ResponseEntity<Boolean> - True if the user is an admin, false otherwise
     */
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

    /*
     * This method is used to logout the user that is currently logged in.
     * It takes in a HttpSession object.
     * @Return ResponseEntity<Void> - An empty response entity
     */
    @PostMapping("logout")
    public ResponseEntity<Void> logoutUser(HttpSession session) {
        session.removeAttribute("user");
        session.invalidate();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*
     * This method is used to get all the movies that the user has purchased.
     * It takes in a HttpSession object.
     * @Return ResponseEntity<List<Movie>> - A list of movies that the user has purchased
     */
    @GetMapping("/myMovies")
    public ResponseEntity<List<Movie>> getPurchasedMovies(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Movie> movies = userService.getMoviesByUser(user);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    /*
     * This method is used to get all the movies that a user has purchased by the users Id.
     * It takes in a HttpSession object with a user that must be an admin.
     * It takes in a int as a path variable that is the id of the user who's movies are being retrieved.
     * @Return ResponseEntity<List<Movie>> - A list of movies that the user has purchased.
     */
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

    /*
     * This method is used to begin the process of changing a users password.
     * The method uses the emailService object to send an OTP to the email provided.
     * It takes in a string as a path variable that is the email of the user whose password is being reset.
     * @Return ResponseEntity<String> - A response entity with a message that the user should be receiving an email soon.
     */
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
    /*
     * This message is used to verify that the OTP provided by the user is correct.
     * It takes in a string as a path variable that is the email of the user whose password is being reset.
     * It takes in a int as a path variable that is the OTP that the user provided.
     * @Return ResponseEntity<?> - A response entity with the user that was verified.
     */
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

    /*
     * This method is used to reset the password of a user.
     * It takes in a Users object that contains the new password.
     * It takes in a HttpSession object that contains the user that is currently logged in.
     * @Return ResponseEntity<?> - A response entity with the user that was updated.
     */
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

    /*
     * This method is used to set a user as an admin.
     * It takes in a int as a path variable that is the id of the user that is being set as an admin.
     * It takes in a HttpSession object that contains the user that is currently logged in.
     * @Return ResponseEntity<?> - A response entity with the user that was updated.
     */
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



    /*
     * This method is used to add money to a user's balance.
     * It takes in a Users object that contains the amount of money to be added.
     * It takes in a HttpSession object that contains the user that is currently logged in.
     * @Return ResponseEntity<Users> - A response entity with the user that was updated.
     */
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

    /*
     * This method is used to get all the users in the database.
     * It takes in a HttpSession object that contains the user that is currently logged in. This user must be an admin
     * @Return ResponseEntity<List<Users>> - A response entity with a list of all the users in the database.
     */
    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Users> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /*
     * This method is used to register a new user.
     * It takes in a Users object that contains the information of the new user.
     * It takes in a HttpSession object that is updated with the new user that was registered.
     * @Return ResponseEntity<Users> - A response entity with the user that was registered.
     */
    @PostMapping("register")
    public ResponseEntity<Users> registerUserHandler(@RequestBody Users user, HttpSession session) {
        Users newUser;
        newUser = userService.registerUser(user);
        session.setAttribute("user", newUser);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    /*
     * This is an Exception Handler to handle an Invalid Registration Exception.
     * @Return String - A message that the registration was invalid.
     */
    @ExceptionHandler(InvalidRegistrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleInvalidRegistration(InvalidRegistrationException e)
    {
        return e.getMessage();
    }

    /*
     * This is an Exception Handler to handle an Insufficient Funds Exception.
     * @Return String - A message that the user has insufficient funds.
     */
    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleInsufficientFundsException(InsufficientFundsException e)
    {
        return e.getMessage();
    }

    /*
     * This is an Exception Handler to handle an User Already Exists Exception.
     * @Return String - A message that the user already exists.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody String handleUserAlreadyExists(UserAlreadyExistsException e)
    {
        return e.getMessage();
    }

    /*
     * This is an Exception Handler to handle an Invalid Password Exception.
     * @Return String - A message that the password is invalid.
     */
    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleInvalidPasswordException(InvalidPasswordException e) {
        return e.getMessage();
    }

    /*
     * This is an Exception Handler to handle an Unauthorized Exception.
     * @Return String - A message that the user is unauthorized.
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody String handleNotAuthorizedException(UnauthorizedException e) {
        return e.getMessage();
    }

    /*
     * This is an Exception Handler to handle an User Not Found Exception.
     * @Return String - A message that the user was not found.
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleUserNotFoundException(UserNotFoundException e) {
        return e.getMessage();
    }

    /*
     * This is a method to create a random OTP.
     * @Return int - A random OTP
     */
    private int createOTP() {
        return (int) (Math.random() * 9000) + 1000;
    }
}
