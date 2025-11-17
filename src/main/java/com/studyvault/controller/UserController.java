package com.studyvault.controller;

import com.studyvault.dto.OtpVerificationRequest;
import com.studyvault.model.PendingUser;
import com.studyvault.model.User;
import com.studyvault.repository.PendingUserRepository;
import com.studyvault.repository.UserRepository;
import com.studyvault.service.EmailService;
import com.studyvault.service.OtpService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PendingUserRepository pendingUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    // -----------------------------------------
    // ✅ REGISTER (store pending user, send OTP)
    // -----------------------------------------
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {

        // Already a real user?
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email already registered");
        }

        // Already pending?
        if (pendingUserRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email already pending verification");
        }

        // Save PENDING user
        PendingUser pending = PendingUser.builder()
                .email(user.getEmail())
                .name(user.getName())
                .passwordHash(passwordEncoder.encode(user.getPassword()))
                .createdAt(Instant.now())
                .build();

        pendingUserRepository.save(pending);

        // Generate OTP + save
        String otp = otpService.generateOtp();
        otpService.saveOtp(user.getEmail(), otp);

        // Send email
        emailService.sendOTPEmail(user.getEmail(), otp);

        return ResponseEntity.ok("Registration initiated. OTP sent to email.");
    }

    // -----------------------------------------
    // ✅ VERIFY OTP → Convert PENDING → REAL USER
    // -----------------------------------------
    @PostMapping("/verify-otp")
    @Transactional
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationRequest request) {

        boolean validOtp = otpService.verifyOtp(request.getEmail(), request.getOtp());

        if (!validOtp) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired OTP");
        }

        // Find pending user
        PendingUser pending = pendingUserRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (pending == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No pending verification found for this email");
        }

        // Create real user
        User realUser = new User();
        realUser.setName(pending.getName());
        realUser.setEmail(pending.getEmail());
        realUser.setPassword(pending.getPasswordHash());
        realUser.setVerified(true);

        userRepository.save(realUser);

        // Cleanup
        pendingUserRepository.deleteByEmail(pending.getEmail());
        otpService.deleteOtpsForEmail(pending.getEmail());

        return ResponseEntity.ok("Email verification successful");
    }

    // -----------------------------------------
    // ✅ LOGIN (unchanged)
    // -----------------------------------------
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

    // -----------------------------------------
    // OTHER ENDPOINTS (unchanged)
    // -----------------------------------------

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestParam String email) {
        User user = userRepository.findByEmail(email);
        if (user == null || !user.isVerified()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or not verified");
        }

        return ResponseEntity.ok(new UserDTO(user.getName(), user.getEmail()));
    }

    static class UserDTO {
        private final String name;
        private final String email;

        public UserDTO(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
    }
}
