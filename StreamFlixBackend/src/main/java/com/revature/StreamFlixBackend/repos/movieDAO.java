package com.revature.StreamFlixBackend.repos;

import com.revature.StreamFlixBackend.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface movieDAO extends JpaRepository<Movie, Integer> {
    
}
