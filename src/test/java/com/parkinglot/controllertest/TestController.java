package com.parkinglot.controllertest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.parkinglot.controller.ParkingController;
import com.parkinglot.dto.CarRequest;
import com.parkinglot.entity.Car;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.service.CarService;
import com.parkinglot.service.ParkingSlotService;

class ParkingControllerTest {

    @InjectMocks
    private ParkingController parkingController;

    @Mock
    private CarService carService;

    @Mock
    private ParkingSlotService parkingSlotService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateParkingLot() {
        when(parkingSlotService.createParkingLot(anyInt())).thenReturn("Parking lot created");

        ResponseEntity<?> response = parkingController.createParkingLot(Map.of("numberOfSlots", 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Parking lot created", ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void testParkCar_success() {
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotId(1);
        slot.setAvailable(true);

        when(parkingSlotService.findNearestAvailableSlot()).thenReturn(Optional.of(slot));

        CarRequest carRequest = new CarRequest();
        carRequest.setRegestrationNumber("ABC123");
        carRequest.setColor("Red");

        ResponseEntity<String> response = parkingController.parkCar(carRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Allocated Slot Number : 1", response.getBody());

        verify(carService).save(any(Car.class));
        verify(parkingSlotService).save(slot);
    }

    @Test
    void testParkCar_fullLot() {
        when(parkingSlotService.findNearestAvailableSlot()).thenReturn(Optional.empty());

        CarRequest carRequest = new CarRequest();
        carRequest.setRegestrationNumber("ABC123");
        carRequest.setColor("Red");

        ResponseEntity<String> response = parkingController.parkCar(carRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Parking lot is full", response.getBody());
    }

    @Test
    void testLeaveSlot_success() {
        Car car = new Car();
        car.setRegestrationNumber("ABC123");
        car.setParkingSlot(new ParkingSlot(1, true));

        when(carService.findBySlotId(1)).thenReturn(Optional.of(car));
        when(parkingSlotService.findById(1)).thenReturn(Optional.of(new ParkingSlot(1, false)));

        ResponseEntity<String> response = parkingController.leaveSlot(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Slot number : 1is free", response.getBody());

        verify(carService).delete(car);
        verify(parkingSlotService).save(any(ParkingSlot.class));
    }

    @Test
    void testLeaveSlot_slotAlreadyFree() {
        when(carService.findBySlotId(1)).thenReturn(Optional.empty());

        ResponseEntity<String> response = parkingController.leaveSlot(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Slot is already free", response.getBody());
    }

    @Test
    void testFindCarsByColor_found() {
        when(carService.findByColor("Red")).thenReturn(Arrays.asList(
            new Car("ABC123", "Red", new ParkingSlot(1, false)),
            new Car("XYZ789", "Red", new ParkingSlot(2, false))
        ));

        ResponseEntity<List<String>> response = parkingController.findCarsByColor("Red");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Arrays.asList("ABC123", "XYZ789"), response.getBody());
    }

    @Test
    void testFindCarsByColor_notFound() {
        when(carService.findByColor("Blue")).thenReturn(List.of());

        ResponseEntity<List<String>> response = parkingController.findCarsByColor("Blue");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testFindSlotByRegestrationNumber_found() {
        Car car = new Car("ABC123", "Red", new ParkingSlot(1, false));
        when(carService.findByRegestrationNumber("ABC123")).thenReturn(Optional.of(car));

        ResponseEntity<Integer> response = parkingController.findSlotByRegestrationNumber("ABC123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody());
    }

    @Test
    void testFindSlotByRegestrationNumber_notFound() {
        when(carService.findByRegestrationNumber("XYZ789")).thenReturn(Optional.empty());

        ResponseEntity<Integer> response = parkingController.findSlotByRegestrationNumber("XYZ789");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testFindSlotsByColor_found() {
        when(carService.findByColor("Red")).thenReturn(Arrays.asList(
            new Car("ABC123", "Red", new ParkingSlot(1, false)),
            new Car("XYZ789", "Red", new ParkingSlot(2, false))
        ));

        ResponseEntity<List<Integer>> response = parkingController.findSlotsByColor("Red");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Arrays.asList(1, 2), response.getBody());
    }

    @Test
    void testFindSlotsByColor_notFound() {
        when(carService.findByColor("Blue")).thenReturn(List.of());

        ResponseEntity<List<Integer>> response = parkingController.findSlotsByColor("Blue");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }
}

