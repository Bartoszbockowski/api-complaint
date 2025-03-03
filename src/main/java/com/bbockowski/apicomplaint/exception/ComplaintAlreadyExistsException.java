package com.bbockowski.apicomplaint.exception;

public class ComplaintAlreadyExistsException extends RuntimeException {

  public ComplaintAlreadyExistsException(String message) {
    super(message);
  }
}
