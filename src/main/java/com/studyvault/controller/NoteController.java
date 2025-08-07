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
}
