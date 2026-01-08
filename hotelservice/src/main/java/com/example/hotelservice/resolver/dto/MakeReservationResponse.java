package com.example.hotelservice.resolver.dto;

import com.example.hotelservice.model.ReservationStatus;

public record MakeReservationResponse(ReservationStatus status, String reference, Double totalAmount) {}
