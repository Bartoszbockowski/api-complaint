package com.bbockowski.apicomplaint.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddComplaintRequest {
  private String productId;
  private String content;
  private String reporter;
  private String country;
}
