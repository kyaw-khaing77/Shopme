package com.shopme.admin.customer;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.Customer;

@Repository
public interface CustomerRepository extends SearchRepository<Customer, Integer> {

	@Query("Select c From Customer c where CONCAT(c.email,' ',c.firstName,' ',c.lastName,' ',c.addressLine1,' ',"
			+ "c.addressLine2,' ',c.city,' ',c.state,' ',c.postalCode,' ',c.country.name) LIKE %?1%")
	public Page<Customer> findAll(String keyword,Pageable pageable);
	
	@Query("Update Customer c Set c.enabled=?2 Where c.id =?1")
	@Modifying
	public void updateEnabledStatus(Integer id,boolean enabled);
	
	public long countById(Integer id);
	
	@Query("select c from Customer c where c.email =?1")
	public Customer findByEmail(String email);
	
	public List<Customer> findAllByOrderByFirstNameAsc();

}
