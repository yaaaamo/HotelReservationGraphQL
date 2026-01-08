package com.example.agenceservice.services;

import com.example.agenceservice.config.HotelsConfig;
import com.example.agenceservice.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class HotelGatewayService {

  private final HotelsConfig hotelsConfig;

  @Value("${agency.id}")
  private String agencyId;

  @Value("${agency.password}")
  private String agencyPassword;

  public HotelGatewayService(HotelsConfig hotelsConfig) {
    this.hotelsConfig = hotelsConfig;
  }

  private dtos.AgencyAuthInput auth() {
    return new dtos.AgencyAuthInput(agencyId, agencyPassword);
  }

  public Map<String, dtos.SearchOfferResponse> searchAll(String startDate, String endDate, int guests) {
    Map<String, dtos.SearchOfferResponse> results = new LinkedHashMap<>();
    for (HotelsConfig.HotelConfig h : hotelsConfig.getHotels()) {
      try {
        dtos.SearchOfferResponse r = searchOne(h.getUrl(), startDate, endDate, guests);
        results.put(h.getUrl(), r);
      } catch (Exception e) {
        results.put(h.getUrl(), new dtos.SearchOfferResponse(null, java.util.List.of()));
      }
    }
    return results;
  }

  public dtos.SearchOfferResponse searchOne(String graphqlUrl, String startDate, String endDate, int guests) {
    HotelGraphqlClient hotel = new HotelGraphqlClient(graphqlUrl);

    String document = """
      query($req: SearchOfferRequest!) {
        searchOffer(request: $req) {
          hotel { name stars address city country latitude longitude }
          offers { chambreId roomNumber roomType beds nights basePriceTotal discountedPriceTotal }
        }
      }
    """;

    dtos.SearchOfferRequest req = new dtos.SearchOfferRequest(auth(), startDate, endDate, guests);

    return hotel.client()
            .document(document)
            .variable("req", req)
            .retrieve("searchOffer")
            .toEntity(dtos.SearchOfferResponse.class)
            .block();
  }

  public dtos.MakeReservationResponse makeReservation(
          String graphqlUrl,
          String chambreId,
          String startDate,
          String endDate,
          dtos.ClientInput clientInput
  ) {
    HotelGraphqlClient hotel = new HotelGraphqlClient(graphqlUrl);

    String document = """
      mutation($req: MakeReservationRequest!) {
        makeReservation(request: $req) {
          status
          reference
          totalAmount
        }
      }
    """;

    dtos.MakeReservationRequest req = new dtos.MakeReservationRequest(auth(), chambreId, startDate, endDate, clientInput);

    return hotel.client()
            .document(document)
            .variable("req", req)
            .retrieve("makeReservation")
            .toEntity(dtos.MakeReservationResponse.class)
            .block();
  }
}
