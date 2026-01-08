package com.example.hotelservice.services;

import com.example.hotelservice.model.Agence;
import com.example.hotelservice.repository.AgenceRepository;
import org.springframework.stereotype.Service;

@Service
public class AgencyAuthService {

  private final AgenceRepository agenceRepository;

  public AgencyAuthService(AgenceRepository agenceRepository) {
    this.agenceRepository = agenceRepository;
  }

  public Agence requireValidAgency(String agenceId, String password) {
    Agence agence = agenceRepository.findById(agenceId)
            .orElseThrow(() -> new UnauthorizedAgencyException("UNKNOWN_AGENCY"));
    if (!agence.validateCredentials(agenceId, password)) {
      throw new UnauthorizedAgencyException("BAD_CREDENTIALS");
    }
    return agence;
  }
}

