package com.revature.StreamFlixBackend.repos;

import com.revature.StreamFlixBackend.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<Users, Integer> {

    public Optional<Users> findByUsername(String username);
}
