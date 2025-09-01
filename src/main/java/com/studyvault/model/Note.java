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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Every note must belong to a user
    private User user;
}
