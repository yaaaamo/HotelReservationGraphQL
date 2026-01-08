package com.example.hotelservice.repository;

import com.example.hotelservice.model.Agence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgenceRepository extends JpaRepository<Agence, String> {
}
