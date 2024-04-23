package com.revature.StreamFlixBackend.services;

import com.revature.StreamFlixBackend.models.Users;
import com.revature.StreamFlixBackend.exceptions.UserAlreadyExistsException;
import com.revature.StreamFlixBackend.repos.UserDAO;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
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
