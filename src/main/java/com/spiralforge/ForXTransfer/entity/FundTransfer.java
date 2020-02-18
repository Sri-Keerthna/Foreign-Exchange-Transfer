package com.spiralforge.forxtransfer.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
public class FundTransfer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private Long fundTransferId;
	@OneToOne
	@JoinColumn(name = "from_account")
	private Account account;
	private Long toAccount;
	private Double amount;
	private Double transferAmount;
	private Double chargeAmount;
	private String transferStatus;
	private String currencyType;
	private LocalDateTime transferDate;
}

