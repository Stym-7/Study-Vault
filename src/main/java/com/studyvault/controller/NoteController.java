package com.studyvault.controller;

import com.studyvault.dto.NoteRequestDTO;
import com.studyvault.model.Note;
import com.studyvault.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    // Upload Note API using DTO
    @PostMapping("/upload")
    public String uploadNote(@RequestBody NoteRequestDTO noteRequest) {
        Note note = new Note();
        note.setTopic(noteRequest.getTopic());
        note.setDriveLink(noteRequest.getLink());  // Proper mapping
        noteRepository.save(note);
        return "Note uploaded successfully!";
    }

    // Get All Notes API
    @GetMapping("/all")
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    // Delete Note API
    @DeleteMapping("/delete/{id}")
    public String deleteNote(@PathVariable Long id) {
        if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id);
            return "Note deleted successfully!";
        } else {
            return "Note not found!";
        }
    }

    // Get Total Notes Count (for dashboard stats)
    @GetMapping("/count")
    public long getNotesCount() {
        return noteRepository.count();
    }

    // Get Recently Uploaded Notes (Top 5)
    @GetMapping("/recent")
    public List<Note> getRecentNotes() {
        return noteRepository.findAll()
                .stream()
                .sorted((a, b) -> Long.compare(b.getId(), a.getId())) // Latest first
                .limit(5)
                .toList();
    }
}
