package com.parkinglot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parkinglot.entity.ParkingSlot;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Integer> {
	
	Optional<ParkingSlot> findFirstByIsAvailableTrueOrderBySlotIdAsc();

}
