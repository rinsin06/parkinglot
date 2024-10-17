package com.parkinglot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parkinglot.entity.Car;
import com.parkinglot.repository.CarRepository;

@Service
public class CarService {

	@Autowired
	private CarRepository carRepository;
	
	
	public void save(Car car) {
		
		carRepository.save(car);
	}
	
	public List<Car> findByColor(String color){
		
		return carRepository.findByColor(color);
	}
	
	public Optional<Car>findBySlotId(int slotId){
		
		return carRepository.findByParkingSlotSlotId(slotId);
	}
	
	public void delete(Car car) {
		carRepository.delete(car);
	}
	
	public Optional<Car> findByRegestrationNumber(String regestrationNumber){
		
		return carRepository.findByRegestrationNumber(regestrationNumber);
	}
}
