package com.example.hotelservice.resolver.dto;


public record SearchOfferRequest(AgencyAuthInput auth, String startDate, String endDate, int guests) {}
