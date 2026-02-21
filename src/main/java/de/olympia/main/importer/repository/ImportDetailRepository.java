package de.olympia.main.importer.repository;

import de.olympia.main.importer.entity.ImportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportDetailRepository extends JpaRepository<ImportDetail, Long> {

    List<ImportDetail> findByImportLogId(Long importLogId);

    List<ImportDetail> findByImportLogIdAndEntityType(Long importLogId, String entityType);
}

