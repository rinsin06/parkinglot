package com.parkinglot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "car")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {

	@Id
	@Column(name = "regestration_number", nullable = false , unique = true)
	
	private String regestrationNumber;
	
	@Column(nullable  = false)
	private String color;
	
	@OneToOne
	@JoinColumn(name = "slot_id", referencedColumnName = "slotId")
	private ParkingSlot parkingSlot;
	
}
