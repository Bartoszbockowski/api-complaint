package com.bbockowski.apicomplaint.exception;

public class ComplaintNotFoundException extends RuntimeException {

  public ComplaintNotFoundException(String message) {
    super(message);
  }
}
