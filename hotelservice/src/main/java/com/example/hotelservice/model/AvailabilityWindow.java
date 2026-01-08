package com.example.hotelservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "availability_windows")
public class AvailabilityWindow {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = false)
  private LocalDate endDate;

  @Column(nullable = false)
  private int quantity;  // Nombre de réservations possibles

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chambre_id", nullable = false)
  private Chambre chambre;

  public AvailabilityWindow() {}

  public AvailabilityWindow(LocalDate startDate, LocalDate endDate, int quantity, Chambre chambre) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.quantity = quantity;
    this.chambre = chambre;
  }


  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public LocalDate getStartDate() { return startDate; }
  public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

  public LocalDate getEndDate() { return endDate; }
  public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

  public int getQuantity() { return quantity; }
  public void setQuantity(int quantity) { this.quantity = quantity; }

  public Chambre getChambre() { return chambre; }
  public void setChambre(Chambre chambre) { this.chambre = chambre; }
}

