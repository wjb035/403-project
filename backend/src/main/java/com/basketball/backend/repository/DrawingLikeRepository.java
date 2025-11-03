package com.basketball.backend.repository;

import com.basketball.backend.model.DrawingLike;
import com.basketball.backend.model.DrawingLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DrawingLikeRepository extends JpaRepository<DrawingLike, DrawingLikeId> {
    // For proper drawing like linking
    Optional<DrawingLike> findByUserIdAndDrawingId(Long userId, Long drawingId);
}
