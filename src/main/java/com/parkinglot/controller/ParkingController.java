package com.parkinglot.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parkinglot.dto.CarRequest;
import com.parkinglot.entity.Car;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.service.CarService;
import com.parkinglot.service.ParkingSlotService;

@RestController
@RequestMapping("/api")
public class ParkingController {
	
	
	@Autowired
	private CarService carService;
	
	@Autowired
	private ParkingSlotService parkingSlotService;
	
	@PostMapping("/createParkingLot")
	public ResponseEntity<?> createParkingLot(@RequestBody Map<String, Integer> payLoad){
		
		int numberOfSlots = payLoad.get("numberOfSlots");
		
		String message = parkingSlotService.createParkingLot(numberOfSlots);
		
		return ResponseEntity.ok().body(Collections.singletonMap("message", message));
	}
	
	
	
	@PostMapping("/park")
	public ResponseEntity<String> parkCar(@RequestBody CarRequest carRequest){
		
		Optional<ParkingSlot> nearestSlot = parkingSlotService.findNearestAvailableSlot();
		
		if(nearestSlot.isPresent()) {
			
			ParkingSlot slot = nearestSlot.get();
			
			Car car = new Car(carRequest.getRegestrationNumber(), carRequest.getColor(),slot );
			
			carService.save(car);
			
			slot.setAvailable(false);
			
			parkingSlotService.save(slot);
			
			return ResponseEntity.ok("Allocated Slot Number : " + slot.getSlotId());
	
		}
		
		return ResponseEntity.status(500).body("Parking lot is full");
	}
	
	
	@PostMapping("/leave/{slotId}")
	public ResponseEntity<String> leaveSlot(@PathVariable int slotId){
		
		Optional<Car> car = carService.findBySlotId(slotId);
		
		if(car.isPresent()) {
			
			carService.delete(car.get());
			
			ParkingSlot slot = parkingSlotService.findById(slotId).get();
			
			slot.setAvailable(true);
			
			parkingSlotService.save(slot);
			
			return ResponseEntity.ok("Slot number : " + slotId + "is free");
		}
		
		return ResponseEntity.status(404).body("Slot is already free");
	}
	
	@GetMapping("/cars/color/{color}")
	ResponseEntity<List<String>> findCarsByColor(@PathVariable String color){
		
		List<String> regestrationNumbers = carService.findByColor(color).stream().map(Car::
			getRegestrationNumber).toList();
		
		if(!regestrationNumbers.isEmpty()) {
			
			return ResponseEntity.ok(regestrationNumbers);
		}
			
		return ResponseEntity.status(404).body(List.of());
				
	}
	
	@GetMapping("/slot/{regestrationNumber}")
	public ResponseEntity<Integer> findSlotByRegestrationNumber(@PathVariable String regestrationNumber){
		
		Optional<Car> car = carService.findByRegestrationNumber(regestrationNumber);
		
		if(car.isPresent()) {
			
			return ResponseEntity.ok(car.get().getParkingSlot().getSlotId());
		}
		
		return ResponseEntity.status(404).body(null);
	}
	
	@GetMapping("/slots/color/{color}")
	public ResponseEntity<List<Integer>> findSlotsByColor(@PathVariable String color){
		
		List<Integer> slotIds = carService.findByColor(color).stream().map(car ->
		
				car.getParkingSlot().getSlotId()).toList();
		
		if(!slotIds.isEmpty()) {
			
			return ResponseEntity.ok(slotIds);
		}
		
		return ResponseEntity.status(404).body(List.of());
				
	}
}
