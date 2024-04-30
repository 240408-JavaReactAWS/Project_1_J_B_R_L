package com.revature.StreamFlixBackend.models;

import java.util.Date;
import java.util.Objects;

import jakarta.persistence.*;

/*
 * OneTimePassword class for StreamFlixBackend
 * This class is used to create the one_time_password entity and the one_time_password table in the database.
 */
@Entity
public class OneTimePassword {

    /* otpId is the primary key for the one_time_password table */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int otpId;
    /* otp is the one time password */
    @Column(nullable = false)
    private int otp;
    /* expirationDate is the expiration date of the one time password */
    @Column(nullable = false)
    private Date expirationDate;
    /* user is the user that the one time password is associated with */
    @OneToOne
    private Users user;

    /*
     * No args constructor required by JPA
     */
    public OneTimePassword() {
    }

    /*
     * Constructor for OneTimePassword
     * @param otpId the one time password id
     * @param otp the one time password
     * @param expirationDate the expiration date of the one time password
     * @param user the user that the one time password is associated with
     */
    public OneTimePassword(int otpId, int otp, Date expirationDate, Users user) {
        this.otpId = otpId;
        this.otp = otp;
        this.expirationDate = expirationDate;
        this.user = user;
    }

    /*
     * Constructor for OneTimePassword
     * @param otp the one time password
     * @param expirationDate the expiration date of the one time password
     * @param user the user that the one time password is associated with
     */
    public OneTimePassword(int otp, Date expirationDate, Users user) {
        this.otp = otp;
        this.expirationDate = expirationDate;
        this.user = user;
    }

    /*
     * Get the otpId
     * @return the otpId
     */
    public int getOtpId() {
        return otpId;
    }

    /*
     * Set the otpId
     * @param otpId the otpId to set
     */
    public int getOtp() {
        return otp;
    }

    /*
     * Set the otp
     * @param otp the otp to set
     */
    public void setOtp(int otp) {
        this.otp = otp;
    }

    /*
     * Get the expirationDate
     * @return the expirationDate
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /*
     * Set the expirationDate
     * @param expirationDate the expirationDate to set
     */
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    /*
     * Get the user
     * @return the user
     */
    public Users getUser() {
        return user;
    }

    /*
     * Equals method for OneTimePassword that checks the otpId, otp, expirationDate, and user
     * @param o the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OneTimePassword that = (OneTimePassword) o;
        return otpId == that.otpId && otp == that.otp && Objects.equals(expirationDate, that.expirationDate) && Objects.equals(user, that.user);
    }

    /*
     * Hash code method for OneTimePassword that hashes the otpId, otp, expirationDate, and user
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(otpId, otp, expirationDate, user);
    }

    /*
     * To string method for OneTimePassword that uses the otpId, otp, expirationDate, and user
     * @return the string
     */
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
