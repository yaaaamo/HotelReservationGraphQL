package com.example.hotelservice.resolver.dto;


public record HotelInfo(
        String name,
        int stars,
        String address,
        String city,
        String country,
        Double latitude,
        Double longitude
) {}

