package com.studyvault.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    @Column(name = "drive_link")
    private String driveLink;

    // A note belongs to a user (optional if you want to upload without login)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
}
