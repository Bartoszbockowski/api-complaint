package com.bbockowski.apicomplaint.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.bbockowski.apicomplaint.dto.AddComplaintRequest;
import com.bbockowski.apicomplaint.dto.ComplaintResponse;
import com.bbockowski.apicomplaint.dto.EditComplaintRequest;
import com.bbockowski.apicomplaint.exception.ComplaintCreateException;
import com.bbockowski.apicomplaint.exception.ComplaintNotFoundException;
import com.bbockowski.apicomplaint.model.Complaint;
import com.bbockowski.apicomplaint.repository.ComplaintRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ComplaintDefaultServiceImplTest {
  @Mock
  private ComplaintRepository complaintRepository;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private ComplaintDefaultServiceImpl complaintDefaultService;

  private Complaint complaint;
  private AddComplaintRequest addComplaintRequest;
  private EditComplaintRequest editComplaintRequest;
  private UUID complaintId;

  @BeforeEach
  void setUp() {
    complaintId = UUID.randomUUID();
    complaint = new Complaint();
    complaint.setId(complaintId);
    complaint.setProductId("prod1");
    complaint.setContent("Initial content");
    complaint.setCreatedAt(Timestamp.from(Instant.now()));
    complaint.setReporter("reporter@example.com");
    complaint.setCountry("Poland");
    complaint.setReportCount(1);

    addComplaintRequest = new AddComplaintRequest();
    addComplaintRequest.setProductId("prod1");
    addComplaintRequest.setContent("New complaint content");
    addComplaintRequest.setReporter("reporter@example.com");
    addComplaintRequest.setCountry("Poland");

    editComplaintRequest = new EditComplaintRequest();
    editComplaintRequest.setId(complaintId.toString());
    editComplaintRequest.setContent("Updated content");
  }

  @Test
  void shouldCreateNewComplaintWhenNoExistingComplaint() {
    // given
    when(complaintRepository.findByProductIdAndReporter(anyString(), anyString()))
      .thenReturn(Optional.empty());
    when(complaintRepository.save(any(Complaint.class)))
      .thenAnswer(
        invocation -> {
          Complaint saved = invocation.getArgument(0);
          saved.setId(complaintId);
          return saved;
        }
      );
    when(modelMapper.map(any(Complaint.class), eq(ComplaintResponse.class)))
      .thenReturn(new ComplaintResponse());

    // when
    ComplaintResponse response = complaintDefaultService.createComplaint(
      addComplaintRequest
    );

    // then
    assertAll(
      () -> assertThat(response).isNotNull(),
      () -> verify(complaintRepository).save(any(Complaint.class))
    );
  }

  @Test
  void shouldIncrementReportCountWhenComplaintExists() {
    // given
    when(complaintRepository.findByProductIdAndReporter(anyString(), anyString()))
      .thenReturn(Optional.of(complaint));
    when(modelMapper.map(any(Complaint.class), eq(ComplaintResponse.class)))
      .thenReturn(new ComplaintResponse());

    // when
    ComplaintResponse response = complaintDefaultService.createComplaint(
      addComplaintRequest
    );

    // then
    assertAll(
      () -> assertThat(response).isNotNull(),
      () -> assertThat(complaint.getReportCount()).isEqualTo(2),
      () -> verify(complaintRepository).save(complaint)
    );
  }

  @Test
  void shouldThrowExceptionWhenSavingComplaintFails() {
    // given
    when(complaintRepository.findByProductIdAndReporter(anyString(), anyString()))
      .thenReturn(Optional.empty());
    when(complaintRepository.save(any(Complaint.class)))
      .thenThrow(new RuntimeException("Database error"));

    // when
    ComplaintCreateException exception = assertThrows(
      ComplaintCreateException.class,
      () -> complaintDefaultService.createComplaint(addComplaintRequest)
    );

    // then
    assertAll(
      () ->
        assertThat(exception.getMessage())
          .contains("Failed to create or update complaint"),
      () -> verify(complaintRepository).save(any(Complaint.class))
    );
  }

  @Test
  void shouldNotCreateNewComplaintWhenDuplicateExists() {
    // given
    when(complaintRepository.findByProductIdAndReporter(anyString(), anyString()))
      .thenReturn(Optional.of(complaint));
    when(modelMapper.map(any(Complaint.class), eq(ComplaintResponse.class)))
      .thenReturn(new ComplaintResponse());

    // when
    ComplaintResponse response = complaintDefaultService.createComplaint(
      addComplaintRequest
    );

    // then
    assertAll(
      () -> assertThat(response).isNotNull(),
      () -> assertThat(complaint.getReportCount()).isEqualTo(2),
      () -> verify(complaintRepository).save(complaint)
    );
  }

  @Test
  void shouldThrowExceptionWhenComplaintNotFoundOnEdit() {
    // given
    when(complaintRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    // when / then
    assertThrows(
      ComplaintNotFoundException.class,
      () -> complaintDefaultService.editComplaint(editComplaintRequest)
    );
  }

  @Test
  void shouldUpdateComplaintContent() {
    // given
    when(complaintRepository.findById(any(UUID.class)))
      .thenReturn(Optional.of(complaint));
    when(modelMapper.map(any(Complaint.class), eq(ComplaintResponse.class)))
      .thenReturn(new ComplaintResponse());

    // when
    ComplaintResponse response = complaintDefaultService.editComplaint(
      editComplaintRequest
    );

    // then
    assertAll(
      () -> assertThat(response).isNotNull(),
      () -> assertThat(complaint.getContent()).isEqualTo("Updated content"),
      () -> verify(complaintRepository).save(complaint)
    );
  }

  @Test
  void shouldThrowExceptionWhenEditingComplaintFails() {
    // given
    when(complaintRepository.findById(any(UUID.class)))
      .thenReturn(Optional.of(complaint));
    when(complaintRepository.save(any(Complaint.class)))
      .thenThrow(new RuntimeException("Database error"));

    // when
    RuntimeException exception = assertThrows(
      RuntimeException.class,
      () -> complaintDefaultService.editComplaint(editComplaintRequest)
    );

    // then
    assertAll(
      () -> assertThat(exception.getMessage()).contains("Database error"),
      () -> verify(complaintRepository).save(complaint)
    );
  }

  @Test
  void shouldThrowExceptionWhenGettingNonExistentComplaint() {
    // given
    when(complaintRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    // when / then
    assertThrows(
      ComplaintNotFoundException.class,
      () -> complaintDefaultService.getComplaint(complaintId)
    );
  }

  @Test
  void shouldReturnEmptyPageWhenNoComplaintsExist() {
    // given
    when(complaintRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

    // when
    Page<ComplaintResponse> response = complaintDefaultService.getAllComplaints(
      Pageable.unpaged()
    );

    // then
    assertAll(
      () -> assertThat(response.getTotalElements()).isEqualTo(0),
      () -> verify(complaintRepository).findAll(any(Pageable.class))
    );
  }

  @Test
  void shouldReturnAllComplaintsWhenTheyExist() {
    // given
    when(complaintRepository.findAll(any(Pageable.class)))
      .thenReturn(new PageImpl<>(List.of(complaint)));
    when(modelMapper.map(any(Complaint.class), eq(ComplaintResponse.class)))
      .thenReturn(new ComplaintResponse());

    // when
    Page<ComplaintResponse> response = complaintDefaultService.getAllComplaints(
      Pageable.unpaged()
    );

    // then
    assertAll(
      () -> assertThat(response.getTotalElements()).isEqualTo(1),
      () -> verify(complaintRepository).findAll(any(Pageable.class))
    );
  }

  @Test
  void shouldNotUpdateComplaintWhenContentIsNull() {
    // given
    when(complaintRepository.findById(any(UUID.class)))
      .thenReturn(Optional.of(complaint));
    when(modelMapper.map(any(Complaint.class), eq(ComplaintResponse.class)))
      .thenReturn(new ComplaintResponse());
    editComplaintRequest.setContent(null);

    // when
    ComplaintResponse response = complaintDefaultService.editComplaint(
      editComplaintRequest
    );

    // then
    assertAll(
      () -> assertThat(response).isNotNull(),
      () -> assertThat(complaint.getContent()).isEqualTo("Initial content"),
      () -> verify(complaintRepository, never()).save(complaint)
    );
  }
}
