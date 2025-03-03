package com.bbockowski.apicomplaint.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class IpLocationService {
  private final RestTemplate restTemplate;

  @CircuitBreaker(name = "ipLocationService", fallbackMethod = "fallbackCountry")
  @Retry(name = "ipLocationServiceRetry")
  public String getCountryByIp(String ip) {
    String requestUrl = UriComponentsBuilder
      .fromUriString("http://ip-api.com/json/" + ip)
      .queryParam("fields", "country")
      .toUriString();

    IpLocationResponse locationResponse = restTemplate.getForObject(
      requestUrl,
      IpLocationResponse.class
    );

    if (locationResponse == null || locationResponse.getCountry() == null) {
      return "Unknown";
    }
    return locationResponse.getCountry();
  }

  public String fallbackCountry(String ip, Throwable t) {
    return "Fallback Country";
  }
}

@Setter
@Getter
class IpLocationResponse {
  private String country;
}
