package com.example.hotelservice.exceptions;
import com.example.hotelservice.model.ErrorCode;

import java.util.Map;

public class ApiException extends RuntimeException {
  private final ErrorCode code;
  private final Map<String, Object> details;

  public ApiException(ErrorCode code, String message) {
    super(message);
    this.code = code;
    this.details = Map.of();
  }

  public ApiException(ErrorCode code, String message, Map<String, Object> details) {
    super(message);
    this.code = code;
    this.details = details == null ? Map.of() : details;
  }

  public ErrorCode getCode() {
    return code;
  }

  public Map<String, Object> getDetails() {
    return details;
  }
}

