package com.example.hotelservice.resolver.dto;

public record MakeReservationRequest(AgencyAuthInput auth, String chambreId, String startDate, String endDate,
                              ClientInput client) {}
