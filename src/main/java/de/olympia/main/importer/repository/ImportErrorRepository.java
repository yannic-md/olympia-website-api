package de.olympia.main.importer.repository;

import de.olympia.main.importer.entity.ImportError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportErrorRepository extends JpaRepository<ImportError, Long> {

    List<ImportError> findByImportLogId(Long importLogId);

    @Query("SELECT ie FROM ImportError ie WHERE ie.importLogId = :importLogId ORDER BY ie.rowNumber ASC")
    List<ImportError> findErrorsByImportLog(@Param("importLogId") Long importLogId);
}

