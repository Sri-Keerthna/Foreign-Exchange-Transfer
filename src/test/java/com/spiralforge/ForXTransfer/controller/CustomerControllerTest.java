package com.spiralforge.forxtransfer.controller;


import static org.junit.Assert.assertEquals;

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
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.spiralforge.forxtransfer.dto.AccountResponseDto;
import com.spiralforge.forxtransfer.dto.LoginRequestDto;
import com.spiralforge.forxtransfer.dto.LoginResponseDto;
import com.spiralforge.forxtransfer.dto.TransactionResponseDto;
import com.spiralforge.forxtransfer.entity.Account;
import com.spiralforge.forxtransfer.entity.Customer;
import com.spiralforge.forxtransfer.entity.FundTransfer;
import com.spiralforge.forxtransfer.exception.AccountNotFoundException;
import com.spiralforge.forxtransfer.exception.CustomerNotFoundException;
import com.spiralforge.forxtransfer.exception.TransactionsNotFoundException;
import com.spiralforge.forxtransfer.service.CustomerService;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CustomerControllerTest {

	/**
	 * The Constant log.
	 */
	private static final Logger logger = LoggerFactory.getLogger(CustomerControllerTest.class);

	@InjectMocks
	CustomerController customerController;

	@Mock
	CustomerService customerService;

	AccountResponseDto responseDto = new AccountResponseDto();
	List<AccountResponseDto> responseList = new ArrayList<>();
	List<TransactionResponseDto> response = new ArrayList<>();
	LoginRequestDto loginRequestDto = null;
	LoginResponseDto loginResponse = null;
	Customer customer = null;
	
	Account account = new Account();
	List<Account> accountList = new ArrayList<>();
	List<FundTransfer> fundTransferList = new ArrayList<>();
	FundTransfer fundTransfer = new FundTransfer();
	TransactionResponseDto TransactionResponseDto = new TransactionResponseDto();
	List<TransactionResponseDto> resultList = new ArrayList<>();

	@Before
	public void before() {
		loginRequestDto = new LoginRequestDto();
		loginRequestDto.setMobileNumber(9876543210L);
		loginRequestDto.setPassword("muthu123");

		loginResponse = new LoginResponseDto();
		
		customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("Muthu");
		customer.setEmail("muthu@gmail.com");
		customer.setMobileNumber(9876543210L);
		customer.setPassword("muthu123");

		account.setAccountNumber(1234567L);
		account.setAccountType("Savings");
		account.setBalance(20000D);
		account.setCustomer(customer);
		accountList.add(account); 
		
		fundTransfer.setAccount(account);
		fundTransfer.setFundTransferId(1L);
		fundTransfer.setTransferAmount(50000D);
		fundTransfer.setToAccount(3454345L);
		fundTransfer.setTransferStatus("SUCCESS");
		fundTransfer.setTransferDate(LocalDateTime.of(2019, 9, 22, 10, 10, 10));
		fundTransferList.add(fundTransfer);

		BeanUtils.copyProperties(fundTransferList, TransactionResponseDto);
		resultList.add(TransactionResponseDto);
	}

	@Test
	public void testAccountListPositive() throws CustomerNotFoundException, AccountNotFoundException {
		logger.info("Entered into accountList method in Testcontroller");
		Mockito.when(customerService.accountList(1L)).thenReturn(responseList);
		ResponseEntity<List<AccountResponseDto>> responseList = customerController.accountList(1L);
		assertEquals(200, responseList.getStatusCodeValue());
	}

	@Test
	public void testCheckLoginPositive() throws CustomerNotFoundException {
		Mockito.when(customerService.checkLogin(loginRequestDto)).thenReturn(loginResponse);
		ResponseEntity<LoginResponseDto> response = customerController.checkLogin(loginRequestDto);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testTransactionListPositive() throws CustomerNotFoundException, TransactionsNotFoundException, AccountNotFoundException {
		Mockito.when(customerService.transactionList(1L, 9, 2019)).thenReturn(resultList);
		ResponseEntity<List<TransactionResponseDto>> responseList = customerController.transactionList(1L, 9, 2019);
		assertEquals(HttpStatus.OK, responseList.getStatusCode());
	}
	
	@Test(expected = TransactionsNotFoundException.class)
	public void testTransactionListNegative() throws CustomerNotFoundException, TransactionsNotFoundException, AccountNotFoundException {
		List<TransactionResponseDto> response = new ArrayList<>();
		Mockito.when(customerService.transactionList(1L, 9, 2019)).thenReturn(response);
		customerController.transactionList(1L, 9, 2019);
	}
}
