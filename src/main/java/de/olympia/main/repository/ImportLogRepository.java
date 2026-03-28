package de.olympia.main.repository;

import de.olympia.main.entity.ImportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ImportLogRepository extends JpaRepository<ImportLog, Long> {

    List<ImportLog> findByStatus(ImportLog.ImportStatus status);

    List<ImportLog> findByImportTypeOrderByImportedAtDesc(String importType);

    @Query("SELECT il FROM ImportLog il WHERE il.importedAt >= :startDate AND il.importedAt <= :endDate ORDER BY il.importedAt DESC")
    List<ImportLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}

