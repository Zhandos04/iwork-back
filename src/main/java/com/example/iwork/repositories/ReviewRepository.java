package com.example.iwork.repositories;

import com.example.iwork.entities.Review;
import com.example.iwork.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Long countByUser(User user);
}
