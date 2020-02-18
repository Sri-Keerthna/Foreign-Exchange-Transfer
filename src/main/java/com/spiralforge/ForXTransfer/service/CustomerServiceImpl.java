package com.spiralforge.forxtransfer.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.spiralforge.forxtransfer.repository.TransactionRepository;
import com.spiralforge.forxtransfer.utils.Converter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sri Keerthna.
 * @author Muthu.
 * @author Sujal.
 * @since 2020-02-11.
 */
@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

	/**
	 * The Constant log.
	 */
	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

	@Autowired
	CustomerRepository customerReopsitory;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	FundTransferRepository fundTransferRepository;

	/**
	 * @author Muthu
	 * 
	 *         Method is used to check whether he/she is valid customer or not
	 * 
	 * 
	 * @param loginRequestDto which takes the input parameter as mobile number and
	 *                        password
	 * @return LoginResponseDto which returns the customer id and his/her name
	 * @throws CustomerNotFoundException thrown when the customer credentials are
	 *                                   invalid
	 */
	@Override
	public LoginResponseDto checkLogin(@Valid LoginRequestDto loginRequestDto) throws CustomerNotFoundException {
		log.info("For checking whether the credentials are valid or not");
		Customer customer = customerReopsitory.findByMobileNumberAndPassword(loginRequestDto.getMobileNumber(),
				loginRequestDto.getPassword());
		if (Objects.isNull(customer)) {
			log.error(ApplicationConstants.CUSTOMER_NOTFOUND_MESSAGE);
			throw new CustomerNotFoundException(ApplicationConstants.CUSTOMER_NOTFOUND_MESSAGE);
		}
		LoginResponseDto loginResponseDto = new LoginResponseDto();
		BeanUtils.copyProperties(customer, loginResponseDto);
		return loginResponseDto;
	}

	/**
	 * @author Sri Keerthna.
	 * @since 2020-02-11. In this method if the customer is having accounts then it
	 *        will fetch the account details of that particular customer.
	 * @param customerId got from the customer.
	 * @return list of accounts.
	 * @throws CustomerNotFoundException if customer is not there then it will throw
	 *                                   this exception.
	 * @throws AccountNotFoundException  if account is not there for that customer
	 *                                   then it will throw this exception.
	 */
	@Override
	public List<AccountResponseDto> accountList(Long customerId)
			throws CustomerNotFoundException, AccountNotFoundException {
		Optional<Customer> customer = customerReopsitory.findById(customerId);
		if (!customer.isPresent()) {
			logger.error("customer not found exception occurred");
			throw new CustomerNotFoundException(ApplicationConstants.CUSTOMER_NOTFOUND_MESSAGE);
		}
		List<Account> accounts = accountRepository.findAccountByCustomer(customer);
		if (accounts.isEmpty()) {
			logger.error("Account not found");
			throw new AccountNotFoundException(ApplicationConstants.ACCOUNT_NOTFOUND_MESSAGE);
		}
		List<AccountResponseDto> accountResponseDto = new ArrayList<>();
		accounts.forEach(account -> {
			AccountResponseDto responseDto = new AccountResponseDto();
			BeanUtils.copyProperties(account, responseDto);
			accountResponseDto.add(responseDto);
		});
		logger.info("Got the account details");
		return accountResponseDto;
	}

	/**
	 * @author Sri Keerthna.
	 * @since 2020-02-11. In this method using customer id monthly transactions are
	 *        fetched from database.
	 * @param customerId got from customer
	 * @param month      for which month they want the transaction summary.
	 * @param year       for which month they want the transaction summary.
	 * @return list of transactions taken place on that particular month and year.
	 * @throws CustomerNotFoundException     if customer is not found
	 * @throws TransactionsNotFoundException if transaction is not there.
	 * @throws AccountNotFoundException      if account is not there for that
	 *                                       customer.
	 */
	@Override
	public List<TransactionResponseDto> transactionList(Long customerId, Integer month, Integer year)
			throws CustomerNotFoundException, TransactionsNotFoundException, AccountNotFoundException {
		Optional<Customer> customer = customerReopsitory.findById(customerId);
		if (!customer.isPresent()) {
			logger.error("customer not found exception occurred");
			throw new CustomerNotFoundException(ApplicationConstants.CUSTOMER_NOTFOUND_MESSAGE);
		}
		List<Account> accounts = accountRepository.findAccountByCustomer(customer);
		if (accounts.isEmpty()) {
			logger.error("Account not found");
			throw new AccountNotFoundException(ApplicationConstants.ACCOUNT_NOTFOUND_MESSAGE);
		}
		List<TransactionResponseDto> responseDto = new ArrayList<>();
		accounts.forEach(account -> {
			List<LocalDateTime> dates = Converter.getDateFromMonthAndYear(month, year);
			LocalDateTime fromDate = dates.get(0);
			LocalDateTime toDate = dates.get(1);
			List<FundTransfer> fundTransfer = fundTransferRepository.findByTransferDate(account.getAccountNumber(),
					fromDate, toDate, ApplicationConstants.PENDING);
			fundTransfer.forEach(fund -> {
				TransactionResponseDto transactionResponseDto = new TransactionResponseDto();
				BeanUtils.copyProperties(fund, transactionResponseDto);
				transactionResponseDto.setFromAccount(fund.getAccount().getAccountNumber());
				responseDto.add(transactionResponseDto);
			});
		});
		logger.info("Got the list of transaction taken place on that particular month and year");
		return responseDto;
	}

	@Override
	public Optional<Customer> getCustomerByCustomerId(Long customerId) {
		return customerReopsitory.findById(customerId);
	}

	@Override
	public Optional<Account> getAccountByAccountNumber(Long fromAccount) {
		return accountRepository.findById(fromAccount);
	}
}
