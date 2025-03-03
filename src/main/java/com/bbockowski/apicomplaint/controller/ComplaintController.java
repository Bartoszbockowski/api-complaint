package com.bbockowski.apicomplaint.controller;

import com.bbockowski.apicomplaint.dto.AddComplaintRequest;
import com.bbockowski.apicomplaint.dto.ComplaintResponse;
import com.bbockowski.apicomplaint.dto.EditComplaintRequest;
import com.bbockowski.apicomplaint.errorhandling.ErrorResponse;
import com.bbockowski.apicomplaint.service.ComplaintDefaultService;
import com.bbockowski.apicomplaint.service.IpLocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling operations related to complaints.
 * Exposes endpoints for creating, retrieving, and updating complaints.
 */
@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
public class ComplaintController {
  private final ComplaintDefaultService complaintDefaultService;
  private final IpLocationService ipLocationService;

  /**
   * Creates a new complaint or increments an existing one, based on the productId and reporter.
   * Determines the complainant's country using the IP address.
   *
   * @param addComplaintRequest the DTO containing complaint details
   * @param request             the incoming HTTP request (used for IP extraction)
   * @return the newly created or updated complaint as a response
   */
  @Operation(summary = "Add or increment a complaint")
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Complaint added or updated successfully",
        content = @Content(schema = @Schema(implementation = ComplaintResponse.class))
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid request format",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "502",
        description = "Bad Gateway - error in external IP location service",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
    }
  )
  @PostMapping
  public ResponseEntity<ComplaintResponse> addComplaint(
    @RequestBody AddComplaintRequest addComplaintRequest,
    HttpServletRequest request
  ) {
    String clientIp = Optional
      .ofNullable(request.getHeader("X-Forwarded-For"))
      .orElse(request.getRemoteAddr());

    addComplaintRequest.setCountry(ipLocationService.getCountryByIp(clientIp));

    return ResponseEntity.ok(
      complaintDefaultService.createComplaint(addComplaintRequest)
    );
  }

  /**
   * Retrieves a specific complaint by its UUID.
   *
   * @param id the UUID of the complaint
   * @return the complaint as a response entity
   */
  @Operation(summary = "Get a complaint by ID")
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Complaint retrieved successfully",
        content = @Content(schema = @Schema(implementation = ComplaintResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Complaint not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
    }
  )
  @GetMapping("/{id}")
  public ResponseEntity<ComplaintResponse> getComplaint(@PathVariable UUID id) {
    return ResponseEntity.ok(complaintDefaultService.getComplaint(id));
  }

  /**
   * Retrieves all complaints in a paginated form.
   *
   * @param pageable pagination configuration
   * @return a page of complaint responses
   */
  @Operation(summary = "Get all complaints in paginated form")
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Complaints retrieved successfully",
        content = @Content(schema = @Schema(implementation = ComplaintResponse.class))
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
    }
  )
  @GetMapping("/all")
  public ResponseEntity<Page<ComplaintResponse>> getAllComplaints(
    @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    return ResponseEntity.ok(complaintDefaultService.getAllComplaints(pageable));
  }

  /**
   * Updates the content of an existing complaint, if found.
   *
   * @param editComplaintRequest the DTO containing updated complaint data
   * @return the updated complaint as a response
   */
  @Operation(summary = "Update a complaint")
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Complaint updated successfully",
        content = @Content(schema = @Schema(implementation = ComplaintResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Complaint not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid request format",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
    }
  )
  @PutMapping
  public ResponseEntity<ComplaintResponse> editComplaint(
    @Valid @RequestBody EditComplaintRequest editComplaintRequest
  ) {
    return ResponseEntity.ok(complaintDefaultService.editComplaint(editComplaintRequest));
  }
}
