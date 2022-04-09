package com.shopme.admin.customer;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@Service
@Transactional
public class CustomerService {
	
	public static final int CUSTOMERS_PER_PAGE = 10; 

	@Autowired
	private CustomerRepository repo;
	
	@Autowired
	private CountryRepository countryRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public void listByPage(int pageNum,PagingAndSortingHelper helper){	
		helper.listEntities(pageNum, CUSTOMERS_PER_PAGE, repo);
	}
	
	public void updateCustomerEnableStatus(Integer id,boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}
	
	public Customer getCustomer(Integer id) throws CustomerNotFoundException {
		try {
			return repo.findById(id).get();
		}catch (NoSuchElementException ex) {
			throw new CustomerNotFoundException("Could not find any Customer with ID "+id);
		}
	}
	
	public boolean isEmailUnique(Integer id,String email) {
		Customer customer = repo.findByEmail(email);
		
		if(customer != null && customer.getId() != id ) {
			return false;
		}
		
		return true;
	}
	
	public void save(Customer customerInForm) {
		
		Customer customerInDb = repo.findById(customerInForm.getId()).get();

		if(!customerInForm.getPassword().isEmpty()) {
			encodePassword(customerInForm);		
		}else {
			customerInForm.setPassword(customerInDb.getPassword());
		}
		
		customerInForm.setEnabled(customerInDb.isEnabled());
		customerInForm.setCreatedTime(customerInDb.getCreatedTime());
		customerInForm.setVerificationCode(customerInDb.getVerificationCode());
		customerInForm.setAuthenticationType(customerInDb.getAuthenticationType());
        customerInForm.setResetPasswordToken(customerInDb.getResetPasswordToken());
		
		repo.save(customerInForm);
		
	}
	
	public void encodePassword(Customer customer) {
	  String encodedPassword	=  passwordEncoder.encode(customer.getPassword());
	  customer.setPassword(encodedPassword);
	}
	
	public List<Country> listAllCountries(){
		return countryRepo.findAllByOrderByNameAsc();
	}
	
	public List<Customer> listAllCustomers(){
		return repo.findAllByOrderByFirstNameAsc();
	}
	
	public void deleteCustomer(Integer id) throws CustomerNotFoundException {
        Long count = repo.countById(id);
        if(count == null || count == 0) {
        	throw new CustomerNotFoundException("Customer not find any customers with ID "+id);
        }
        
        repo.deleteById(id);
	}
}
