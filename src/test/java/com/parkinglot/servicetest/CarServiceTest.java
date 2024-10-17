package com.parkinglot.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.parkinglot.entity.Car;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.repository.CarRepository;
import com.parkinglot.service.CarService;

class CarServiceTest {

    @InjectMocks
    private CarService carService;

    @Mock
    private CarRepository carRepository;

    private Car car;
    private ParkingSlot parkingSlot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        parkingSlot = new ParkingSlot();
        parkingSlot.setSlotId(1);
        parkingSlot.setAvailable(true);

        car = new Car();
        car.setRegestrationNumber("ABC123");
        car.setColor("Red");
        car.setParkingSlot(parkingSlot);
    }

    @Test
    void testSaveCar() {
        carService.save(car);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    void testFindByColor() {
        when(carRepository.findByColor("Red")).thenReturn(List.of(car));
        List<Car> result = carService.findByColor("Red");
        assertEquals(1, result.size());
        assertEquals("ABC123", result.get(0).getRegestrationNumber());
        verify(carRepository, times(1)).findByColor("Red");
    }

    @Test
    void testFindByColor_NoCars() {
        when(carRepository.findByColor("Blue")).thenReturn(List.of());
        List<Car> result = carService.findByColor("Blue");
        assertEquals(0, result.size());
        verify(carRepository, times(1)).findByColor("Blue");
    }

    @Test
    void testFindBySlotId() {
        when(carRepository.findByParkingSlotSlotId(1)).thenReturn(Optional.of(car));
        Optional<Car> result = carService.findBySlotId(1);
        assertTrue(result.isPresent());
        assertEquals("ABC123", result.get().getRegestrationNumber());
        verify(carRepository, times(1)).findByParkingSlotSlotId(1);
    }

    @Test
    void testFindBySlotId_NotFound() {
        when(carRepository.findByParkingSlotSlotId(1)).thenReturn(Optional.empty());
        Optional<Car> result = carService.findBySlotId(1);
        assertTrue(result.isEmpty());
        verify(carRepository, times(1)).findByParkingSlotSlotId(1);
    }

    @Test
    void testDeleteCar() {
        carService.delete(car);
        verify(carRepository, times(1)).delete(car);
    }

    @Test
    void testFindByRegestrationNumber() {
        when(carRepository.findByRegestrationNumber("ABC123")).thenReturn(Optional.of(car));
        Optional<Car> result = carService.findByRegestrationNumber("ABC123");
        assertTrue(result.isPresent());
        assertEquals("Red", result.get().getColor());
        verify(carRepository, times(1)).findByRegestrationNumber("ABC123");
    }

    @Test
    void testFindByRegestrationNumber_NotFound() {
        when(carRepository.findByRegestrationNumber("XYZ987")).thenReturn(Optional.empty());
        Optional<Car> result = carService.findByRegestrationNumber("XYZ987");
        assertTrue(result.isEmpty());
        verify(carRepository, times(1)).findByRegestrationNumber("XYZ987");
    }
}
