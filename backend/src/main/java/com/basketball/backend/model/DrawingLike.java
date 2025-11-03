package com.basketball.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "drawing_likes")
@IdClass(DrawingLikeId.class)
public class DrawingLike {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "drawing_id")
    private Long drawingId;


    // getters and setters
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDrawingId() {
        return drawingId;
    }
    public void setDrawingId(Long drawingId) {
        this.drawingId = drawingId;
    }
}