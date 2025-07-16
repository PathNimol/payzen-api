package org.aub.payrollapi.service;


public interface EmailSenderService {
    void sendEmail(String toEmail, String otp);
}
