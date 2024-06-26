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


import java.security.MessageDigest;
import java.security.SecureRandom;
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
        username = username.toLowerCase();
        Optional<Users> loginUser = userDAO.findByUsername(username);

        if (loginUser.isEmpty()) {
            throw new UserNotFoundException("No user was found");
        }
        if (validatePassword(password, loginUser.get())) {
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
        if (username == null) {
            throw new InvalidRegistrationException("Username must be specified");
        }
        if (username.contains(" ")) {
            throw new InvalidRegistrationException("Username cannot contain spaces");
        }
        username = username.toLowerCase();
        if (password == null)
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
        try {
            byte[] salt = new byte[2];
            SecureRandom.getInstanceStrong().nextBytes(salt);
            user.setSalt(salt);
            user.setPassword(getHashedPassword(password, salt));
//            byte[] passwordBytes = password.getBytes();
//            // Concatenate the passwordBytes and salt
//            byte[] saltedPassword = new byte[salt.length + passwordBytes.length];
//            System.arraycopy(passwordBytes, 0, saltedPassword, 0, passwordBytes.length);
//            System.arraycopy(salt, 0, saltedPassword, passwordBytes.length, salt.length);
//
//
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            byte[] hash = md.digest(saltedPassword);
//            user.setPassword(bytesToHex(hash));
        } catch (Exception e) {
            throw new InvalidRegistrationException("Unable to register new user:" +
                    username);
        }
        return userDAO.save(user);
    }

    /*
     * Validates a user's password
     * @param password the password to validate
     * @param user the user to validate the password for
     */
    private static Boolean validatePassword(String password, Users user) {
        byte[] salt = user.getSalt();
        byte[] passwordBytes = password.getBytes();
        byte[] saltedPassword = new byte[salt.length + passwordBytes.length];
        System.arraycopy(passwordBytes, 0, saltedPassword, 0, passwordBytes.length);
        System.arraycopy(salt, 0, saltedPassword, passwordBytes.length, salt.length);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(saltedPassword);
            return user.getPassword().equals(bytesToHex(hash));
        } catch (Exception e) {
            return false;
        }
    }

    private static String getHashedPassword(String password, byte[] salt) {
        byte[] passwordBytes = password.getBytes();
        byte[] saltedPassword = new byte[salt.length + passwordBytes.length];
        System.arraycopy(passwordBytes, 0, saltedPassword, 0, passwordBytes.length);
        System.arraycopy(salt, 0, saltedPassword, passwordBytes.length, salt.length);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(saltedPassword);
            return bytesToHex(hash);
        } catch (Exception e) {
            return null;
        }
    }

    /*
     * Converts a byte array to a hex string
     * @param hash the byte array to convert
     * @return the hex string
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
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
        if (userPatch.getPassword().isEmpty() || userPatch.getPassword().length() < 4) {
            throw new InvalidPasswordException("Password must be at least 4 characters");
        }
        Users user = userOpt.get();
        user.setPassword(getHashedPassword(userPatch.getPassword(), user.getSalt()));
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
