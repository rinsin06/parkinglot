package com.parkinglot.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.repository.ParkingSlotRepository;
import com.parkinglot.service.ParkingSlotService;

class ParkingSlotServiceTest {

    @InjectMocks
    private ParkingSlotService parkingSlotService;

    @Mock
    private ParkingSlotRepository parkingSlotRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindNearestAvailableSlot() {
        ParkingSlot slot = new ParkingSlot(1, true);
        when(parkingSlotRepository.findFirstByIsAvailableTrueOrderBySlotIdAsc()).thenReturn(Optional.of(slot));
        Optional<ParkingSlot> result = parkingSlotService.findNearestAvailableSlot();
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getSlotId());
        verify(parkingSlotRepository, times(1)).findFirstByIsAvailableTrueOrderBySlotIdAsc();
    }

    @Test
    void testFindNearestAvailableSlot_NoSlot() {
        when(parkingSlotRepository.findFirstByIsAvailableTrueOrderBySlotIdAsc()).thenReturn(Optional.empty());
        Optional<ParkingSlot> result = parkingSlotService.findNearestAvailableSlot();
        assertTrue(result.isEmpty());
        verify(parkingSlotRepository, times(1)).findFirstByIsAvailableTrueOrderBySlotIdAsc();
    }

    @Test
    void testSaveSlot() {
        ParkingSlot slot = new ParkingSlot(1, true);
        parkingSlotService.save(slot);
        verify(parkingSlotRepository, times(1)).save(slot);
    }

    @Test
    void testFindById() {
        ParkingSlot slot = new ParkingSlot(1, true);
        when(parkingSlotRepository.findById(1)).thenReturn(Optional.of(slot));
        Optional<ParkingSlot> result = parkingSlotService.findById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getSlotId());
        verify(parkingSlotRepository, times(1)).findById(1);
    }

    @Test
    void testFindById_NotFound() {
        when(parkingSlotRepository.findById(1)).thenReturn(Optional.empty());
        Optional<ParkingSlot> result = parkingSlotService.findById(1);
        assertTrue(result.isEmpty());
        verify(parkingSlotRepository, times(1)).findById(1);
    }

    @Test
    void testCreateParkingLot() {
        int numberOfSlots = 5;
        String result = parkingSlotService.createParkingLot(numberOfSlots);
        assertEquals("Created parking slot with 5slots", result);
        verify(parkingSlotRepository, times(numberOfSlots)).save(any(ParkingSlot.class));
    }
}
