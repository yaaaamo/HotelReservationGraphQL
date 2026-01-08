package com.example.hotelservice.data;

import com.example.hotelservice.model.*;
import com.example.hotelservice.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;


// Initialisation des données - Hôtel injecté depuis le profil Spring

@Configuration
public class DatabaseInitializer {

  private final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);


  @Value("${hotel.code}")
  private String hotelCode;

  @Value("${hotel.name}")
  private String hotelName;

  @Value("${hotel.stars}")
  private int hotelStars;

  @Value("${hotel.ville}")
  private String hotelVille;

  @Value("${hotel.pays}")
  private String hotelPays;

  @Value("${hotel.rue}")
  private String hotelRue;

  @Value("${hotel.numero}")
  private String hotelNumero;

  @Value("${hotel.latitude}")
  private Double hotelLat;

  @Value("${hotel.longitude}")
  private Double hotelLng;

  @Value("${hotel.image-url:}")
  private String roomImageUrl;


  // Initialise l'hôtel et ses chambres

  @Bean
  CommandLineRunner initDatabase(HotelRepository hotelRepo,
                                 AvailabilityWindowRepository winRepo) {
    return args -> {
      if (hotelRepo.count() == 0) {
        // Créer l'hôtel depuis les propriétés du profil
        Hotel hotel = new Hotel(
                hotelName,
                hotelStars,
                hotelPays,
                hotelVille,
                hotelRue,
                hotelNumero,
                hotelLat,
                hotelLng
        );

        // Créer les chambres
        Chambre c101 = new Chambre("101", RoomType.SIMPLE, 1, 80.0);
        Chambre c102 = new Chambre("102", RoomType.SIMPLE, 1, 80.0);
        Chambre c201 = new Chambre("201", RoomType.DOUBLE, 2, 120.0);
        Chambre c202 = new Chambre("202", RoomType.DOUBLE, 2, 120.0);
        Chambre c301 = new Chambre("301", RoomType.SUITE, 2, 200.0);
        Chambre c401 = new Chambre("401", RoomType.FAMILIALE, 4, 250.0);

        // Image URL
        if (roomImageUrl != null && !roomImageUrl.isEmpty()) {
          c101.setImageUrl(roomImageUrl);
          c102.setImageUrl(roomImageUrl);
          c201.setImageUrl(roomImageUrl);
          c202.setImageUrl(roomImageUrl);
          c301.setImageUrl(roomImageUrl);
          c401.setImageUrl(roomImageUrl);
        }

        // Rattacher à l'hôtel
        hotel.addChambre(c101);
        hotel.addChambre(c102);
        hotel.addChambre(c201);
        hotel.addChambre(c202);
        hotel.addChambre(c301);
        hotel.addChambre(c401);

        hotelRepo.save(hotel);

        // Créer les fenêtres de disponibilité
        winRepo.save(new AvailabilityWindow(
                LocalDate.of(2027, 12, 10),
                LocalDate.of(2027, 12, 20),
                1, c101));
        winRepo.save(new AvailabilityWindow(
                LocalDate.of(2027, 12, 10),
                LocalDate.of(2027, 12, 20),
                1, c102));
        winRepo.save(new AvailabilityWindow(
                LocalDate.of(2027, 12, 10),
                LocalDate.of(2027, 12, 20),
                2, c201));
        winRepo.save(new AvailabilityWindow(
                LocalDate.of(2027, 12, 10),
                LocalDate.of(2027, 12, 20),
                2, c202));
        winRepo.save(new AvailabilityWindow(
                LocalDate.of(2027, 12, 20),
                LocalDate.of(2027, 12, 30),
                3, c301));
        winRepo.save(new AvailabilityWindow(
                LocalDate.of(2027, 12, 20),
                LocalDate.of(2027, 12, 30),
                4, c401));

        logger.info("Hôtel créé: {} ({} étoiles) à {}", hotelName, hotelStars, hotelVille);
      } else {
        logger.info("Base déjà initialisée, aucun hôtel créé.");
      }
    };
  }


  //Initialise les agences partenaires

  @Bean
  CommandLineRunner initAgencies(AgenceRepository agencyRepo) {
    return args -> {
      if (agencyRepo.count() == 0) {
        // Agence 1: 10% de réduction (factor = 0.90)
        agencyRepo.save(new Agence("AGENCE1", "Agence Paris", "secret1", 0.90));
        // Agence 2: 20% de réduction (factor = 0.80)
        agencyRepo.save(new Agence("AGENCE2", "Agence Lyon", "secret2", 0.80));

        logger.info("Agences partenaires créées");
      }
    };
  }
}

