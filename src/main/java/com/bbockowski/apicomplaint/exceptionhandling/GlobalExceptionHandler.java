package com.bbockowski.apicomplaint.exceptionhandling;

import com.bbockowski.apicomplaint.exception.ComplaintAlreadyExistsException;
import com.bbockowski.apicomplaint.exception.ComplaintCreateException;
import com.bbockowski.apicomplaint.exception.ComplaintNotFoundException;
import com.bbockowski.apicomplaint.exception.ComplaintUpdateException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ComplaintNotFoundException.class)
  public ResponseEntity<ExceptionResponseDto> handleComplaintNotFoundException(
    ComplaintNotFoundException e
  ) {
    ExceptionResponseDto responseDto = new ExceptionResponseDto(
      List.of(e.getMessage()),
      "NOT_FOUND",
      LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
  }

  @ExceptionHandler(ComplaintAlreadyExistsException.class)
  public ResponseEntity<ExceptionResponseDto> handleComplaintAlreadyExistsException(
    ComplaintAlreadyExistsException e
  ) {
    ExceptionResponseDto responseDto = new ExceptionResponseDto(
      List.of(e.getMessage()),
      "CONFLICT",
      LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDto);
  }

  @ExceptionHandler(ComplaintCreateException.class)
  public ResponseEntity<ExceptionResponseDto> handleComplaintCreationException(
    ComplaintCreateException e
  ) {
    ExceptionResponseDto responseDto = new ExceptionResponseDto(
      List.of(e.getMessage()),
      "BAD_REQUEST",
      LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
  }

  @ExceptionHandler(ComplaintUpdateException.class)
  public ResponseEntity<ExceptionResponseDto> handleComplaintUpdateException(
    ComplaintUpdateException e
  ) {
    ExceptionResponseDto responseDto = new ExceptionResponseDto(
      List.of(e.getMessage()),
      "BAD_REQUEST",
      LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionResponseDto> handleGeneralException(Exception e) {
    ExceptionResponseDto responseDto = new ExceptionResponseDto(
      List.of("An unexpected error occurred: " + e.getMessage()),
      "INTERNAL_SERVER_ERROR",
      LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
  }
}
