package com.example.agenceservice.services;

import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;

public class HotelGraphqlClient {

  private final HttpGraphQlClient client;

  public HotelGraphqlClient(String graphqlUrl) {
    WebClient webClient = WebClient.builder().baseUrl(graphqlUrl).build();
    this.client = HttpGraphQlClient.builder(webClient).build();
  }

  public HttpGraphQlClient client() {
    return client;
  }
}

