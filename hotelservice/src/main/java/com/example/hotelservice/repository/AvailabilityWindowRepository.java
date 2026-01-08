package com.example.hotelservice.repository;

import com.example.hotelservice.model.AvailabilityWindow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityWindowRepository extends JpaRepository<AvailabilityWindow, Long> {

  @Query("""
  select aw from AvailabilityWindow aw
  join fetch aw.chambre c
  where aw.startDate <= :endDate and aw.endDate >= :startDate
""")
  List<AvailabilityWindow> findWindowsWithChambre(
          @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate
  );

}
