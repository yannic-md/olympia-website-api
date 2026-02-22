package de.olympia.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import de.olympia.main.entity.Sports;

@Repository
public interface SportsRepository extends JpaRepository<Sports, Long> {
    Optional<Sports> findByNameIgnoreCase(String name);
}

