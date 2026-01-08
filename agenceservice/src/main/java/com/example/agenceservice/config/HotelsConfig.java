package com.example.agenceservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "")
public class HotelsConfig {

  private List<HotelConfig> hotels = new ArrayList<>();

  public List<HotelConfig> getHotels() {
    return hotels;
  }

  public void setHotels(List<HotelConfig> hotels) {
    this.hotels = hotels;
  }

  public static class HotelConfig {
    private String name;
    private String url;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    @Override
    public String toString() {
      return name + " (" + url + ")";
    }
  }
}

