package com.revature.StreamFlixBackend.repos;

import com.revature.StreamFlixBackend.models.OneTimePassword;
import com.revature.StreamFlixBackend.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OneTimePasswordDAO extends JpaRepository<OneTimePassword, Integer> {
    @Query("SELECT otp FROM OneTimePassword otp WHERE otp.user = :user AND otp.otp = :otp")
    Optional<OneTimePassword> findByUserAndOtp(@Param("user") Users user, @Param("otp") int otp);

    Optional<OneTimePassword> findByUser(Users user);
}
