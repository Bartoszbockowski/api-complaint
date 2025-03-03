package com.bbockowski.apicomplaint.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bbockowski.apicomplaint.dto.AddComplaintRequest;
import com.bbockowski.apicomplaint.dto.EditComplaintRequest;
import com.bbockowski.apicomplaint.model.Complaint;
import com.bbockowski.apicomplaint.repository.ComplaintRepository;
import com.bbockowski.apicomplaint.service.IpLocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("dev")
class ComplaintApiIT {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ComplaintRepository complaintRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Mock
  private IpLocationService ipLocationService;

  @Mock
  private AddComplaintRequest addComplaintRequest;

  @Mock
  private EditComplaintRequest editComplaintRequest;

  @BeforeEach
  void setUp() {
    when(ipLocationService.getCountryByIp("123.123.123.123")).thenReturn("Poland");
  }

  @Test
  void addComplaint_ShouldCreateAndReturnComplaint() throws Exception {
    // given
    when(addComplaintRequest.getProductId()).thenReturn("RocknRoll-001");
    when(addComplaintRequest.getReporter()).thenReturn("Cliff Burton");
    when(addComplaintRequest.getContent()).thenReturn("Volume too loud");
    when(addComplaintRequest.getCountry()).thenReturn("China");
    var complaintRequestJson = objectMapper.writeValueAsString(addComplaintRequest);

    // when
    var result = mockMvc.perform(
      post("/api/v1/complaints")
        .contentType(MediaType.APPLICATION_JSON)
        .header("X-Forwarded-For", "123.123.123.123")
        .content(complaintRequestJson)
    );

    // then
    result
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").exists())
      .andExpect(jsonPath("$.productId").value("RocknRoll-001"))
      .andExpect(jsonPath("$.content").value("Volume too loud"))
      .andExpect(jsonPath("$.reporter").value("Cliff Burton"))
      .andExpect(jsonPath("$.country").value("China"));
  }

  @Test
  void getComplaint_ShouldReturnComplaint() throws Exception {
    // given
    var savedComplaint = new Complaint();
    savedComplaint.setProductId("ultimate-prop");
    savedComplaint.setReporter("Ada Lovelace");
    savedComplaint.setContent("Algorithm glitch");
    savedComplaint.setCountry("Finland");
    savedComplaint = complaintRepository.save(savedComplaint);

    // when
    var result = mockMvc.perform(
      get("/api/v1/complaints/{id}", savedComplaint.getId())
        .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    result
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(savedComplaint.getId().toString()))
      .andExpect(jsonPath("$.productId").value("ultimate-prop"))
      .andExpect(jsonPath("$.content").value("Algorithm glitch"))
      .andExpect(jsonPath("$.reporter").value("Ada Lovelace"))
      .andExpect(jsonPath("$.country").value("Finland"));
  }

  @Test
  void updateComplaint_ShouldUpdateAndReturnComplaint() throws Exception {
    // given
    var savedComplaint = new Complaint();
    savedComplaint.setProductId("product-404");
    savedComplaint.setReporter("Nick Fury");
    savedComplaint.setContent("Original content");
    savedComplaint.setCountry("Iceland");
    savedComplaint = complaintRepository.save(savedComplaint);

    when(editComplaintRequest.getId()).thenReturn(savedComplaint.getId().toString());
    when(editComplaintRequest.getContent()).thenReturn("Patched content");
    var updateRequestJson = objectMapper.writeValueAsString(editComplaintRequest);

    // when
    var result = mockMvc.perform(
      put("/api/v1/complaints")
        .contentType(MediaType.APPLICATION_JSON)
        .content(updateRequestJson)
    );

    // then
    result
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(savedComplaint.getId().toString()))
      .andExpect(jsonPath("$.content").value("Patched content"));

    var updatedComplaint = complaintRepository
      .findById(savedComplaint.getId())
      .orElseThrow();
    assertThat(updatedComplaint.getContent()).isEqualTo("Patched content");
  }
}
