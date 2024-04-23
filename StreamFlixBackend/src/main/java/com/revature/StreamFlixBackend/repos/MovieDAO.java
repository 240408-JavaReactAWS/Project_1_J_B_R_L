package com.revature.StreamFlixBackend.repos;

import com.revature.StreamFlixBackend.models.Movie;
import com.revature.StreamFlixBackend.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieDAO extends JpaRepository<Movie, Integer> {

    List<Movie> getMoviesByUser(Users user);
    Optional<List<Movie>> findDistinctByUserNotContaining(Users user);
}
