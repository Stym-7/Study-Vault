package com.studyvault.util;

import java.util.Random;

public class OTPUtil {
    public static String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
}
