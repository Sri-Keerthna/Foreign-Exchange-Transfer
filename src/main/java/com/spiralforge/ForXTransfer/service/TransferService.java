package com.spiralforge.forxtransfer.service;

import com.spiralforge.forxtransfer.dto.ExchangeRequestDto;
import com.spiralforge.forxtransfer.dto.ExchangeResponseDto;
import com.spiralforge.forxtransfer.entity.FundTransfer;
import com.spiralforge.forxtransfer.exception.AccountNotFoundException;
import com.spiralforge.forxtransfer.exception.CurrencyNotFoundException;
import com.spiralforge.forxtransfer.exception.CustomerNotFoundException;

public interface TransferService {

	ExchangeResponseDto previewExchangeAmount(Long customerId, String base, String quote, Double amount) throws CurrencyNotFoundException;

	FundTransfer transfer(Long customerId, ExchangeRequestDto exchangeRequestDto)
			throws CustomerNotFoundException,AccountNotFoundException;

}
