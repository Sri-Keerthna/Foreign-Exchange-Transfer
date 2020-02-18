package com.spiralforge.forxtransfer.controller;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spiralforge.forxtransfer.constants.ApplicationConstants;
import com.spiralforge.forxtransfer.dto.ExchangeRequestDto;
import com.spiralforge.forxtransfer.dto.ExchangeResponseDto;
import com.spiralforge.forxtransfer.entity.FundTransfer;
import com.spiralforge.forxtransfer.exception.AccountNotFoundException;
import com.spiralforge.forxtransfer.exception.CurrencyNotFoundException;
import com.spiralforge.forxtransfer.exception.CustomerNotFoundException;
import com.spiralforge.forxtransfer.service.TransferService;

@RestController
@RequestMapping("/transfers")
@CrossOrigin(allowedHeaders = { "*", "*/" }, origins = { "*", "*/" })
public class TransferController {

	Logger logger = LoggerFactory.getLogger(TransferController.class);

	@Autowired
	private TransferService transferService;

	/**
	 * @author Sujal
	 * 
	 *         This method is used to see the preview of fund transfer
	 * 
	 * @param customerId
	 * @param base
	 * @param quote
	 * @param amount
	 * @return ExchangeResponseDto
	 * @throws CurrencyNotFoundException
	 */
	@GetMapping("/customers/{customerId}/preview")
	public ResponseEntity<ExchangeResponseDto> previewExchangeAmount(@PathVariable("customerId") Long customerId,
			@RequestParam("base") String base, @RequestParam("quote") String quote,
			@RequestParam("amount") Double amount) throws CurrencyNotFoundException {
		logger.info("For preview exchange amount");
		ExchangeResponseDto exchangeResponseDto = transferService.previewExchangeAmount(customerId, base, quote,
				amount);
		if (Objects.isNull(exchangeResponseDto)) {
			exchangeResponseDto = new ExchangeResponseDto();
			exchangeResponseDto.setMessage(ApplicationConstants.FAILED);
			exchangeResponseDto.setStatusCode(405);
		} else {
			exchangeResponseDto.setMessage(ApplicationConstants.SUCCESS);
			exchangeResponseDto.setStatusCode(205);
		}
		return new ResponseEntity<>(exchangeResponseDto, HttpStatus.OK);
	}

	/**
	 * @author Sujal
	 * 
	 *         This method is used to transfer the fund
	 * 
	 * @param customerId
	 * @param exchangeRequestDto
	 * @return ExchangeResponseDto
	 * @throws AccountNotFoundException
	 * @throws CustomerNotFoundException
	 * @throws com.spiralforge.forxtransfer.exception.AccountNotFoundException
	 */
	@PostMapping("/customers/{customerId}/transfer")
	public ResponseEntity<ExchangeResponseDto> transfer(@PathVariable("customerId") Long customerId,
			@RequestBody ExchangeRequestDto exchangeRequestDto)
			throws CustomerNotFoundException, AccountNotFoundException {
		logger.info("api for transfer amount");
		ExchangeResponseDto exchangeResponseDto = new ExchangeResponseDto();
		FundTransfer fubTransfer = transferService.transfer(customerId, exchangeRequestDto);
		if (Objects.isNull(fubTransfer)) {
			exchangeResponseDto.setMessage(ApplicationConstants.FAILED);
			exchangeResponseDto.setStatusCode(405);
		} else {
			exchangeResponseDto.setTotalAmount(fubTransfer.getTransferAmount());
			exchangeResponseDto.setMessage(ApplicationConstants.SUCCESS);
			exchangeResponseDto.setStatusCode(205);
		}
		return new ResponseEntity<>(exchangeResponseDto, HttpStatus.OK);
	}

}
