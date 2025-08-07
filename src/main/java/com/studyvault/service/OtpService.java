package com.studyvault.service;

import com.studyvault.model.OtpVerification;
import com.studyvault.repository.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpVerificationRepository otpRepository;
    private static final int OTP_VALID_DURATION_MINUTES = 5;

    public String generateOtp() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    public void saveOtp(String email, String otp) {
        OtpVerification otpEntity = OtpVerification.builder()
                .email(email)
                .otp(otp)
                .generatedAt(LocalDateTime.now())
                .build();
        otpRepository.save(otpEntity);
    }

    public boolean verifyOtp(String email, String enteredOtp) {
        Optional<OtpVerification> latestOtp = otpRepository.findTopByEmailOrderByGeneratedAtDesc(email);

        if (latestOtp.isEmpty()) return false;

        OtpVerification otpRecord = latestOtp.get();

        // Check expiry
        if (otpRecord.getGeneratedAt().plusMinutes(OTP_VALID_DURATION_MINUTES).isBefore(LocalDateTime.now())) {
            return false; // Expired
        }

        return otpRecord.getOtp().equals(enteredOtp);
    }
}
