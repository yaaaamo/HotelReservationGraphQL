package com.example.agenceservice.dto;
import java.util.List;

public class dtos {


  public record AgencyAuthInput(String agenceId, String password) {}

  public record SearchOfferRequest(AgencyAuthInput auth, String startDate, String endDate, int guests) {}

  public record MakeReservationRequest(
          AgencyAuthInput auth,
          String chambreId,
          String startDate,
          String endDate,
          ClientInput client
  ) {}

  public record ClientInput(String firstName, String lastName, String email, String phone) {}

  public record SearchOfferResponse(HotelInfo hotel, List<Offer> offers) {}

  public record HotelInfo(
          String name,
          int stars,
          String address,
          String city,
          String country,
          Double latitude,
          Double longitude
  ) {}

  public record Offer(
          String chambreId,
          String roomNumber,
          String roomType,
          int beds,
          int nights,
          double basePriceTotal,
          double discountedPriceTotal
  ) {}

  public record MakeReservationResponse(String status, String reference, Double totalAmount) {}

}
