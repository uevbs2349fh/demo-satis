package com.demo.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnowApiRequestDto {
	
	@JsonProperty("u_ticket_number")
	private int ticketNumber;
	@JsonProperty("u_fault_device")
	private String faultDevice;
	@JsonProperty("u_contract_type")
	private String contractType;
	@JsonProperty("u_test_result")
	private String result;

}
