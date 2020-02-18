package com.spiralforge.forxtransfer.service;

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

import com.spiralforge.forxtransfer.constants.ApplicationConstants;
import com.spiralforge.forxtransfer.entity.Account;
import com.spiralforge.forxtransfer.entity.Customer;
import com.spiralforge.forxtransfer.entity.FundTransfer;
import com.spiralforge.forxtransfer.repository.AccountRepository;
import com.spiralforge.forxtransfer.repository.FundTransferRepository;
import com.spiralforge.forxtransfer.repository.TransactionRepository;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TransactionServiceTest {
	@InjectMocks
	TransactionServiceImpl transactionServiceImpl;

	@Mock
	TransactionRepository transactionRepository;

	@Mock
	AccountRepository accountRepository;

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

	@Before
	public void before() {
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
	public void testScheduleTransactionStatusException() {
		Mockito.when(fundTransferRepository.findAllByTransferStatus(ApplicationConstants.STATUS_PENDING_MESSAGE))
				.thenReturn(fundTransferList1);
		transactionServiceImpl.scheduleTransactionStatus();
	}

	@Test
	public void testScheduleTransactionStatusSuccess() {
		Mockito.when(fundTransferRepository.findAllByTransferStatus(ApplicationConstants.STATUS_PENDING_MESSAGE))
				.thenReturn(fundTransferList);
		Mockito.when(accountRepository.findByAccountNumber(fundTransfer.getToAccount())).thenReturn(account1);
		Long fromAccount = fundTransfer.getAccount().getAccountNumber();
		Mockito.when(accountRepository.findByAccountNumber(fromAccount)).thenReturn(account);
		transactionServiceImpl.scheduleTransactionStatus();
	}

	@Test
	public void testScheduleTransactionStatusSameAccount() {
		Mockito.when(fundTransferRepository.findAllByTransferStatus(ApplicationConstants.STATUS_PENDING_MESSAGE))
				.thenReturn(fundTransferList2);
		Mockito.when(accountRepository.findByAccountNumber(fundTransfer.getToAccount())).thenReturn(account1);
		Long fromAccount = fundTransfer.getAccount().getAccountNumber();
		Mockito.when(accountRepository.findByAccountNumber(fromAccount)).thenReturn(account);
		transactionServiceImpl.scheduleTransactionStatus();
	}

	@Test
	public void testScheduleTransactionStatusAmountInSufficient() {
		Mockito.when(fundTransferRepository.findAllByTransferStatus(ApplicationConstants.STATUS_PENDING_MESSAGE))
				.thenReturn(fundTransferList2);
		Mockito.when(accountRepository.findByAccountNumber(fundTransfer.getToAccount())).thenReturn(account1);
		Long fromAccount = fundTransfer.getAccount().getAccountNumber();
		Mockito.when(accountRepository.findByAccountNumber(fromAccount)).thenReturn(account);
		transactionServiceImpl.scheduleTransactionStatus();
	}
}
