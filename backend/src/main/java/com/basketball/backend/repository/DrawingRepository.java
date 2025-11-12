package com.basketball.backend.repository;

import com.basketball.backend.model.Drawing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Repository interface that handles database operations for Drawing entities.
// spring Data JPA automatically provides implementations
// for basic CRUD (create, read, update, delete) operations.
@Repository
public interface DrawingRepository extends JpaRepository<Drawing, Long> {

    // custom method to find a drawing by timestamp
    List<Drawing> findAllByOrderByCreatedAtDesc();

    // custom method for getting drawings sorted by likes descending
    List<Drawing> findAllByOrderByLikesCountDesc();

    // custom method for getting all drawings from user
    List<Drawing> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}

