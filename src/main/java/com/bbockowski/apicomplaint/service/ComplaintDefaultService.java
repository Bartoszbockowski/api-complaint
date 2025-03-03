package com.bbockowski.apicomplaint.service;

import com.bbockowski.apicomplaint.dto.AddComplaintRequest;
import com.bbockowski.apicomplaint.dto.ComplaintResponse;
import com.bbockowski.apicomplaint.dto.EditComplaintRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ComplaintDefaultService {
  ComplaintResponse createComplaint(AddComplaintRequest addComplaintRequest);

  ComplaintResponse editComplaint(EditComplaintRequest editComplaintRequest);

  ComplaintResponse getComplaint(UUID id);

  Page<ComplaintResponse> getAllComplaints(Pageable pageable);
}
