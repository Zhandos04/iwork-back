package com.example.iwork.repositories;

import com.example.iwork.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByLocationValue(String locationValue);

    /**
     * Поиск локаций, содержащих указанный текст, без учета регистра
     * @param text строка поиска
     * @return список локаций, соответствующих критериям поиска
     */
    List<Location> findByLocationValueContainingIgnoreCase(String text);
}