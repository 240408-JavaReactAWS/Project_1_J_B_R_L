package com.revature.StreamFlixBackend.services;

import com.revature.StreamFlixBackend.exceptions.InvalidRegistrationException;
import com.revature.StreamFlixBackend.exceptions.UserAlreadyExistsException;
import com.revature.StreamFlixBackend.models.Users;
import com.revature.StreamFlixBackend.repos.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Users registerUser(Users user) throws InvalidRegistrationException, UserAlreadyExistsException {
        String username = user.getUsername();
        String password = user.getPassword();
        if (username == null || password == null)
            throw new InvalidRegistrationException("Unable to register new user:" +
                    username + ". Username and password must be specified.");
        else if (username.length() < 4 || password.length() < 4)
            throw new InvalidRegistrationException("Unable to register new user:" +
                    username + ". Username and password must be at least four characters.");
        else if (userDAO.findByUsername(username).isPresent())
            throw new UserAlreadyExistsException("User " + username + " already exists!");
        else return userDAO.save(user);
    }
}
