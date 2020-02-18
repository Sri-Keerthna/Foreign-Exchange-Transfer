package com.spiralforge.forxtransfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spiralforge.forxtransfer.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{


}
