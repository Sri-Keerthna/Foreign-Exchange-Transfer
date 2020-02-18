package com.spiralforge.forxtransfer.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
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
import com.spiralforge.forxtransfer.repository.FundTransferRepository;
import com.spiralforge.forxtransfer.util.Utility;

@Service
public class TransferServiceImpl implements TransferService {

	Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private FundTransferRepository fundTransferRepository;

	/**
	 * 
	 */
	@Override
	public ExchangeResponseDto previewExchangeAmount(Long customerId, String base, String quote, Double amount)
			throws CurrencyNotFoundException {
		ExchangeResponseDto exchangeResponseDto = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<>(headers);
		String url = "https://api.exchangeratesapi.io/latest?base=" + base;

		XchangeDto xchangeDto = restTemplate.exchange(url, HttpMethod.GET, entity, XchangeDto.class).getBody();
		if (!Objects.isNull(xchangeDto)) {
			logger.info("getting result from excernal api");
			exchangeResponseDto = getExchangeAmount(customerId, base, quote, amount, xchangeDto);
		}
		return exchangeResponseDto;
	}

	/**
	 * @author Sujal
	 * 
	 *         This method is used to get the exchange rate for the fund
	 * @param customerId
	 * @param base
	 * @param quote
	 * @param amount
	 * @param xchangeDto
	 * @return ExchangeResponseDto
	 * @throws CurrencyNotFoundException
	 */
	private ExchangeResponseDto getExchangeAmount(Long customerId, String base, String quote, Double amount,
			XchangeDto xchangeDto) throws CurrencyNotFoundException {

		ExchangeResponseDto exchangeResponseDto = null;
		HashMap<String, Double> xchangeRates = xchangeDto.getRates();
		Optional<Double> xchangeRate = xchangeRates.entrySet().stream()
				.filter(key -> key.getKey().equalsIgnoreCase(quote)).map(mapper -> mapper.getValue()).findAny();

		if (!xchangeRate.isPresent()) {
			logger.error("no data present for provided currency");

			throw new CurrencyNotFoundException(ApplicationConstants.CURRENCY_INVALID);
		} else {
			logger.info(" result present for provided currency");
			exchangeResponseDto = new ExchangeResponseDto();
			Double charges = Utility.calculateChareges(amount);
			Double transferAmount = (Math.round(xchangeRate.get() * amount * 100.0) / 100.0);
			exchangeResponseDto.setCharges(charges);
			exchangeResponseDto.setTransferAmount(transferAmount);
			exchangeResponseDto.setTotalAmount(amount + charges);
			exchangeResponseDto.setCustomerId(customerId);
			exchangeResponseDto.setRate((Math.round(xchangeRate.get() * 100.0) / 100.0));
			exchangeResponseDto.setDate(xchangeDto.getDate());
		}
		return exchangeResponseDto;
	}

	/**
	 * @author Sujal
	 * 
	 *         This method is used to transfer the fund
	 * @param customerId
	 * @param ExchangeRequestDto
	 * @return FundTransfer
	 * @throws CustomerNotFoundException
	 * @throws AccountNotFoundException
	 */
	@Override
	public FundTransfer transfer(Long customerId, ExchangeRequestDto exchangeRequestDto)
			throws CustomerNotFoundException, AccountNotFoundException {
		FundTransfer fundTransfer1 = null;
		Optional<Customer> customer = customerService.getCustomerByCustomerId(customerId);

		if (!customer.isPresent()) {
			logger.error(" inside customer not found");
			throw new CustomerNotFoundException(ApplicationConstants.CUSTOMER_NOTFOUND_MESSAGE);
		} else {
			Optional<Account> account = customerService.getAccountByAccountNumber(exchangeRequestDto.getFromAccount());
			if (!account.isPresent()) {
				logger.error(" inside account not found");
				throw new AccountNotFoundException(ApplicationConstants.ACCOUNT_INVALID);
			} else {
				logger.info(" inside customer found");
				fundTransfer1 = saveFundTransfer(exchangeRequestDto, account.get());
			}
		}
		return fundTransfer1;
	}

	/**
	 * @author Sujal
	 * 
	 *         This method is used to save the fund transfer data
	 * 
	 * @param exchangeRequestDto
	 * @param account
	 * @return FundTransfer
	 */
	private FundTransfer saveFundTransfer(ExchangeRequestDto exchangeRequestDto, Account account) {
		FundTransfer fundTransfer = new FundTransfer();
		BeanUtils.copyProperties(exchangeRequestDto, fundTransfer);
		fundTransfer.setAccount(account);
		fundTransfer.setTransferDate(LocalDateTime.now());
		fundTransfer.setTransferStatus(ApplicationConstants.STATUS_PENDING_MESSAGE);
		return fundTransferRepository.save(fundTransfer);
	}
}
