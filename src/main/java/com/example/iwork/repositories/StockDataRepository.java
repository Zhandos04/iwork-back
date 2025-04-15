package com.example.iwork.repositories;

import com.example.iwork.entities.StockData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockDataRepository extends JpaRepository<StockData, Long> {
    Optional<StockData> findByCompanyId(Long companyId);
}
