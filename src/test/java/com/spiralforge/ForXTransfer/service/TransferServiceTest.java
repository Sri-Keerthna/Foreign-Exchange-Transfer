package com.spiralforge.forxtransfer.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.spiralforge.forxtransfer.constants.ApplicationConstants;
import com.spiralforge.forxtransfer.dto.ExchangeRequestDto;
import com.spiralforge.forxtransfer.dto.ExchangeResponseDto;
import com.spiralforge.forxtransfer.dto.XchangeDto;
import com.spiralforge.forxtransfer.entity.Account;
import com.spiralforge.forxtransfer.entity.Customer;
import com.spiralforge.forxtransfer.entity.FundTransfer;
import com.spiralforge.forxtransfer.exception.AccountNotFoundException;
import com.spiralforge.forxtransfer.exception.CurrencyNotFoundException;
import com.spiralforge.forxtransfer.exception.CustomerNotFoundException;
import com.spiralforge.forxtransfer.repository.AccountRepository;
import com.spiralforge.forxtransfer.repository.FundTransferRepository;
import com.spiralforge.forxtransfer.repository.TransactionRepository;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TransferServiceTest {

	@InjectMocks
	private TransferServiceImpl transferServiceImpl;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	AccountRepository accountRepository;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private CustomerService customerService;

	@Mock
	FundTransferRepository fundTransferRepository;

	FundTransfer fundTransfer = null;
	List<FundTransfer> fundTransferList = null;
	Account account = null;
	Account account1 = null;
	Customer customer = null;
	List<FundTransfer> fundTransferList1 = null;

	FundTransfer fundTransfer1 = null;
	List<FundTransfer> fundTransferList2 = null;

	FundTransfer fundTransfer2 = null;
	List<FundTransfer> fundTransferList3 = null;

	ExchangeRequestDto exchangeRequestDto = null;
	ExchangeResponseDto exchangeResponseDto = null;

	static ResponseEntity responseEntity = Mockito.mock(ResponseEntity.class);

	@Before
	public void before() {

		exchangeRequestDto = new ExchangeRequestDto();
		exchangeRequestDto.setAmount(200D);
		exchangeRequestDto.setCurrencyType("AUD");
		exchangeRequestDto.setTransferAmount(200D);
		exchangeRequestDto.setFromAccount(1L);
		exchangeRequestDto.setToAccount(1L);
		exchangeRequestDto.setTransferAmount(766D);
		exchangeRequestDto.setChargeAmount(6765D);
		exchangeRequestDto.setTotalAmount(7686D);
		exchangeRequestDto.setTransferDate(LocalDateTime.now());
		exchangeRequestDto.setTransferStatus(ApplicationConstants.STATUS_PENDING_MESSAGE);

		exchangeResponseDto = new ExchangeResponseDto();
		exchangeResponseDto.setStatusCode(205);

		fundTransfer = new FundTransfer();
		fundTransferList = new ArrayList<>();
		customer = new Customer();
		account = new Account();
		account1 = new Account();
		customer.setCustomerId(1L);
		account.setAccountNumber(1L);
		account.setCustomer(customer);
		account.setBalance(1000.00);
		account1.setAccountNumber(2L);
		account1.setCustomer(customer);
		account1.setBalance(100.00);
		fundTransfer.setAccount(account);
		fundTransfer.setChargeAmount(100.00);
		fundTransfer.setTransferAmount(150.00);
		fundTransfer.setToAccount(2L);
		fundTransfer.setTransferDate(LocalDateTime.now());
		fundTransfer.setTransferStatus(ApplicationConstants.STATUS_PENDING_MESSAGE);
		fundTransferList.add(fundTransfer);

		fundTransferList1 = new ArrayList<>();

		fundTransfer1 = new FundTransfer();
		fundTransferList2 = new ArrayList<>();
		fundTransfer1.setAccount(account1);
		fundTransfer1.setChargeAmount(100.00);
		fundTransfer1.setTransferAmount(150.00);
		fundTransfer1.setToAccount(2L);
		fundTransfer1.setTransferDate(LocalDateTime.now());
		fundTransfer1.setTransferStatus(ApplicationConstants.STATUS_PENDING_MESSAGE);
		fundTransferList2.add(fundTransfer1);

		fundTransfer2 = new FundTransfer();
		fundTransferList3 = new ArrayList<>();
		fundTransfer2.setAccount(account);
		fundTransfer2.setChargeAmount(100.00);
		fundTransfer2.setTransferAmount(15000.00);
		fundTransfer2.setToAccount(2L);
		fundTransfer2.setTransferDate(LocalDateTime.now());
		fundTransfer2.setTransferStatus(ApplicationConstants.STATUS_PENDING_MESSAGE);
		fundTransferList3.add(fundTransfer2);

	}

	@Test
	public void testPreviewExchangeAmountPositive() throws CurrencyNotFoundException {
		String base = "USD";
		Long customerId = 1L;
		String quote = "AUD";
		Double amount = 300D;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		String url = "https://api.exchangeratesapi.io/latest?base=" + base;

		XchangeDto xchangeDto = new XchangeDto();
		xchangeDto.setBase(base);
		xchangeDto.setDate("");
		HashMap<String, Double> map = new HashMap<>();
		map.put("AUD", 30.00);
		xchangeDto.setRates(map);

		Mockito.when(restTemplate.exchange(url, HttpMethod.GET, entity, XchangeDto.class))
				.thenReturn(new ResponseEntity<XchangeDto>(xchangeDto, HttpStatus.OK));

		ExchangeResponseDto exchangeResponseDto = transferServiceImpl.previewExchangeAmount(customerId, base, quote,
				amount);
		assertNotNull(exchangeResponseDto);

	}

	@Test
	public void testPreviewExchangeAmountNegative() throws CurrencyNotFoundException {
		String base = "USD";
		Long customerId = 1L;
		String quote = "AUD";
		Double amount = 300D;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		String url = "https://api.exchangeratesapi.io/latest?base=" + base;

		XchangeDto xchangeDto = new XchangeDto();

		Mockito.when(restTemplate.exchange(url, HttpMethod.GET, entity, XchangeDto.class)).thenReturn(responseEntity);

		ExchangeResponseDto exchangeResponseDto = transferServiceImpl.previewExchangeAmount(customerId, base, quote,
				amount);
		assertNull(exchangeResponseDto);

	}

	@Test
	public void testTransferAmountPositive() throws CustomerNotFoundException, AccountNotFoundException {
		Long customerId = 1L;
		Long fromAccount = 1L;
		FundTransfer fundTransfer1 = new FundTransfer();
		fundTransfer1.setAmount(200D);
		fundTransfer1.setAccount(account);
		fundTransfer1.setChargeAmount(100.00);
		fundTransfer1.setTransferAmount(150.00);
		fundTransfer1.setToAccount(2L);
		fundTransfer1.setTransferDate(LocalDateTime.now());
		fundTransfer1.setTransferStatus(ApplicationConstants.STATUS_PENDING_MESSAGE);
		Mockito.when(customerService.getCustomerByCustomerId(customerId)).thenReturn(Optional.of(customer));
		Mockito.when(customerService.getAccountByAccountNumber(fromAccount)).thenReturn(Optional.of(account));
		Mockito.when(fundTransferRepository.save(fundTransfer)).thenReturn(fundTransfer1);

		FundTransfer fundTransfer = transferServiceImpl.transfer(customerId, exchangeRequestDto);

		assertEquals(200D, fundTransfer1.getAmount());
	}

	@Test(expected = AccountNotFoundException.class)
	public void testTransferAmountNegative() throws CustomerNotFoundException, AccountNotFoundException {
		Long customerId = 1L;
		Long fromAccount = 1L;
		Mockito.when(customerService.getCustomerByCustomerId(customerId)).thenReturn(Optional.of(customer));
		Mockito.when(customerService.getAccountByAccountNumber(fromAccount)).thenReturn(Optional.ofNullable(null));
		FundTransfer fundTransfer = transferServiceImpl.transfer(customerId, exchangeRequestDto);

	}
	
	@Test(expected = CustomerNotFoundException.class)
	public void testTransferAmountException() throws CustomerNotFoundException, AccountNotFoundException {
		Long customerId = 1L;
		Long fromAccount = 1L;
		Mockito.when(customerService.getCustomerByCustomerId(customerId)).thenReturn(Optional.ofNullable(null));
		FundTransfer fundTransfer = transferServiceImpl.transfer(customerId, exchangeRequestDto);

	}
}
