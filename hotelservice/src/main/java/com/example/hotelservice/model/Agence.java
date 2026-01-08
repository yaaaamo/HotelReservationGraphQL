package com.example.hotelservice.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "agencies")
public class Agence {

  @Id
  private String id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private double reductionFactor;  // Ex: 0.90 = 10% de réduction (prix * 0.90)

  public Agence() {}

  public Agence(String id, String name, String password, double reductionFactor) {
    this.id = id;
    this.name = name;
    this.password = password;
    this.reductionFactor = reductionFactor;
  }

  // Vérifie les identifiants
  public boolean validateCredentials(String agenceId, String motDePasse) {
    return this.id.equals(agenceId) && this.password.equals(motDePasse);
  }

  // Calcule le prix avec la réduction
  public double calculerPrix(double prixBase) {
    return prixBase * reductionFactor;
  }


  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public double getReductionFactor() { return reductionFactor; }
  public void setReductionFactor(double reductionFactor) { this.reductionFactor = reductionFactor; }

  @Override
  public String toString() {
    return String.format("Agency{id='%s', name='%s', reduction=%.0f%%}",
            id, name, (1 - reductionFactor) * 100);
  }
}

