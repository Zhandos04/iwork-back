package com.example.iwork.repositories;

import com.example.iwork.entities.Salary;
import com.example.iwork.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    Long countByUser(User user);
}
