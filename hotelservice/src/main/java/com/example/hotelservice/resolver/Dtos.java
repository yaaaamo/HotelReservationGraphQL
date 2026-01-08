package com.example.hotelservice.resolver;


import com.example.hotelservice.model.ReservationStatus;
import com.example.hotelservice.model.RoomType;

import java.util.List;

public class Dtos {
}

record AgencyAuthInput(String agenceId, String password) {}

record SearchOfferRequest(AgencyAuthInput auth, String startDate, String endDate, int guests) {}

record SearchOfferResponse(HotelInfo hotel, List<Offer> offers) {}

record HotelInfo(String name, int stars, String address, String city, String country,
                 Double latitude, Double longitude) {}

record Offer(String chambreId, String roomNumber, RoomType roomType, int beds, int nights,
             double basePriceTotal, double discountedPriceTotal) {}

record MakeReservationRequest(AgencyAuthInput auth, String chambreId, String startDate, String endDate,
                              ClientInput client) {}

record ClientInput(String firstName, String lastName, String email, String phone) {}

record MakeReservationResponse(ReservationStatus status, String reference, Double totalAmount) {}

