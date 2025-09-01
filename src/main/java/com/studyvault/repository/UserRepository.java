package com.studyvault.repository;

import com.studyvault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by email
    User findByEmail(String email);

    // Check if email already exists (for registration)
    boolean existsByEmail(String email);

    // Find only verified users by email (for login)
    User findByEmailAndVerifiedTrue(String email);
}
