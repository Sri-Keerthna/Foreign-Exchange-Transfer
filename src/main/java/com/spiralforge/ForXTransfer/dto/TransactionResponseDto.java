package com.spiralforge.forxtransfer.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionResponseDto {

	private Long fundTransferId;
	private Long fromAccount;
	private Long toAccount;
	private Double transferAmount;
	private String transferStatus;
	private LocalDateTime transferDate;
}
