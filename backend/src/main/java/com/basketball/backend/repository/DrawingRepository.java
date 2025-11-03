package com.basketball.backend.repository;

import com.basketball.backend.model.Drawing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// Repository interface that handles database operations for Drawing entities.
// spring Data JPA automatically provides implementations
// for basic CRUD (create, read, update, delete) operations.
@Repository
public interface DrawingRepository extends JpaRepository<Drawing, Long> {

    // custom method to find a drawing by timestamp
    List<Drawing> findAllByOrderByCreatedAtDesc();

    // customm method for getting drawings sorted by likes descending
    List<Drawing> findAllByOrderByLikesCountDesc();

    // For proper drawing like linking
    Optional<DrawingLike> findByUserIdAndDrawingId(Long userId, Long drawingId);

}

