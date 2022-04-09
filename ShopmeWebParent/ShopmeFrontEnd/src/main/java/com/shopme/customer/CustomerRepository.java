package com.shopme.customer;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Integer> {

	@Query("Select c From Customer c Where c.email = ?1")
	public Customer findByEmail(String email);
	
	@Query("Select c From Customer c Where c.verificationCode = ?1")
	public Customer findByVerificationCode(String code);
	
	@Query("Update Customer c Set c.enabled = 1,c.verificationCode = null Where c.id = ?1")
	@Modifying
	public void enable(Integer id);
	
	@Modifying
	@Query("Update Customer c Set c.authenticationType = ?2 Where c.id = ?1")
	public void updateAuthenticationType(Integer Id,AuthenticationType authenticationType);
	
	public Customer findByResetPasswordToken(String token);
}
