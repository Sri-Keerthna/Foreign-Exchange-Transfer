package com.spiralforge.forxtransfer.controller;

import java.util.List;

import javax.validation.Valid;

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
import com.spiralforge.forxtransfer.dto.AccountResponseDto;
import com.spiralforge.forxtransfer.dto.LoginRequestDto;
import com.spiralforge.forxtransfer.dto.LoginResponseDto;
import com.spiralforge.forxtransfer.dto.TransactionResponseDto;
import com.spiralforge.forxtransfer.exception.AccountNotFoundException;
import com.spiralforge.forxtransfer.exception.CustomerNotFoundException;
import com.spiralforge.forxtransfer.exception.TransactionsNotFoundException;
import com.spiralforge.forxtransfer.service.CustomerService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sri Keerthna.
 * @author Muthu.
 * @author Sujal.
 * @since 2020-02-11.
 */
@RestController
@RequestMapping("/customers")
@Slf4j
@CrossOrigin(allowedHeaders = { "*", "*/" }, origins = { "*", "*/" })
public class CustomerController {

	/**
	 * The Constant log.
	 */
	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	@Autowired
	CustomerService customerService;

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
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> checkLogin(@Valid @RequestBody LoginRequestDto loginRequestDto)
			throws CustomerNotFoundException {
		log.info("For checking whether the person is staff or a customer");
		LoginResponseDto loginResponse = customerService.checkLogin(loginRequestDto);
		log.info(ApplicationConstants.LOGIN_SUCCESSMESSAGE);
		loginResponse.setMessage(ApplicationConstants.LOGIN_SUCCESSMESSAGE);
		return new ResponseEntity<>(loginResponse, HttpStatus.OK);
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
	@GetMapping("/{customerId}/accounts")
	public ResponseEntity<List<AccountResponseDto>> accountList(@PathVariable Long customerId)
			throws CustomerNotFoundException, AccountNotFoundException {
		logger.info("Entered into accountList method in controller");
		List<AccountResponseDto> accountResponseList = customerService.accountList(customerId);
		return new ResponseEntity<>(accountResponseList, HttpStatus.OK);
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
	@GetMapping("/{customerId}/transactions")
	public ResponseEntity<List<TransactionResponseDto>> transactionList(@PathVariable Long customerId,
			@RequestParam Integer month, @RequestParam Integer year)
			throws CustomerNotFoundException, TransactionsNotFoundException, AccountNotFoundException {
		logger.info("Entered into transactionList method in controller");
		List<TransactionResponseDto> transactionResponse = customerService.transactionList(customerId, month, year);
		if (transactionResponse.isEmpty()) {
			logger.error("No transactions found");
			throw new TransactionsNotFoundException(ApplicationConstants.TRANSACTION_NOTFOUND_EXCEPTION);
		}
		return new ResponseEntity<>(transactionResponse, HttpStatus.OK);
	}
}
