package com.basketball.backend.repository;

import com.basketball.backend.model.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// Repository interface that handles database operations for Prompt entities.
// spring Data JPA automatically provides implementations
// for basic CRUD (create, read, update, delete) operations.
@Repository
public interface PromptRepository extends JpaRepository<Prompt, Long> {
    // method to return the latest propmt based on when it was generated
    Prompt findTopByOrderByDateGeneratedDesc();
}