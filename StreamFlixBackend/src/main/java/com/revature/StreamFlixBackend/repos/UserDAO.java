package com.revature.StreamFlixBackend.repos;

import com.revature.StreamFlixBackend.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * UserDAO interface for StreamFlixBackend
 * This interface is used to create the UserDAO repository and interact with the users table.
 */
@Repository
public interface UserDAO extends JpaRepository<Users, Integer> {
    /*
     * Finds the user by username
     * @param username the username to find the user by
     * @return the user found by username
     */
    Optional<Users> findByUsername(String username);

    /*
     * Finds the user by email
     * @param email the email to find the user by
     */
    Optional<Users> findByEmail(String email);
}
