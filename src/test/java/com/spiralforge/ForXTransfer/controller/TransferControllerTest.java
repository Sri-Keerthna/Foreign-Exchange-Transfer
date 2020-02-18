package com.spiralforge.forxtransfer.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spiralforge.forxtransfer.constants.ApplicationConstants;
import com.spiralforge.forxtransfer.dto.AccountResponseDto;
import com.spiralforge.forxtransfer.dto.ExchangeRequestDto;
import com.spiralforge.forxtransfer.dto.ExchangeResponseDto;
import com.spiralforge.forxtransfer.entity.Account;
import com.spiralforge.forxtransfer.entity.Customer;
import com.spiralforge.forxtransfer.entity.FundTransfer;
import com.spiralforge.forxtransfer.exception.AccountNotFoundException;
import com.spiralforge.forxtransfer.exception.CurrencyNotFoundException;
import com.spiralforge.forxtransfer.exception.CustomerNotFoundException;
import com.spiralforge.forxtransfer.service.CustomerService;
import com.spiralforge.forxtransfer.service.TransferService;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TransferControllerTest {

	/**
	 * The Constant log.
	 */
	private static final Logger logger = LoggerFactory.getLogger(TransferControllerTest.class);

	@InjectMocks
	private TransferController transferController;

	@Mock
	private CustomerService customerService;
	
	@Mock
	private TransferService transferService;

	AccountResponseDto responseDto = new AccountResponseDto();
	List<AccountResponseDto> responseList = new ArrayList<>();

	ExchangeRequestDto exchangeRequestDto = null;
	ExchangeResponseDto exchangeResponseDto = null;
	FundTransfer fundTransfer = null;
	Customer customer = null;
	Account account=null;

	@Before
	public void before() {
		exchangeRequestDto = new ExchangeRequestDto();
		exchangeRequestDto.setAmount(200D);
		exchangeRequestDto.setCurrencyType("AUD");
		
		exchangeResponseDto=new ExchangeResponseDto();
		exchangeResponseDto.setStatusCode(205);
		
		fundTransfer = new FundTransfer();
		customer = new Customer();
		account = new Account();
		customer.setCustomerId(1L);
		account.setAccountNumber(1L);
		account.setCustomer(customer);
		account.setBalance(1000.00);

		fundTransfer.setAccount(account);
		fundTransfer.setChargeAmount(100.00);
		fundTransfer.setTransferAmount(150.00);
		fundTransfer.setToAccount(2L);
		fundTransfer.setTransferDate(LocalDateTime.now());
		fundTransfer.setTransferStatus(ApplicationConstants.STATUS_PENDING_MESSAGE);
	}

	@Test
	public void testPreviewExchangeAmountPositive() throws CustomerNotFoundException, AccountNotFoundException, CurrencyNotFoundException {
		logger.info("Entered into accountList method in Testcontroller");
		Mockito.when(transferService.previewExchangeAmount(1L, "USD", "AUD", 200D)).thenReturn(exchangeResponseDto);
		ExchangeResponseDto response = transferController.previewExchangeAmount(1L, "USD", "AUD", 200D).getBody();
		assertEquals(205, response.getStatusCode());
	}
	
	@Test
	public void testPreviewExchangeAmountNegative() throws CustomerNotFoundException, AccountNotFoundException, CurrencyNotFoundException {
		logger.info("Entered into accountList method in Testcontroller");
		Mockito.when(transferService.previewExchangeAmount(1L, "USD", "AUD", 200D)).thenReturn(null);
		ExchangeResponseDto response = transferController.previewExchangeAmount(1L, "USD", "AUD", 200D).getBody();
		assertEquals(405, response.getStatusCode());
	}

	@Test
	public void testTransferAmountPositive() throws CustomerNotFoundException, AccountNotFoundException  {
		logger.info("Entered into accountList method in Testcontroller");
		Mockito.when(transferService.transfer(1L, exchangeRequestDto)).thenReturn(fundTransfer);
		ExchangeResponseDto response = transferController.transfer(1L, exchangeRequestDto).getBody();
		assertEquals(205, response.getStatusCode());
	}
	
	@Test
	public void testTransferAmountNegative() throws CustomerNotFoundException, AccountNotFoundException  {
		logger.info("Entered into accountList method in Testcontroller");
		Mockito.when(transferService.transfer(1L, exchangeRequestDto)).thenReturn(null);
		ExchangeResponseDto response = transferController.transfer(1L, exchangeRequestDto).getBody();
		assertEquals(405, response.getStatusCode());
	}

}
