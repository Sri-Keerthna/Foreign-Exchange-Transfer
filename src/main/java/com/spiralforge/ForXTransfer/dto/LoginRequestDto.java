package com.spiralforge.forxtransfer.dto;

import javax.validation.constraints.NotBlank;

import com.spiralforge.forxtransfer.constants.ApplicationConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
	private Long mobileNumber;
	@NotBlank(message = ApplicationConstants.EMPTY_CUSTOMERINPUT_MESSAGE)
	private String password;
}
