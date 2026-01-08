package com.example.hotelservice.resolver.dto;



import java.util.List;

public record SearchOfferResponse(HotelInfo hotel, List<Offer> offers) {}

