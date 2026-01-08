package com.example.agenceservice.controller;

import com.example.agenceservice.config.HotelsConfig;
import com.example.agenceservice.dto.dtos;
import com.example.agenceservice.services.HotelGatewayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AgenceController {

  private final HotelGatewayService gateway;
  private final HotelsConfig hotelsConfig;

  @Value("${agency.name}")
  private String agencyName;

  public AgenceController(HotelGatewayService gateway, HotelsConfig hotelsConfig) {
    this.gateway = gateway;
    this.hotelsConfig = hotelsConfig;
  }

  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("agencyName", agencyName);
    model.addAttribute("city", "");
    model.addAttribute("startDate", "");
    model.addAttribute("endDate", "");
    model.addAttribute("guests", 1);
    model.addAttribute("hotelsByUrl", hotelsByUrl());
    return "index";
  }

  @PostMapping("/search")
  public String search(
          @RequestParam String startDate,
          @RequestParam String endDate,
          @RequestParam int guests,
          @RequestParam String city,
          Model model
  ) {
    model.addAttribute("agencyName", agencyName);
    model.addAttribute("city", city);
    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);
    model.addAttribute("guests", guests);
    model.addAttribute("hotelsByUrl", hotelsByUrl());

    Map<String, dtos.SearchOfferResponse> results = filterByCity(gateway.searchAll(startDate, endDate, guests), city);
    model.addAttribute("results", results);

    return "index";
  }

  @PostMapping("/reserveForm")
  public String reserveForm(
          @RequestParam String hotelUrl,
          @RequestParam String chambreId,
          @RequestParam String startDate,
          @RequestParam String endDate,
          @RequestParam int guests,
          @RequestParam String city,
          Model model
  ) {
    model.addAttribute("agencyName", agencyName);
    model.addAttribute("city", city);
    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);
    model.addAttribute("guests", guests);
    model.addAttribute("hotelsByUrl", hotelsByUrl());

    Map<String, dtos.SearchOfferResponse> results = filterByCity(gateway.searchAll(startDate, endDate, guests), city);
    model.addAttribute("results", results);

    model.addAttribute("reserveHotelUrl", hotelUrl);
    model.addAttribute("reserveChambreId", chambreId);

    return "index";
  }

  @PostMapping("/reserve")
  public String reserve(
          @RequestParam String hotelUrl,
          @RequestParam String chambreId,
          @RequestParam String startDate,
          @RequestParam String endDate,
          @RequestParam int guests,
          @RequestParam String city,
          @RequestParam String firstName,
          @RequestParam String lastName,
          @RequestParam(required = false) String email,
          @RequestParam(required = false) String phone,
          Model model
  ) {
    model.addAttribute("agencyName", agencyName);
    model.addAttribute("city", city);
    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);
    model.addAttribute("guests", guests);
    model.addAttribute("hotelsByUrl", hotelsByUrl());

    Map<String, dtos.SearchOfferResponse> results = filterByCity(gateway.searchAll(startDate, endDate, guests), city);
    model.addAttribute("results", results);

    try {
      var resp = gateway.makeReservation(
              hotelUrl,
              chambreId,
              startDate,
              endDate,
              new dtos.ClientInput(firstName, lastName, email, phone)
      );
      model.addAttribute("reservationResponse", resp);
    } catch (Exception e) {
      model.addAttribute("errorMessage", extractMessage(e));
      model.addAttribute("reserveHotelUrl", hotelUrl);
      model.addAttribute("reserveChambreId", chambreId);
    }

    return "index";
  }

  private Map<String, String> hotelsByUrl() {
    Map<String, String> m = new LinkedHashMap<>();
    for (HotelsConfig.HotelConfig h : hotelsConfig.getHotels()) {
      m.put(h.getUrl(), h.getName());
    }
    return m;
  }

  private Map<String, dtos.SearchOfferResponse> filterByCity(Map<String, dtos.SearchOfferResponse> results, String city) {
    if (city == null || city.isBlank()) return new LinkedHashMap<>(results);

    return results.entrySet().stream()
            .filter(e -> e.getValue() != null && e.getValue().hotel() != null)
            .filter(e -> e.getValue().hotel().city() != null)
            .filter(e -> e.getValue().hotel().city().equalsIgnoreCase(city))
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (a, b) -> a,
                    LinkedHashMap::new
            ));
  }

  private String extractMessage(Exception e) {
    String msg = e.getMessage();
    if (msg == null || msg.isBlank()) return "Reservation failed";
    return msg;
  }
}
