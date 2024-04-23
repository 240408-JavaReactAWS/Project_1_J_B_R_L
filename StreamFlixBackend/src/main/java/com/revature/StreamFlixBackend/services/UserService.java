package com.revature.StreamFlixBackend.services;

import com.revature.StreamFlixBackend.models.Movie;
import com.revature.StreamFlixBackend.models.Users;
import com.revature.StreamFlixBackend.repos.MovieDAO;
import com.revature.StreamFlixBackend.repos.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Users loginUser(String username, String password) {
        Optional<Users> loginUser = userDAO.findByUsername(username);

        if (loginUser.isEmpty()) {
            throw new NoSuchElementException("No user was found");
        }
        if (loginUser.get().getPassword().equals(password)) {
            return loginUser.get();
        } else {
            throw new IllegalArgumentException("Wrong Password");
        }
    }
}
