package com.basketball.backend.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "prompts")
public class Prompt {

    // auto-generated incremental ID for each prompt stored in db
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // fields
    private String text;

    // day the prompt was generated
    @Column(name = "date_generated")
    private Timestamp dateGenerated;

    // ACCESSORS AND MUTATORS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Timestamp getDateGenerated() { return dateGenerated; }
    public void setDateGenerated(Timestamp dateGenerated) { this.dateGenerated = dateGenerated; }
}
