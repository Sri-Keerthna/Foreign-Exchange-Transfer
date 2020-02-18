package com.spiralforge.forxtransfer.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
@SequenceGenerator(name = "transactionId", initialValue = 100105, allocationSize = 1)
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactionId")
	private Long transactionId;
	@OneToOne
	@JoinColumn(name = "from_account")
	private Account account;
	private Long toAccount;
	private Double amount;
	private String transactionType;
	private LocalDateTime transactionDate;
}
