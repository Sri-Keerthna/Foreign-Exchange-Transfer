package com.spiralforge.forxtransfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spiralforge.forxtransfer.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{

	Customer findByMobileNumberAndPassword(Long mobileNumber, String password);

}