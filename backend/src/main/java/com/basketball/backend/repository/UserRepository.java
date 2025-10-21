package com.basketball.backend.repository;

import com.basketball.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository interface that handles database operations for User entities.
// spring Data JPA automatically provides implementations 
// for basic CRUD (create, read, update, delete) operations.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // custom method to find a user by their username
    User findByUsername(String username);
}
