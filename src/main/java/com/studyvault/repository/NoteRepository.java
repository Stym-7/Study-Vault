package com.studyvault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.studyvault.model.Note;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    // Get all notes uploaded by a specific user
    List<Note> findByUserId(Long userId);

    // Count how many notes a specific user has uploaded
    long countByUserId(Long userId);

    // Count total contributors (unique users who uploaded notes)
    @Query("SELECT COUNT(DISTINCT n.user.id) FROM Note n")
    long countDistinctUserId();
}
