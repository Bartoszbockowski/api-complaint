package com.bbockowski.apicomplaint.exception;

public class ComplaintCreateException extends RuntimeException {

  public ComplaintCreateException(String message, Throwable cause) {
    super(message, cause);
  }
}
