package com.revature.StreamFlixBackend.repos;

import com.revature.StreamFlixBackend.models.OneTimePassword;
import com.revature.StreamFlixBackend.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * OneTimePasswordDAO interface for StreamFlixBackend
 * This interface is used to create the OneTimePasswordDAO repository and interact with the one_time_password table.
 */
@Repository
public interface OneTimePasswordDAO extends JpaRepository<OneTimePassword, Integer> {
    /*
     * Finds the one time password by user and otp
     * @param user the user to find the one time password by
     * @param otp the one time password to find by
     * @return the one time password found by user and otp
     */
    @Query("SELECT otp FROM OneTimePassword otp WHERE otp.user = :user AND otp.otp = :otp")
    Optional<OneTimePassword> findByUserAndOtp(@Param("user") Users user, @Param("otp") int otp);

    /*
     * Finds the one time password by user
     * @param user the user to find the one time password by
     * @return the one time password found by user
     */
    Optional<OneTimePassword> findByUser(Users user);
}
