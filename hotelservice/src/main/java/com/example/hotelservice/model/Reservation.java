package com.example.hotelservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String reference;

  @Column(nullable = false)
  private LocalDate dateArrivee;

  @Column(nullable = false)
  private LocalDate dateDepart;

  @Column(nullable = false)
  private String nomClient;

  @Column(nullable = false)
  private String prenomClient;

  private String emailClient;
  private String telephoneClient;

  @Column(nullable = false)
  private double montantTotal;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ReservationStatus statut;

  private String agenceId;

  @Column(nullable = false)
  private LocalDateTime dateReservation;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chambre_id", nullable = false)
  private Chambre chambre;

  public Reservation() {
    this.dateReservation = LocalDateTime.now();
    this.statut = ReservationStatus.CONFIRMED;
  }

  // Génère une référence unique
  public void genererReference() {
    this.reference = "RES-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
  }


  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getReference() { return reference; }
  public void setReference(String reference) { this.reference = reference; }

  public LocalDate getDateArrivee() { return dateArrivee; }
  public void setDateArrivee(LocalDate dateArrivee) { this.dateArrivee = dateArrivee; }

  public LocalDate getDateDepart() { return dateDepart; }
  public void setDateDepart(LocalDate dateDepart) { this.dateDepart = dateDepart; }

  public String getNomClient() { return nomClient; }
  public void setNomClient(String nomClient) { this.nomClient = nomClient; }

  public String getPrenomClient() { return prenomClient; }
  public void setPrenomClient(String prenomClient) { this.prenomClient = prenomClient; }

  public String getEmailClient() { return emailClient; }
  public void setEmailClient(String emailClient) { this.emailClient = emailClient; }

  public String getTelephoneClient() { return telephoneClient; }
  public void setTelephoneClient(String telephoneClient) { this.telephoneClient = telephoneClient; }

  public double getMontantTotal() { return montantTotal; }
  public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }

  public ReservationStatus getStatut() { return statut; }
  public void setStatut(ReservationStatus statut) { this.statut = statut; }


  public String getAgenceId() { return agenceId; }
  public void setAgenceId(String agenceId) { this.agenceId = agenceId; }

  public LocalDateTime getDateReservation() { return dateReservation; }
  public void setDateReservation(LocalDateTime dateReservation) { this.dateReservation = dateReservation; }

  public Chambre getChambre() { return chambre; }
  public void setChambre(Chambre chambre) { this.chambre = chambre; }
}
