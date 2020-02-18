package com.spiralforge.forxtransfer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
	private String message;
	private Long customerId;
	private String customerName;
}
