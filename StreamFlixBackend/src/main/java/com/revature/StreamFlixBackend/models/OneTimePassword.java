package com.revature.StreamFlixBackend.models;

import java.util.Date;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
public class OneTimePassword {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int otpId;
    @Column(nullable = false)
    private int otp;
    @Column(nullable = false)
    private Date expirationDate;
    @OneToOne
    private Users user;

    public OneTimePassword() {
    }

    public OneTimePassword(int otpId, int otp, Date expirationDate, Users user) {
        this.otpId = otpId;
        this.otp = otp;
        this.expirationDate = expirationDate;
        this.user = user;
    }

    public OneTimePassword(int otp, Date expirationDate, Users user) {
        this.otp = otp;
        this.expirationDate = expirationDate;
        this.user = user;
    }

    public int getOtpId() {
        return otpId;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Users getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OneTimePassword that = (OneTimePassword) o;
        return otpId == that.otpId && otp == that.otp && Objects.equals(expirationDate, that.expirationDate) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(otpId, otp, expirationDate, user);
    }

    @Override
    public String toString() {
        return "OneTimePassword{" +
                "id=" + otpId +
                ", otp='" + otp + '\'' +
                ", expirationDate=" + expirationDate +
                ", user=" + user +
                '}';
    }
}
