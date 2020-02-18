package com.spiralforge.forxtransfer.service;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.spiralforge.forxtransfer.entity.Account;
import com.spiralforge.forxtransfer.entity.Customer;
import com.spiralforge.forxtransfer.exception.AccountNotFoundException;
import com.spiralforge.forxtransfer.exception.CustomerNotFoundException;
import com.spiralforge.forxtransfer.exception.TransactionsNotFoundException;
import com.spiralforge.forxtransfer.dto.AccountResponseDto;
import com.spiralforge.forxtransfer.dto.LoginRequestDto;
import com.spiralforge.forxtransfer.dto.LoginResponseDto;
import com.spiralforge.forxtransfer.dto.TransactionResponseDto;

public interface CustomerService {

	LoginResponseDto checkLogin(@Valid LoginRequestDto loginRequestDto) throws CustomerNotFoundException;

	List<AccountResponseDto> accountList(Long customerId) throws CustomerNotFoundException, AccountNotFoundException;

	List<TransactionResponseDto> transactionList(Long customerId, Integer month, Integer year) throws CustomerNotFoundException, TransactionsNotFoundException, AccountNotFoundException;

	Optional<Customer> getCustomerByCustomerId(Long customerId);

	Optional<Account> getAccountByAccountNumber(Long fromAccount);

}
