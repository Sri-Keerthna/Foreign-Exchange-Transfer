package com.spiralforge.forxtransfer.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeRequestDto implements Serializable {

	private Double totalAmount;
	private Long fromAccount;
	private Long toAccount;
	private Double amount;
	private Double transferAmount;
	private Double chargeAmount;
	private String transferStatus;
	private String currencyType;
	private LocalDateTime transferDate;
}
