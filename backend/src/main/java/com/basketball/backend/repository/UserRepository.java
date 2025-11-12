package com.basketball.backend.repository;

import com.basketball.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Repository interface that handles database operations for User entities.
// spring Data JPA automatically provides implementations 
// for basic CRUD (create, read, update, delete) operations.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // custom method to find a user by their username, one of them is not case sensitive for users
    User findByUsername(String username);
    Optional<User> findById(Long id);
    List<User> findByUsernameContainingIgnoreCase(String username);

}
