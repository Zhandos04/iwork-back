package com.example.iwork.repositories;

import com.example.iwork.entities.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByCategory(String category);
    List<Job> findByTitleContainingIgnoreCase(String title);
}