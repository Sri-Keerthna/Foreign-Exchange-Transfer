package com.spiralforge.forxtransfer.service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.spiralforge.forxtransfer.constants.ApplicationConstants;
import com.spiralforge.forxtransfer.entity.Account;
import com.spiralforge.forxtransfer.entity.FundTransfer;
import com.spiralforge.forxtransfer.entity.Transaction;
import com.spiralforge.forxtransfer.repository.AccountRepository;
import com.spiralforge.forxtransfer.repository.CustomerRepository;
import com.spiralforge.forxtransfer.repository.FundTransferRepository;
import com.spiralforge.forxtransfer.repository.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

	Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

	@Autowired
	CustomerRepository customerReopsitory;

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private FundTransferRepository fundTransferRepository;

	/**
	 * @author Muthu
	 * 
	 *         Method is used to run for running in specific intervals
	 * 
	 */
	@Scheduled(cron = "*/5 * * * * ?")
	public void scheduleTransactionStatus() {
		List<FundTransfer> fundTransferList = fundTransferRepository
				.findAllByTransferStatus(ApplicationConstants.STATUS_PENDING_MESSAGE);
		if (fundTransferList.isEmpty()) {
			log.error(ApplicationConstants.FUNDTRANSFER_LIST_EMPTY_MESSAGE);
		}
		fundTransferList.forEach(transferDetails -> {
			if (transferDetails.getToAccount().equals(transferDetails.getAccount().getAccountNumber())) {
				log.info(ApplicationConstants.TRANSACTION_FAILED);
				saveFundTransfer(transferDetails, ApplicationConstants.TRANSACTION_FAILED);
			} else {
				Account creditAccount = checkAccountDetails(transferDetails, transferDetails.getToAccount());
				Account debitAccount = checkAccountDetails(transferDetails,
						transferDetails.getAccount().getAccountNumber());
				Boolean amountResponse = checkBalance(transferDetails.getTransferAmount(),
						transferDetails.getChargeAmount(), debitAccount.getBalance());
				if (amountResponse.equals(false)) {
					saveFundTransfer(transferDetails, ApplicationConstants.NO_BALANCE_MESSAGE);
				} else {
					Double updatedDebitBalance = updateDebitAccountBalance(transferDetails.getTransferAmount(),
							transferDetails.getChargeAmount(), debitAccount.getBalance());
					debitAccount.setBalance(updatedDebitBalance);
					accountRepository.save(debitAccount);
					Double updatedCreditBalance = updateCreditAccountBalance(transferDetails.getTransferAmount(),
							creditAccount.getBalance());
					creditAccount.setBalance(updatedCreditBalance);
					accountRepository.save(creditAccount);
					saveFundTransfer(transferDetails, ApplicationConstants.TRANSFER_SUCCESS_MESSAGE);
					saveTransaction(transferDetails, debitAccount, transferDetails.getTransferAmount(),
							transferDetails.getChargeAmount(), transferDetails.getToAccount());
				}
			}
		});
	}

	/**
	 * @author Muthu
	 *
	 *         Method is used to check whether the account details is present or not
	 *
	 * @param transferDetails which takes the values from the fund transfer table
	 * @param toAccount       which account customer want to transfer amount
	 * @return details of the account that includes id,balance
	 */
	private Account checkAccountDetails(FundTransfer transferDetails, Long toAccount) {
		Account account = accountRepository.findByAccountNumber(toAccount);
		if (Objects.isNull(account)) {
			log.info(ApplicationConstants.ACCOUNT_INVALID);
			saveFundTransfer(transferDetails, ApplicationConstants.ACCOUNT_INVALID);
		}
		return account;
	}

	/**
	 * @author Muthu
	 *
	 *         Method is used to update the transaction table after getting success
	 *
	 * @param transferDetails transferDetails which takes the values from the fund
	 *                        transfer table
	 * @param debitAccount    which is customer account
	 * @param transferAmount  amount he wants to transfer
	 * @param chargeAmount    charges for the amount he want to transfer
	 * @param toAccount       which account customer want to transfer amount
	 */
	private void saveTransaction(FundTransfer transferDetails, Account debitAccount, Double transferAmount,
			Double chargeAmount, Long toAccount) {
		Transaction transaction = new Transaction();
		transaction.setAccount(debitAccount);
		transaction.setAmount(transferAmount + chargeAmount);
		transaction.setToAccount(toAccount);
		transaction.setTransactionDate(transferDetails.getTransferDate());
		transaction.setTransactionType(ApplicationConstants.DEBIT_MESSAGE);
		transactionRepository.save(transaction);
	}

	/**
	 * @author Muthu
	 *
	 *         Method is used to save in fund transfer table
	 *
	 * @param transferDetails transferDetails which takes the values from the fund
	 *                        transfer table
	 * @param status          whether transaction is success
	 * @return Saves in table
	 */
	private FundTransfer saveFundTransfer(FundTransfer transferDetails, String status) {
		transferDetails.setTransferStatus(status);
		return fundTransferRepository.save(transferDetails);
	}

	/**
	 * @author Muthu
	 *
	 *         Method is used to get the updated balance amount after transaction in
	 *         credit account
	 *
	 * @param transferAmount amount he wants to transfer
	 * @param balance        balance in his/her account
	 * @return credit account balance
	 */
	private Double updateCreditAccountBalance(Double transferAmount, Double balance) {
		return transferAmount + balance;
	}

	/**
	 * @author Muthu
	 *
	 *         Method is used to check whether the customer on debited side has
	 *         sufficient amount to transfer
	 *
	 * @param transferAmount amount he wants to transfer
	 * @param chargeAmount   charges for the amount he want to transfer
	 * @param balance        balance in his/her account
	 * @return true or false
	 */
	private Boolean checkBalance(Double transferAmount, Double chargeAmount, Double balance) {
		Double totalAmount = transferAmount + chargeAmount;
		return (balance >= totalAmount);
	}

	/**
	 * @author Muthu
	 *
	 *         Method is used to get the updated balance amount after transaction in
	 *         debit account
	 *
	 * @param transferAmount amount he wants to transfer
	 * @param chargeAmount   charges for the amount he want to transfer
	 * @param balance        balance in his/her account
	 * @return debit account balance
	 */
	private Double updateDebitAccountBalance(Double transferAmount, Double chargeAmount, Double balance) {
		return balance - transferAmount - chargeAmount;
	}
}
