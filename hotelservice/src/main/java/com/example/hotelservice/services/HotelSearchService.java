package com.example.hotelservice.services;

import com.example.hotelservice.exceptions.ApiException;
import com.example.hotelservice.model.*;
import com.example.hotelservice.repository.AvailabilityWindowRepository;
import com.example.hotelservice.repository.ReservationRepository;
import com.example.hotelservice.resolver.dto.Offer;
import com.example.hotelservice.resolver.dto.SearchOfferRequest;
import com.example.hotelservice.resolver.dto.SearchOfferResponse;
import com.example.hotelservice.resolver.dto.HotelInfo;
import com.example.hotelservice.repository.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HotelSearchService {

  private final HotelRepository hotelRepository;
  private final AvailabilityWindowRepository windowRepository;
  private final ReservationRepository reservationRepository;
  private final AgencyAuthService agencyAuthService;

  public HotelSearchService(
          HotelRepository hotelRepository,
          AvailabilityWindowRepository windowRepository,
          ReservationRepository reservationRepository,
          AgencyAuthService agencyAuthService
  ) {
    this.hotelRepository = hotelRepository;
    this.windowRepository = windowRepository;
    this.reservationRepository = reservationRepository;
    this.agencyAuthService = agencyAuthService;
  }

  public SearchOfferResponse searchOffer(SearchOfferRequest request) {
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

    int nights = (int) ChronoUnit.DAYS.between(start, end);

    List<AvailabilityWindow> windows =
            windowRepository.findWindowsWithChambre(start, end);


    List<Long> chambreIds = windows.stream()
            .map(w -> w.getChambre().getId())
            .distinct()
            .toList();

    Map<Long, Long> overlappingByChambreId = reservationRepository
            .countOverlappingGrouped(chambreIds, start, end)
            .stream()
            .collect(Collectors.toMap(
                    ReservationRepository.ChambreReservationCount::getChambreId,
                    ReservationRepository.ChambreReservationCount::getCnt
            ));

    List<Offer> offers = new ArrayList<>();

    for (AvailabilityWindow w : windows) {
      Chambre c = w.getChambre();

      if (request.guests() > c.getNombreLits()) continue;

      long overlapping = overlappingByChambreId.getOrDefault(c.getId(), 0L);
      int remaining = w.getQuantity() - (int) overlapping;
      if (remaining <= 0) continue;

      double baseTotal = c.getPrixParNuit() * nights;
      double discountedTotal = agence.calculerPrix(baseTotal);

      offers.add(new Offer(
              String.valueOf(c.getId()),
              c.getNumero(),
              c.getTypeChambre(),
              c.getNombreLits(),
              nights,
              baseTotal,
              discountedTotal
      ));
    }

    return new SearchOfferResponse(hotelInfo(), offers);
  }

  private HotelInfo hotelInfo() {
    Hotel hotel = hotelRepository.findAll().stream().findFirst()
            .orElseThrow(() -> new ApiException(ErrorCode.INTERNAL, "Hotel not initialized"));
    return new HotelInfo(
            hotel.getNom(),
            hotel.getNombreEtoiles(),
            hotel.getAdresseComplete(),
            hotel.getVille(),
            hotel.getPays(),
            hotel.getLatitude(),
            hotel.getLongitude()
    );
  }
}
