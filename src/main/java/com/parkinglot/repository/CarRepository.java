package com.parkinglot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parkinglot.entity.Car;

public interface CarRepository extends JpaRepository<Car, String> {
	
	List<Car> findByColor(String color);
	
	Optional<Car> findByRegestrationNumber(String regestrationNumber);

	Optional<Car> findByParkingSlotSlotId(int slotId);

}
