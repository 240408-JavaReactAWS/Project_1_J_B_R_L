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

@Service
public class UserService {
    private final UserDAO userDAO;
    private final MovieDAO movieDAO;
    private final OneTimePasswordDAO oneTimePasswordDAO;


    @Autowired
    public UserService(UserDAO userDAO, MovieDAO movieDAO, OneTimePasswordDAO oneTimePasswordDAO) {
        this.userDAO = userDAO;
        this.movieDAO = movieDAO;
        this.oneTimePasswordDAO = oneTimePasswordDAO;
    }


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

    public Optional<Users> findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public Users getUserByEmail(String email) {
        return userDAO.findByEmail(email).orElse(null);
    }

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
        else return userDAO.save(user);
    }

    //Returns a list of all users
    public List<Users> getAllUsers() {
        return userDAO.findAll();
    }

    //Stores an otp in the database
    public void saveOTP(OneTimePassword otp) {
        Optional<OneTimePassword> otpRecord = oneTimePasswordDAO.findByUser(otp.getUser());
        otpRecord.ifPresent(oneTimePasswordDAO::delete);
        oneTimePasswordDAO.save(otp);
    }

    //Users can reset their passwords
    public Users resetPassword(Users userPatch) throws UserNotFoundException, InvalidPasswordException {;
        Optional<Users> userOpt = userDAO.findByEmail(userPatch.getEmail());
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        if (userPatch.getPassword().length() < 4) {
            throw new InvalidPasswordException("Password must be at least 4 characters");
        }
        Users user = userOpt.get();
        user.setPassword(userPatch.getPassword());
        return userDAO.save(user);
    }

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

    public Users addMoney(String username, double amount) {
        Users currentUser = userDAO.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (amount < 0.0) {
            return null;
        }
        currentUser.setBalance(currentUser.getBalance() + amount);
        return userDAO.save(currentUser);
    }

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
}
