package com.example.iwork.repositories;

import com.example.iwork.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    User getUserById(Long id);
    Boolean existsByUsername(String username);
    Optional<User> findByConfirmationCode(String confirmationCode);
    List<User> findAllByEmailVerifiedFalseAndCreatedAtBefore(LocalDateTime cutoffTime);
    Optional<User> findByUsername(String username);
}
