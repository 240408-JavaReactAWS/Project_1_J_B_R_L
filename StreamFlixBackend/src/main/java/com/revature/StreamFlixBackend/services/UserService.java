package com.revature.StreamFlixBackend.services;

import com.revature.StreamFlixBackend.exceptions.InvalidRegistrationException;
import com.revature.StreamFlixBackend.exceptions.UserAlreadyExistsException;
import com.revature.StreamFlixBackend.models.Users;
import com.revature.StreamFlixBackend.repos.UserDAO;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserDAO userDAO;

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

}
