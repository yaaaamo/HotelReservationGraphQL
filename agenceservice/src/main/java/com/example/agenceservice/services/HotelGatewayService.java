package com.example.agenceservice.services;

import com.example.agenceservice.config.HotelsConfig;
import com.example.agenceservice.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.GraphQlClientException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class HotelGatewayService {

  private static final Logger logger = LoggerFactory.getLogger(HotelGatewayService.class);

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
        logger.info("Successfully connected to hotel: {}", h.getName());

      } catch (WebClientRequestException e) {

        logger.warn("Connection failed to hotel {}: {}", h.getName(), e.getMessage());
        results.put(h.getUrl(), dtos.SearchOfferResponse.unavailable(
                h.getName(),
                "Service unavailable - Connection refused"
        ));

      } catch (GraphQlClientException e) {

        String errorMessage = extractGraphQlError(e);
        logger.warn("GraphQL error from hotel {}: {}", h.getName(), errorMessage);

        if (errorMessage.contains("UNAUTHORIZED") || errorMessage.contains("credentials")) {
          results.put(h.getUrl(), dtos.SearchOfferResponse.authError(
                  h.getName(),
                  "Authentication failed - Invalid agency credentials"
          ));
        } else {
          results.put(h.getUrl(), new dtos.SearchOfferResponse(
                  new dtos.HotelInfo(h.getName(), 0, null, null, null, null, null),
                  java.util.List.of(),
                  errorMessage,
                  dtos.ConnectionStatus.ERROR
          ));
        }

      } catch (Exception e) {

        logger.error("Unexpected error from hotel {}: {}", h.getName(), e.getMessage());
        results.put(h.getUrl(), new dtos.SearchOfferResponse(
                new dtos.HotelInfo(h.getName(), 0, null, null, null, null, null),
                java.util.List.of(),
                "Unexpected error: " + e.getMessage(),
                dtos.ConnectionStatus.ERROR
        ));
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
          offers { chambreId roomNumber roomType beds nights basePriceTotal discountedPriceTotal imageUrl }
        }
      }
    """;

    dtos.SearchOfferRequest req = new dtos.SearchOfferRequest(auth(), startDate, endDate, guests);


    GraphQlSearchResponse graphQlResponse = hotel.client()
            .document(document)
            .variable("req", req)
            .retrieve("searchOffer")
            .toEntity(GraphQlSearchResponse.class)
            .block();


    if (graphQlResponse != null) {
      return new dtos.SearchOfferResponse(
              graphQlResponse.hotel(),
              graphQlResponse.offers() != null ? graphQlResponse.offers() : java.util.List.of(),
              null,
              dtos.ConnectionStatus.CONNECTED
      );
    }

    return new dtos.SearchOfferResponse(null, java.util.List.of(), null, dtos.ConnectionStatus.CONNECTED);
  }


  private record GraphQlSearchResponse(dtos.HotelInfo hotel, java.util.List<dtos.Offer> offers) {}

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


  private String extractGraphQlError(GraphQlClientException e) {
    if (e.getMessage() != null) {
      return e.getMessage();
    }
    return "GraphQL error";
  }
}