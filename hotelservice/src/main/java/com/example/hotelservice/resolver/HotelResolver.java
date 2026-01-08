package com.example.hotelservice.resolver;


import com.example.hotelservice.model.*;
import com.example.hotelservice.repository.*;
import com.example.hotelservice.services.AgencyAuthService;
import jakarta.transaction.Transactional;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Controller
@Transactional
public class HotelResolver {

  private final HotelRepository hotelRepository;
  private final AvailabilityWindowRepository windowRepository;
  private final ChambreRepository chambreRepository;
  private final ReservationRepository reservationRepository;
  private final AgencyAuthService agencyAuthService;

  public HotelResolver(
          HotelRepository hotelRepository,
          AvailabilityWindowRepository windowRepository,
          ChambreRepository chambreRepository,
          ReservationRepository reservationRepository,
          AgencyAuthService agencyAuthService
  ) {
    this.hotelRepository = hotelRepository;
    this.windowRepository = windowRepository;
    this.chambreRepository = chambreRepository;
    this.reservationRepository = reservationRepository;
    this.agencyAuthService = agencyAuthService;
  }

  @QueryMapping
  public SearchOfferResponse searchOffer(@Argument SearchOfferRequest request) {
    Agence agence = agencyAuthService.requireValidAgency(request.auth().agenceId(), request.auth().password());

    LocalDate start = LocalDate.parse(request.startDate());
    LocalDate end = LocalDate.parse(request.endDate());
    if (!end.isAfter(start)) {
      return new SearchOfferResponse(hotelInfo(), List.of());
    }

    int nights = (int) ChronoUnit.DAYS.between(start, end);

    List<AvailabilityWindow> windows = windowRepository
            .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(end, start);

    List<Offer> offers = new ArrayList<>();

    for (AvailabilityWindow w : windows) {
      Chambre c = w.getChambre();

      if (request.guests() > c.getNombreLits()) continue;

      long overlapping = reservationRepository.countOverlappingReservations(c, start, end);
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

  @MutationMapping
  public MakeReservationResponse makeReservation(@Argument MakeReservationRequest request) {
    Agence agence = agencyAuthService.requireValidAgency(request.auth().agenceId(), request.auth().password());

    LocalDate start = LocalDate.parse(request.startDate());
    LocalDate end = LocalDate.parse(request.endDate());
    if (!end.isAfter(start)) {
      return new MakeReservationResponse(ReservationStatus.REFUSED, null, null);
    }

    long chambreId = Long.parseLong(request.chambreId());
    Chambre chambre = chambreRepository.findById(chambreId).orElse(null);
    if (chambre == null) {
      return new MakeReservationResponse(ReservationStatus.REFUSED, null, null);
    }

    List<AvailabilityWindow> windows = windowRepository
            .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(end, start);

    AvailabilityWindow windowForRoom = null;
    for (AvailabilityWindow w : windows) {
      if (w.getChambre().getId().equals(chambre.getId())) {
        windowForRoom = w;
        break;
      }
    }
    if (windowForRoom == null) {
      return new MakeReservationResponse(ReservationStatus.REFUSED, null, null);
    }

    long overlapping = reservationRepository.countOverlappingReservations(chambre, start, end);
    int remaining = windowForRoom.getQuantity() - (int) overlapping;
    if (remaining <= 0) {
      return new MakeReservationResponse(ReservationStatus.REFUSED, null, null);
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

  private HotelInfo hotelInfo() {
    Hotel hotel = hotelRepository.findAll().stream().findFirst().orElse(null);
    if (hotel == null) {
      return new HotelInfo("UNKNOWN", 0, "", "", "", null, null);
    }
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

