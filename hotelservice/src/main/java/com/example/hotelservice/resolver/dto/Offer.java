package com.example.hotelservice.resolver.dto;

import com.example.hotelservice.model.RoomType;

public record Offer(
        String chambreId,
        String roomNumber,
        RoomType roomType,
        int beds,
        int nights,
        double basePriceTotal,
        double discountedPriceTotal
) {}

