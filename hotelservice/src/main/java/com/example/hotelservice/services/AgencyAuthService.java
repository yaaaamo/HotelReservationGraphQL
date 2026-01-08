package com.example.hotelservice.services;
import com.example.hotelservice.exceptions.ApiException;
import com.example.hotelservice.model.Agence;
import com.example.hotelservice.model.ErrorCode;
import com.example.hotelservice.repository.AgenceRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AgencyAuthService {

  private final AgenceRepository agenceRepository;

  public AgencyAuthService(AgenceRepository agenceRepository) {
    this.agenceRepository = agenceRepository;
  }

  public Agence requireValidAgency(String agenceId, String password) {
    Agence agence = agenceRepository.findById(agenceId)
            .orElseThrow(() -> new ApiException(
                    ErrorCode.UNAUTHORIZED,
                    "Unknown agency",
                    Map.of("reason", "UNKNOWN_AGENCY", "agenceId", agenceId)
            ));

    if (!agence.validateCredentials(agenceId, password)) {
      throw new ApiException(
              ErrorCode.UNAUTHORIZED,
              "Bad credentials",
              Map.of("reason", "BAD_CREDENTIALS", "agenceId", agenceId)
      );
    }
    return agence;
  }
}
