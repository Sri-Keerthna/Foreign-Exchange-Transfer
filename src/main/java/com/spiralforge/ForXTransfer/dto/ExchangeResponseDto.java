package com.spiralforge.forxtransfer.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeResponseDto implements Serializable {

	private Long customerId;
	private Double transferAmount;
	private Double charges;
	private Double totalAmount;
	private Double rate;
	private String date;
	private Integer statusCode;
	private String message;
}
