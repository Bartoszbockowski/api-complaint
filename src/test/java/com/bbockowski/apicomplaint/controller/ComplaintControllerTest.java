package com.bbockowski.apicomplaint.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bbockowski.apicomplaint.dto.AddComplaintRequest;
import com.bbockowski.apicomplaint.dto.ComplaintResponse;
import com.bbockowski.apicomplaint.dto.EditComplaintRequest;
import com.bbockowski.apicomplaint.service.ComplaintDefaultService;
import com.bbockowski.apicomplaint.service.IpLocationService;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StreamUtils;

@WebMvcTest(ComplaintController.class)
class ComplaintControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Mock
  private ComplaintResponse mockComplaintResponse;

  @MockitoBean
  private ComplaintDefaultService complaintDefaultService;

  @MockitoBean
  private IpLocationService ipLocationService;

  @Test
  void addComplaint_ShouldReturnComplaintResponse() throws Exception {
    AddComplaintRequest addComplaintRequest = Mockito.mock(AddComplaintRequest.class);
    doReturn("Poland").when(addComplaintRequest).getCountry();

    ComplaintResponse complaintResponse = Mockito.mock(ComplaintResponse.class);
    doReturn(UUID.randomUUID()).when(complaintResponse).getId();
    doReturn("Poland").when(complaintResponse).getCountry();
    when(ipLocationService.getCountryByIp(anyString())).thenReturn("Poland");
    when(complaintDefaultService.createComplaint(any(AddComplaintRequest.class)))
      .thenReturn(complaintResponse);

    var resource = new ClassPathResource("requestAddComplaint.json");
    var requestBody = StreamUtils.copyToString(
      resource.getInputStream(),
      StandardCharsets.UTF_8
    );

    var result = mockMvc.perform(
      post("/api/v1/complaints")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
    );

    result
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").exists())
      .andExpect(jsonPath("$.country").value("Poland"));
  }

  @Test
  void getComplaint_ShouldReturnComplaintResponse() throws Exception {
    UUID id = UUID.randomUUID();
    ComplaintResponse complaintResponse = Mockito.mock(ComplaintResponse.class);
    doReturn(id).when(complaintResponse).getId();
    doReturn("Poland").when(complaintResponse).getCountry();
    when(complaintDefaultService.getComplaint(id)).thenReturn(complaintResponse);

    var result = mockMvc.perform(get("/api/v1/complaints/{id}", id));
    result
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(id.toString()))
      .andExpect(jsonPath("$.country").value("Poland"));
  }

  @Test
  void getAllComplaints_ShouldReturnListOfComplaints() throws Exception {
    ComplaintResponse complaint = Mockito.mock(ComplaintResponse.class);
    doReturn(UUID.fromString("c732cd78-572c-4059-bd62-61b9f5ed9251"))
      .when(complaint)
      .getId();
    doReturn("Poland").when(complaint).getCountry();
    doReturn(1).when(complaint).getReportCount();

    var pageOfComplaints = new PageImpl<>(
      Collections.singletonList(complaint),
      PageRequest.of(0, 10),
      1
    );
    when(complaintDefaultService.getAllComplaints(any(Pageable.class)))
      .thenReturn(pageOfComplaints);

    var result = mockMvc.perform(get("/api/v1/complaints/all"));
    var resource = new ClassPathResource("expectedComplaintList.json");
    var expectedJson = StreamUtils.copyToString(
      resource.getInputStream(),
      StandardCharsets.UTF_8
    );

    result
      .andExpect(status().isOk())
      .andExpect(MockMvcResultMatchers.content().json(expectedJson, true));
  }

  @Test
  void editComplaint_ShouldReturnUpdatedComplaintResponse() throws Exception {
    UUID id = UUID.randomUUID();
    doReturn(id).when(mockComplaintResponse).getId();
    doReturn("new content").when(mockComplaintResponse).getContent();
    doReturn("Poland").when(mockComplaintResponse).getCountry();
    when(complaintDefaultService.editComplaint(any(EditComplaintRequest.class)))
      .thenReturn(mockComplaintResponse);

    var resource = new ClassPathResource("requestEditComplaint.json");
    var requestBody = StreamUtils.copyToString(
      resource.getInputStream(),
      StandardCharsets.UTF_8
    );

    var result = mockMvc.perform(
      put("/api/v1/complaints")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
    );

    result
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(id.toString()))
      .andExpect(jsonPath("$.content").value("new content"))
      .andExpect(jsonPath("$.country").value("Poland"));
  }
}
