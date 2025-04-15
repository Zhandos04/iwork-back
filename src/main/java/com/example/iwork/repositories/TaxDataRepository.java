package com.example.iwork.repositories;

import com.example.iwork.entities.TaxData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaxDataRepository extends JpaRepository<TaxData, Long> {
    Optional<TaxData> findByCompanyId(Long companyId);
}