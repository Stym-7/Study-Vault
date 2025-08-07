package com.studyvault.dto;

public class NoteRequestDTO {
    private String topic;
    private String link;

    // Constructors
    public NoteRequestDTO() {}

    public NoteRequestDTO(String topic, String link) {
        this.topic = topic;
        this.link = link;
    }

    // Getters and Setters
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
