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

  public record SearchOfferResponse(
          HotelInfo hotel,
          List<Offer> offers,
          String errorMessage,
          ConnectionStatus status
  ) {
    // Başarılı response
    public SearchOfferResponse(HotelInfo hotel, List<Offer> offers) {
      this(hotel, offers, null, ConnectionStatus.CONNECTED);
    }

    // Hata response - hotel bilgisi ile
    public static SearchOfferResponse unavailable(String hotelName, String errorMessage) {
      return new SearchOfferResponse(
              new HotelInfo(hotelName, 0, null, null, null, null, null),
              List.of(),
              errorMessage,
              ConnectionStatus.UNAVAILABLE
      );
    }

    // Hata response - authentication hatası
    public static SearchOfferResponse authError(String hotelName, String errorMessage) {
      return new SearchOfferResponse(
              new HotelInfo(hotelName, 0, null, null, null, null, null),
              List.of(),
              errorMessage,
              ConnectionStatus.AUTH_ERROR
      );
    }

    public boolean isAvailable() {
      return status == ConnectionStatus.CONNECTED;
    }
  }

  public enum ConnectionStatus {
    CONNECTED,      // Hotel servisi çalışıyor
    UNAVAILABLE,    // Bağlantı hatası
    AUTH_ERROR,     // Kimlik doğrulama hatası
    ERROR           // Diğer hatalar
  }

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
          double discountedPriceTotal,
          String imageUrl
  ) {}

  public record MakeReservationResponse(String status, String reference, Double totalAmount) {}

}