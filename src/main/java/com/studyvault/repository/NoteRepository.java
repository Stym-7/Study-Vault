package com.studyvault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.studyvault.model.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
