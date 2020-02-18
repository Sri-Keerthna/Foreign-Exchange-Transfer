package com.spiralforge.forxtransfer.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.spiralforge.forxtransfer.entity.FundTransfer;

@Repository
public interface FundTransferRepository extends JpaRepository<FundTransfer, Long> {

	List<FundTransfer> findAllByTransferStatus(String statusPendingMessage);

	/**
	 * @author Sri Keerthna.
	 * @since 2020-02-11.
	 * In this method using account number, transfer date and status this query will fetch from database.
	 * @param accountNumber got from customer.
	 * @param fromDate got from customer.
	 * @param toDate got from customer.
	 * @param pending status that are pending will be eliminated from this list.
	 * @return list of transactions.
	 */
	@Query("SELECT t FROM FundTransfer t WHERE t.account.accountNumber=:accountNumber and t.transferDate between :fromDate and :toDate and not t.transferStatus=:pending")
	List<FundTransfer> findByTransferDate(Long accountNumber, LocalDateTime fromDate, LocalDateTime toDate,
			String pending);

}
