package com.example.hotelservice.services;
import com.example.hotelservice.exceptions.ApiException;
import com.example.hotelservice.model.*;
import com.example.hotelservice.repository.AvailabilityWindowRepository;
import com.example.hotelservice.repository.ChambreRepository;
import com.example.hotelservice.repository.ReservationRepository;
import com.example.hotelservice.resolver.dto.MakeReservationRequest;
import com.example.hotelservice.resolver.dto.MakeReservationResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HotelReservationService {

  private final AvailabilityWindowRepository windowRepository;
  private final ChambreRepository chambreRepository;
  private final ReservationRepository reservationRepository;
  private final AgencyAuthService agencyAuthService;

  public HotelReservationService(
          AvailabilityWindowRepository windowRepository,
          ChambreRepository chambreRepository,
          ReservationRepository reservationRepository,
          AgencyAuthService agencyAuthService
  ) {
    this.windowRepository = windowRepository;
    this.chambreRepository = chambreRepository;
    this.reservationRepository = reservationRepository;
    this.agencyAuthService = agencyAuthService;
  }

  public MakeReservationResponse makeReservation(MakeReservationRequest request) {
    Agence agence = agencyAuthService.requireValidAgency(request.auth().agenceId(), request.auth().password());

    LocalDate start;
    LocalDate end;
    try {
      start = LocalDate.parse(request.startDate());
      end = LocalDate.parse(request.endDate());
    } catch (DateTimeParseException e) {
      throw new ApiException(
              ErrorCode.BAD_REQUEST,
              "Invalid date format",
              Map.of("startDate", request.startDate(), "endDate", request.endDate())
      );
    }

    if (!end.isAfter(start)) {
      throw new ApiException(
              ErrorCode.BAD_REQUEST,
              "endDate must be after startDate",
              Map.of("startDate", start.toString(), "endDate", end.toString())
      );
    }

    long chambreId;
    try {
      chambreId = Long.parseLong(request.chambreId());
    } catch (NumberFormatException e) {
      throw new ApiException(
              ErrorCode.BAD_REQUEST,
              "Invalid chambreId",
              Map.of("chambreId", request.chambreId())
      );
    }

    Chambre chambre = chambreRepository.findById(chambreId)
            .orElseThrow(() -> new ApiException(
                    ErrorCode.NOT_FOUND,
                    "Chambre not found",
                    Map.of("chambreId", String.valueOf(chambreId))
            ));

    List<AvailabilityWindow> windows =
            windowRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(end, start);

    AvailabilityWindow windowForRoom = null;
    for (AvailabilityWindow w : windows) {
      if (w.getChambre().getId().equals(chambre.getId())) {
        windowForRoom = w;
        break;
      }
    }

    if (windowForRoom == null) {
      throw new ApiException(
              ErrorCode.NOT_FOUND,
              "No availability window for requested dates",
              Map.of(
                      "chambreId", String.valueOf(chambreId),
                      "startDate", start.toString(),
                      "endDate", end.toString()
              )
      );
    }

    long overlapping = reservationRepository.countOverlappingReservations(chambre, start, end);
    int remaining = windowForRoom.getQuantity() - (int) overlapping;
    if (remaining <= 0) {
      throw new ApiException(
              ErrorCode.CONFLICT,
              "No remaining availability",
              Map.of(
                      "chambreId", String.valueOf(chambreId),
                      "startDate", start.toString(),
                      "endDate", end.toString()
              )
      );
    }

    int nights = (int) ChronoUnit.DAYS.between(start, end);
    double baseTotal = chambre.getPrixParNuit() * nights;
    double total = agence.calculerPrix(baseTotal);

    Reservation r = new Reservation();
    r.genererReference();
    r.setDateArrivee(start);
    r.setDateDepart(end);
    r.setPrenomClient(request.client().firstName());
    r.setNomClient(request.client().lastName());
    r.setEmailClient(request.client().email());
    r.setTelephoneClient(request.client().phone());
    r.setMontantTotal(total);
    r.setStatut(ReservationStatus.CONFIRMED);
    r.setAgenceId(agence.getId());
    r.setChambre(chambre);

    reservationRepository.save(r);

    return new MakeReservationResponse(ReservationStatus.CONFIRMED, r.getReference(), total);
  }
}
