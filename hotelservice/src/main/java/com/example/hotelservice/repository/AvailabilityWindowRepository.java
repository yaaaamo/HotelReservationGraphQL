package com.example.hotelservice.repository;

import com.example.hotelservice.model.AvailabilityWindow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityWindowRepository extends JpaRepository<AvailabilityWindow, Long> {

  List<AvailabilityWindow> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
          LocalDate endDate,
          LocalDate startDate
  );
}
