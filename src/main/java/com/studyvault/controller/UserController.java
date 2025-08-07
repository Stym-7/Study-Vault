package com.studyvault.controller;

import com.studyvault.model.User;
import com.studyvault.repository.UserRepository;
import com.studyvault.service.EmailService;
import com.studyvault.util.OTPUtil;
import com.studyvault.dto.OtpVerificationRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // Temporary storage for unverified users
    private final ConcurrentHashMap<String, User> tempUserMap = new ConcurrentHashMap<>();

    // ✅ Register user (store temporarily until OTP verified)
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()) != null || tempUserMap.containsKey(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already registered or pending verification");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        String otp = OTPUtil.generateOTP();
        user.setOtp(otp);
        user.setVerified(false);

        tempUserMap.put(user.getEmail(), user);
        emailService.sendOTPEmail(user.getEmail(), otp);

        return ResponseEntity.ok("Registration initiated. OTP sent to email.");
    }

    // ✅ Verify OTP and store user in DB
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationRequest request) {
        String email = request.getEmail();
        String otp = request.getOtp();

        User user = tempUserMap.get(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pending verification found for this email");
        }

        if (otp.equals(user.getOtp())) {
            user.setVerified(true);
            user.setOtp(null);
            userRepository.save(user);
            tempUserMap.remove(email);
            return ResponseEntity.ok("Email verification successful");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");
        }
    }

    // ✅ Login
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User loginRequest) {
        User existingUser = userRepository.findByEmail(loginRequest.getEmail());
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ User not found");
        }

        if (!existingUser.isVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("❌ Email not verified");
        }

        boolean passwordMatch = passwordEncoder.matches(
                loginRequest.getPassword(),
                existingUser.getPassword()
        );

        if (passwordMatch) {
            return ResponseEntity.ok("✅ Login successful!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Invalid password");
        }
    }

    // 🟡 Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 🟡 Get user by email
    @GetMapping("/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email);
    }

    // ✅ API to get current user info (safe) by email (for dashboard)
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestParam String email) {
        User user = userRepository.findByEmail(email);
        if (user == null || !user.isVerified()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or not verified");
        }

        return ResponseEntity.ok(new UserDTO(user.getName(), user.getEmail()));
    }

    // ✅ Inner DTO class to return safe user info
    static class UserDTO {
        private String name;
        private String email;

        public UserDTO(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }
}
