package com.bbockowski.apicomplaint.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditComplaintRequest {
  private String id;
  private String content;
}
