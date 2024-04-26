package com.revature.StreamFlixBackend.services;


import com.revature.StreamFlixBackend.exceptions.*;
import com.revature.StreamFlixBackend.models.Movie;
import com.revature.StreamFlixBackend.repos.MovieDAO;

import com.revature.StreamFlixBackend.models.Users;

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

    @Autowired
    public UserService(UserDAO userDAO, MovieDAO movieDAO) {
        this.userDAO = userDAO;
        this.movieDAO = movieDAO;
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

    //Users can reset their passwords
    public Users resetUserPassword(int userId, Users user) {
        Optional<Users> currentUserOpt = userDAO.findById(userId);
        if (currentUserOpt.isEmpty()){
            return null;
        }
        Users currentUser = currentUserOpt.get();
        String newPassword = user.getPassword();
        if (newPassword.isBlank() || newPassword.length() < 4) {
            return null;
        }
        currentUser.setPassword(newPassword);
        return userDAO.save(currentUser);
    }

    public List<Movie> getMoviesByUsername(String username) throws UserNotFoundException {
        Optional<Users> user = userDAO.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("No user was found");
        }
        return movieDAO.getMoviesByUser(user.get());
    }

    public List<Movie> getMoviesByUserId(String username, int id) throws UserNotFoundException, UnauthorizedException{
        Optional<Users> user = userDAO.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("Username not found");
        }
        if (!user.get().isAdmin()) {
            throw new UnauthorizedException("User is not admin");
        }
        Optional<Users> getUser = userDAO.findById(id);
        if (getUser.isEmpty()) {
            throw new UserNotFoundException("User id was not found");
        }
        return movieDAO.getMoviesByUser(getUser.get());
    }

    public Users addMoney(String username, int amount) {
        Users currentUser = userDAO.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (amount < 0) {
            return null;
        }
        currentUser.setBalance(currentUser.getBalance() + amount);
        return userDAO.save(currentUser);
    }
}
