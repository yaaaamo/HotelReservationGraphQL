package com.example.hotelservice.repository;

import com.example.hotelservice.model.Chambre;
import com.example.hotelservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  @Query("""
    select count(r) from Reservation r
    where r.chambre = :chambre
      and r.statut = com.example.hotelservice.model.ReservationStatus.CONFIRMED
      and r.dateArrivee < :endDate
      and r.dateDepart > :startDate
  """)
  long countOverlappingReservations(
          @Param("chambre") Chambre chambre,
          @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate
  );

  interface ChambreReservationCount {
    Long getChambreId();
    Long getCnt();
  }

  @Query("""
    select r.chambre.id as chambreId, count(r) as cnt
    from Reservation r
    where r.chambre.id in :chambreIds
      and r.statut = com.example.hotelservice.model.ReservationStatus.CONFIRMED
      and r.dateArrivee < :endDate
      and r.dateDepart > :startDate
    group by r.chambre.id
  """)
  List<ChambreReservationCount> countOverlappingGrouped(
          @Param("chambreIds") List<Long> chambreIds,
          @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate
  );
}
