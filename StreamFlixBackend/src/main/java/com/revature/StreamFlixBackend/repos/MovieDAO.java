package com.revature.StreamFlixBackend.repos;

import com.revature.StreamFlixBackend.models.Movie;
import com.revature.StreamFlixBackend.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 * MovieDAO interface for StreamFlixBackend
 * This interface is used to create the MovieDAO repository and interact with the movies table.
 */
@Repository
public interface MovieDAO extends JpaRepository<Movie, Integer> {

    /*
     * Get the movies by user
     * @param user the user to get the movies by
     * @return the list of movies ordered by the user
     */
    List<Movie> getMoviesByUser(Users user);
    /*
     * Finds movies not ordered by the user
     * @param user the user to find the distinct movies by
     * @return the list of distinct movies not containing the user
     */
    Optional<List<Movie>> findDistinctByUserNotContaining(Users user);
}
