package com.parkinglot.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.repository.ParkingSlotRepository;

@Service
public class ParkingSlotService {

	@Autowired
	private ParkingSlotRepository parkingSlotRepository;
	
	
	public Optional<ParkingSlot> findNearestAvailableSlot(){
		
		return parkingSlotRepository.findFirstByIsAvailableTrueOrderBySlotIdAsc();
	}
	
	public void save(ParkingSlot slot) {
		
		parkingSlotRepository.save(slot);
	}
	
	public Optional<ParkingSlot> findById(Integer id){
		
		return parkingSlotRepository.findById(id);
	}
	
	public String createParkingLot(int numberofSlots) {
		
		for(int i = 1 ; i <= numberofSlots ; i++) {
			
			ParkingSlot slot = new ParkingSlot(i,true);
			
			parkingSlotRepository.save(slot);
			
			
		}
		
		return "Created parking slot with " + numberofSlots +"slots";
		
	}
	
	
}
