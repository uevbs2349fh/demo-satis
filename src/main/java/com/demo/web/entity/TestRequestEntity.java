package com.demo.web.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "test_request")
@Getter
@Setter
public class TestRequestEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ticket_number")
	private int ticketNumber;
	@Column(name = "fault_device")
	private String faultDevice;
	@Column(name = "contranct_type")
	private String contractType;
}
