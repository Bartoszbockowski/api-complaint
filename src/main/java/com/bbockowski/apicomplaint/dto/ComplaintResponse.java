package com.bbockowski.apicomplaint.dto;

import java.sql.Timestamp;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintResponse {
  private UUID id;
  private String productId;
  private String content;
  private Timestamp createdAt;
  private String reporter;
  private String country;
  private int reportCount = 1;
}
