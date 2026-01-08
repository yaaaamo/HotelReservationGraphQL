package com.example.hotelservice.resolver;

import com.example.hotelservice.resolver.dto.MakeReservationRequest;
import com.example.hotelservice.resolver.dto.MakeReservationResponse;
import com.example.hotelservice.resolver.dto.SearchOfferRequest;
import com.example.hotelservice.resolver.dto.SearchOfferResponse;
import com.example.hotelservice.services.HotelReservationService;
import com.example.hotelservice.services.HotelSearchService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class HotelResolver {

  private final HotelSearchService searchService;
  private final HotelReservationService reservationService;

  public HotelResolver(HotelSearchService searchService, HotelReservationService reservationService) {
    this.searchService = searchService;
    this.reservationService = reservationService;
  }

  @QueryMapping
  public SearchOfferResponse searchOffer(@Argument SearchOfferRequest request) {
    return searchService.searchOffer(request);
  }

  @MutationMapping
  public MakeReservationResponse makeReservation(@Argument MakeReservationRequest request) {
    return reservationService.makeReservation(request);
  }
}
