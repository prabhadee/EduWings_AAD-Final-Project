//package com.assignment.backend.service;
//
//import com.assignment.backend.entity.PasswordResetToken;
//
//public interface PasswordResetService {
//    void generateOtp(String email);
//    boolean verifyOtp(String email, String otp);
//    void resetPassword(String email, String newPassword);
//}
package lk.ijse.gdse72.backend.service;

public interface PasswordResetService {
    void generateOtp(String email);
    boolean verifyOtp(String email, String otp);
    void resetPassword(String email, String newPassword);
}