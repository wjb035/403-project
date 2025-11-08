package com.basketball.backend.model;

import java.io.Serializable;
import java.util.Objects;


// Composite primary key
public class DrawingLikeId implements Serializable {
    private Long userId;
    private Long drawingId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DrawingLikeId)) return false;
        DrawingLikeId that = (DrawingLikeId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(drawingId, that.drawingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, drawingId);
    }
}