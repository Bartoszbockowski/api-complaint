package com.bbockowski.apicomplaint.repository;

import com.bbockowski.apicomplaint.model.Complaint;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {
  @Override
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Complaint> findById(UUID id);

  Optional<Complaint> findByProductIdAndReporter(String productId, String reporter);
}
