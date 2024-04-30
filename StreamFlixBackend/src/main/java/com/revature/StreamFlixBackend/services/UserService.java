package com.revature.StreamFlixBackend.services;


import com.revature.StreamFlixBackend.exceptions.*;
import com.revature.StreamFlixBackend.models.Movie;
import com.revature.StreamFlixBackend.models.OneTimePassword;
import com.revature.StreamFlixBackend.repos.MovieDAO;

import com.revature.StreamFlixBackend.models.Users;

import com.revature.StreamFlixBackend.repos.OneTimePasswordDAO;
import com.revature.StreamFlixBackend.repos.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/*
 * UserService class for StreamFlix
 * This class is used to take requests from the controller and perform operations on the users table.
 */
@Service
public class UserService {
    /* userDAO is the UserDAO object used to interact with the users table */
    private final UserDAO userDAO;
    /* movieDAO is the MovieDAO object used to interact with the movies table */
    private final MovieDAO movieDAO;
    /* oneTimePasswordDAO is the OneTimePasswordDAO object used to interact with the one_time_password table */
    private final OneTimePasswordDAO oneTimePasswordDAO;


    /*
     * Constructor for UserService
     * @param userDAO the UserDAO object to set
     * @param movieDAO the MovieDAO object to set
     * @param oneTimePasswordDAO the OneTimePasswordDAO object to set
     */
    @Autowired
    public UserService(UserDAO userDAO, MovieDAO movieDAO, OneTimePasswordDAO oneTimePasswordDAO) {
        this.userDAO = userDAO;
        this.movieDAO = movieDAO;
        this.oneTimePasswordDAO = oneTimePasswordDAO;
    }


    /*
     * Logs in a user
     * @param username the username to log in
     * @param password the password to log in
     * @return the user logged in
     */
    public Users loginUser(String username, String password) throws UserNotFoundException, InvalidPasswordException {
        Optional<Users> loginUser = userDAO.findByUsername(username);

        if (loginUser.isEmpty()) {
            throw new UserNotFoundException("No user was found");
        }
        if (loginUser.get().getPassword().equals(password)) {
            return loginUser.get();
        } else {
            throw new InvalidPasswordException("Wrong Password");
        }
    }

    /*
     * Finds the user by username
     * @param username the username to find the user by
     * @return the user found by username
     */
    public Optional<Users> findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    /*
     * Finds the user by email
     * @param email the email to find the user by
     */
    public Users getUserByEmail(String email) {
        return userDAO.findByEmail(email).orElse(null);
    }

    /*
     * Registers a new user
     * @param user the user to register
     * @return the user registered
     */
    public Users registerUser(Users user) throws InvalidRegistrationException, UserAlreadyExistsException {
        String username = user.getUsername();
        String password = user.getPassword();
        String email = user.getEmail();
        if (username == null || password == null)
            throw new InvalidRegistrationException("Unable to register new user:" +
                    username + ". Username and password must be specified.");
        else if (username.length() < 4 || password.length() < 4)
            throw new InvalidRegistrationException("Unable to register new user:" +
                    username + ". Username and password must be at least four characters.");
        else if (!Pattern.compile("^(.+)@(.+)$").matcher(email).matches())
            throw new InvalidRegistrationException("Unable to register new user:" +
                    username + ". Email must be valid.");
        else if (userDAO.findByEmail(email).isPresent())
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists!");
        else if (userDAO.findByUsername(username).isPresent())
            throw new UserAlreadyExistsException("User " + username + " already exists!");
        else if (user.getName().isEmpty())
            throw new InvalidRegistrationException("Name must be specified");
        else return userDAO.save(user);
    }

    /*
     * Gets all users
     * @return a list of all users
     */
    public List<Users> getAllUsers() {
        return userDAO.findAll();
    }

    /*
     * Saves an OTP
     * @param otp the OTP to save
     */
    public void saveOTP(OneTimePassword otp) {
        Optional<OneTimePassword> otpRecord = oneTimePasswordDAO.findByUser(otp.getUser());
        otpRecord.ifPresent(oneTimePasswordDAO::delete);
        oneTimePasswordDAO.save(otp);
    }

    /*
     * Resets a user's password
     * @param currentUser the current user
     * @param userPatch the user object containing the new password
     * @return the user with the new password
     */
    public Users resetPassword(Users currentUser, Users userPatch) throws UserNotFoundException, InvalidPasswordException {;
        Optional<Users> userOpt = userDAO.findByUsername(currentUser.getUsername());
        if (userOpt.isEmpty()) {
            throw new UnauthorizedException("No such user found");
        }
        if (userPatch.getPassword().length() < 4) {
            throw new InvalidPasswordException("Password must be at least 4 characters");
        }
        Users user = userOpt.get();
        user.setPassword(userPatch.getPassword());
        return userDAO.save(user);
    }

    /*
     * Gets a movie by User
     * @param user the user to get the movie by
     * @return the movie found by user
     */
    public List<Movie> getMoviesByUser(Users user) {
        return movieDAO.getMoviesByUser(user);
    }

    //Depreciated method
    public List<Movie> getMoviesByUsername(String username) throws UserNotFoundException {
        Optional<Users> user = userDAO.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("No user was found");
        }
        return movieDAO.getMoviesByUser(user.get());
    }


    /*
     * Gets a movie by User Id
     * @param id the id of the user to get the movie by
     * @return List of the movies found by user id
     */
    public List<Movie> getMoviesByUserId(int id) throws UserNotFoundException, UnauthorizedException{
//        Optional<Users> user = userDAO.findByUsername(username);
//        if (user.isEmpty()) {
//            throw new UserNotFoundException("Username not found");
//        }
//        if (!user.get().isAdmin()) {
//            throw new UnauthorizedException("User is not admin");
//        }
        Optional<Users> getUser = userDAO.findById(id);
        if (getUser.isEmpty()) {
            throw new UserNotFoundException("User id was not found");
        }
        return movieDAO.getMoviesByUser(getUser.get());
    }

    /*
     * Adds money to a users account
     * @param username the username to add money to
     * @param amount the amount to add
     * @return the user with the new balance
     */
    public Users addMoney(String username, double amount) {
        Users currentUser = userDAO.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (amount < 0.0) {
            throw new InsufficientFundsException("Amount must be positive");
        }
        currentUser.setBalance(currentUser.getBalance() + amount);
        return userDAO.save(currentUser);
    }

    /*
     * Verifies an email
     * @param otp the OTP to verify
     * @param email the email to verify
     * @return the user verified
     */
    public Users verifyEmail(int otp, String email) {
        Users user = userDAO.findByEmail(email).orElse(null);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        OneTimePassword otpRecord = oneTimePasswordDAO.findByUserAndOtp(user, otp).orElse(null);
        if (otpRecord == null) {
            throw new InvalidOTPException("Invalid OTP");
        }
        if (otpRecord.getExpirationDate().before(new java.util.Date())) {
            oneTimePasswordDAO.delete(otpRecord);
            throw new OTPExpirationException("OTP has expired");
        }
        oneTimePasswordDAO.delete(otpRecord);
        return user;
    }

    /*
     * Sets a user as an admin
     * @param id the id of the user to set as an admin
     * @return the user set as an admin
     */
    public Users setAdmin(int id) {
        Optional<Users> user = userDAO.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        Users userToChange = user.get();
        userToChange.setAdmin(true);
        return userDAO.save(userToChange);
    }
}
