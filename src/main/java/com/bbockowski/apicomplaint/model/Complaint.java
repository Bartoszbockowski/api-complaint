package com.bbockowski.apicomplaint.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@Entity
@Table(name = "complaints")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Complaint {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", unique = true, nullable = false)
  private UUID id;

  @Version
  @Column(name = "version")
  private Long version;

  @Column(name = "product_id", nullable = false)
  private String productId;

  @Column(name = "content", nullable = false)
  private String content;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Timestamp createdAt;

  @LastModifiedDate
  @Column(name = "modified_at", nullable = false)
  private Timestamp modifiedAt;

  @Column(name = "reporter", nullable = false)
  private String reporter;

  @Column(name = "country")
  private String country;

  @Column(name = "report_count", nullable = false)
  private int reportCount = 1;

  public void incrementReportCount() {
    this.reportCount++;
  }
}
