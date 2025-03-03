package com.bbockowski.apicomplaint.service;

import com.bbockowski.apicomplaint.dto.AddComplaintRequest;
import com.bbockowski.apicomplaint.dto.ComplaintResponse;
import com.bbockowski.apicomplaint.dto.EditComplaintRequest;
import com.bbockowski.apicomplaint.exception.ComplaintCreateException;
import com.bbockowski.apicomplaint.exception.ComplaintNotFoundException;
import com.bbockowski.apicomplaint.model.Complaint;
import com.bbockowski.apicomplaint.repository.ComplaintRepository;
import jakarta.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ComplaintDefaultServiceImpl implements ComplaintDefaultService {
  private final ComplaintRepository complaintRepository;
  private final ModelMapper modelMapper;

  @Override
  public ComplaintResponse createComplaint(AddComplaintRequest addComplaintRequest) {
    try {
      Optional<Complaint> existingComplaintOpt = complaintRepository.findByProductIdAndReporter(
        addComplaintRequest.getProductId(),
        addComplaintRequest.getReporter()
      );

      if (existingComplaintOpt.isPresent()) {
        Complaint existingComplaint = existingComplaintOpt.get();
        existingComplaint.setReportCount(existingComplaint.getReportCount() + 1);
        complaintRepository.save(existingComplaint);

        return modelMapper.map(existingComplaint, ComplaintResponse.class);
      } else {
        Complaint newComplaint = new Complaint();
        newComplaint.setProductId(addComplaintRequest.getProductId());
        newComplaint.setContent(addComplaintRequest.getContent());
        newComplaint.setCreatedAt(Timestamp.from(Instant.now()));
        newComplaint.setReporter(addComplaintRequest.getReporter());
        newComplaint.setCountry(addComplaintRequest.getCountry());
        newComplaint.setReportCount(1);

        complaintRepository.save(newComplaint);
        return modelMapper.map(newComplaint, ComplaintResponse.class);
      }
    } catch (Exception e) {
      throw new ComplaintCreateException(
        "Failed to create or update complaint: " + e.getMessage(),
        e
      );
    }
  }

  @Override
  public ComplaintResponse getComplaint(UUID id) {
    Complaint complaintEntity = complaintRepository
      .findById(id)
      .orElseThrow(
        () -> new ComplaintNotFoundException("Complaint not found with id: " + id)
      );

    return modelMapper.map(complaintEntity, ComplaintResponse.class);
  }

  @Override
  public Page<ComplaintResponse> getAllComplaints(Pageable pageable) {
    return complaintRepository
      .findAll(pageable)
      .map(complaint -> modelMapper.map(complaint, ComplaintResponse.class));
  }

  @Override
  public ComplaintResponse editComplaint(EditComplaintRequest editComplaintRequest) {
    Complaint complaintToUpdate = complaintRepository
      .findById(UUID.fromString(editComplaintRequest.getId()))
      .orElseThrow(
        () ->
          new ComplaintNotFoundException(
            "Complaint not found with id: " + editComplaintRequest.getId()
          )
      );

    if (editComplaintRequest.getContent() != null) {
      complaintToUpdate.setContent(editComplaintRequest.getContent());
      complaintRepository.save(complaintToUpdate);
    }

    return modelMapper.map(complaintToUpdate, ComplaintResponse.class);
  }
}
