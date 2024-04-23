package com.revature.StreamFlixBackend.services;

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

}
