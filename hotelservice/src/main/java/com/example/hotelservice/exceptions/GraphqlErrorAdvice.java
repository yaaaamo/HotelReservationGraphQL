package com.example.hotelservice.exceptions;
import com.example.hotelservice.model.ErrorCode;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice(annotations = Controller.class)
public class GraphqlErrorAdvice {

  @GraphQlExceptionHandler(ApiException.class)
  public GraphQLError handleApi(ApiException ex) {
    Map<String, Object> extensions = new LinkedHashMap<>();
    extensions.put("code", ex.getCode().name());
    if (ex.getDetails() != null && !ex.getDetails().isEmpty()) {
      extensions.put("details", ex.getDetails());
    }

    return GraphqlErrorBuilder.newError()
            .message(ex.getMessage())
            .extensions(extensions)
            .build();
  }

  @GraphQlExceptionHandler(Exception.class)
  public GraphQLError handleOther(Exception ex) {
    return GraphqlErrorBuilder.newError()
            .message("Internal error")
            .extensions(Map.of("code", ErrorCode.INTERNAL.name()))
            .build();
  }
}

