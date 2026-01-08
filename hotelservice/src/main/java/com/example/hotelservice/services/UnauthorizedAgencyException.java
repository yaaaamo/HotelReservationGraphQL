package com.example.hotelservice.services;


public class UnauthorizedAgencyException extends RuntimeException {
  public UnauthorizedAgencyException(String message) {
    super(message);
  }
}

