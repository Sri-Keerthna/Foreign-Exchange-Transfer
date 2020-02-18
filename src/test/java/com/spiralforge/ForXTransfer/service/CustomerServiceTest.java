package com.spiralforge.forxtransfer.service;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.spiralforge.forxtransfer.constants.ApplicationConstants;
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
import com.spiralforge.forxtransfer.repository.AccountRepository;
import com.spiralforge.forxtransfer.repository.CustomerRepository;
import com.spiralforge.forxtransfer.repository.FundTransferRepository;
import com.spiralforge.forxtransfer.utils.Converter;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CustomerServiceTest {

	/**
	 * The Constant log.
	 */
	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceTest.class);

	@InjectMocks
	CustomerServiceImpl customerServiceImpl;

	@Mock
	CustomerRepository customerRepository;

	@Mock
	AccountRepository accountRepository;

	@Mock
	FundTransferRepository fundTransferRepository;

	LoginRequestDto loginRequestDto = null;
	Customer customer = null;

	Account account = new Account();
	List<Account> accountList = new ArrayList<>();
	AccountResponseDto responseDto = new AccountResponseDto();
	List<AccountResponseDto> responseList = new ArrayList<>();
	List<LocalDateTime> dates = Converter.getDateFromMonthAndYear(9, 2019);
	LocalDateTime fromDate = dates.get(0);
	LocalDateTime toDate = dates.get(1);
	List<FundTransfer> fundTransferList = new ArrayList<>();
	FundTransfer fundTransfer = new FundTransfer();
	TransactionResponseDto TransactionResponseDto = new TransactionResponseDto();
	List<TransactionResponseDto> resultList = new ArrayList<>();

	@Before
	public void before() {
		loginRequestDto = new LoginRequestDto();
		loginRequestDto.setMobileNumber(9876543210L);
		loginRequestDto.setPassword("muthu123");

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

		BeanUtils.copyProperties(accountList, responseDto);
		responseList.add(responseDto);

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
	public void testCheckLoginPositive() throws CustomerNotFoundException {
		Long mobileNumber = loginRequestDto.getMobileNumber();
		String password = loginRequestDto.getPassword();
		Mockito.when(customerRepository.findByMobileNumberAndPassword(mobileNumber, password)).thenReturn(customer);
		LoginResponseDto response = customerServiceImpl.checkLogin(loginRequestDto);
		assertEquals(customer.getCustomerName(), response.getCustomerName());
	}

	@Test(expected = CustomerNotFoundException.class)
	public void testCheckLoginException() throws CustomerNotFoundException {
		Mockito.when(customerRepository.findByMobileNumberAndPassword(98765L, "muthu")).thenReturn(customer);
		customerServiceImpl.checkLogin(loginRequestDto);
	}

	@Test
	public void testAccountListPositive() throws CustomerNotFoundException, AccountNotFoundException {
		Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findAccountByCustomer(Optional.of(customer))).thenReturn(accountList);
		logger.info("Got the account details");
		List<AccountResponseDto> responseList = customerServiceImpl.accountList(1L);
		assertEquals(1, responseList.size());
	}

	@Test(expected = CustomerNotFoundException.class)
	public void testAccountListNegative() throws CustomerNotFoundException, AccountNotFoundException {
		Mockito.when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
		logger.error("customer not found exception occurred");
		customerServiceImpl.accountList(1L);
	}

	@Test(expected = AccountNotFoundException.class)
	public void testAccountListNegativeException() throws CustomerNotFoundException, AccountNotFoundException {
		List<Account> accountLists = new ArrayList<>();
		Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findAccountByCustomer(Optional.of(customer))).thenReturn(accountLists);
		logger.error("Account not found");
		customerServiceImpl.accountList(1L);
	}

	@Test
	public void testTransactionListPositive() throws CustomerNotFoundException, TransactionsNotFoundException, AccountNotFoundException{
		Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findAccountByCustomer(Optional.of(customer))).thenReturn(accountList);
		Mockito.when(fundTransferRepository.findByTransferDate(account.getAccountNumber(), fromDate, toDate,
				ApplicationConstants.PENDING)).thenReturn(fundTransferList);
		logger.info("Got the list of transaction taken place on that particular month and year");
		List<TransactionResponseDto> responseList = customerServiceImpl.transactionList(1L, 9, 2019);
		assertEquals(1, responseList.size());
	}
	
	@Test(expected = CustomerNotFoundException.class)
	public void testTransactionListNegative() throws CustomerNotFoundException, TransactionsNotFoundException, AccountNotFoundException {
		Mockito.when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
		customerServiceImpl.transactionList(1L, 9, 2019);
	}
	
	@Test(expected = AccountNotFoundException.class)
	public void testTransactionListNegativeException() throws CustomerNotFoundException, TransactionsNotFoundException, AccountNotFoundException{
		List<Account> accountLists = new ArrayList<>();
		Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findAccountByCustomer(Optional.of(customer))).thenReturn(accountLists);
		customerServiceImpl.transactionList(1L, 9, 2019);
	}
}
