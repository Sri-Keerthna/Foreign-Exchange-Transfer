package com.spiralforge.forxtransfer.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sri Keerthna.
 * @since 2020-02-11.
 */
@Getter
@Setter
public class AccountResponseDto {

	private Long accountNumber;
	private String accountType;
	private Double balance;
}
