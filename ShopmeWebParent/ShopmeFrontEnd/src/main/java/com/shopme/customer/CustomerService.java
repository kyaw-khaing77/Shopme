package com.shopme.customer;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {

	@Autowired
    private CustomerRepository repo;
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public List<Country> listAllCountries(){
		return countryRepository.findAllByOrderByNameAsc();
	}
	
	public boolean isEmailUnique(String email) {
		Customer customer = repo.findByEmail(email);
		return customer == null;
	}
	
	public void registerCustomer(Customer customer) {
		encodePassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(AuthenticationType.DATABASE);
		
		
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		
		repo.save(customer);
		
	}
	
	public void encodePassword(Customer customer) {
	  String encodedPassword	=  passwordEncoder.encode(customer.getPassword());
	  customer.setPassword(encodedPassword);
	}
	
	public Customer findCustomerByEmail(String email) {
		return repo.findByEmail(email);
	}
	

	
	public boolean verify(String verificationCode) {
		Customer customer = repo.findByVerificationCode(verificationCode);
		if(customer == null || customer.isEnabled()) {
			return false;
		}else {
			repo.enable(customer.getId());
			return true;
		}
	}
	
	public void updateCustomerAuthenticationType(Customer customer,AuthenticationType authenticationType) {
		if(!customer.getAuthenticationType().equals(authenticationType)) {
			repo.updateAuthenticationType(customer.getId(), authenticationType);
		}
	}

	public void addNewCustomerUponOAuthLogin(String name, String email,String countryCode,AuthenticationType authenticationType) {
			Customer customer = new Customer();
			customer.setEmail(email);
			
			setName(name,customer);
			
			customer.setEnabled(true);
			customer.setCreatedTime(new Date());
			customer.setAuthenticationType(authenticationType);
			customer.setPassword("");
			customer.setAddressLine1("");
			customer.setCity("");
			customer.setPhoneNumber("");
			customer.setPostalCode("");
			customer.setCountry(countryRepository.findByCode(countryCode));
			customer.setState("");
			
			repo.save(customer);	
	}

	private void setName(String name,Customer customer) {
		String[] nameArray = name.split(" ");
		if(nameArray.length < 2) {
			customer.setFirstName(name);
			customer.setLastName("");
		}else {
			String firstName = nameArray[0];
			customer.setFirstName(firstName);
			
			String lastName = name.replaceFirst(firstName+" ", "");
			customer.setLastName(lastName);
		}
		
	}
	
	public void update(Customer customerInForm) {
		Customer customerInDB = repo.findById(customerInForm.getId()).get();

		if (customerInDB.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
			if (!customerInForm.getPassword().isEmpty()) {
				String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
				customerInForm.setPassword(encodedPassword);			
			} else {
				customerInForm.setPassword(customerInDB.getPassword());
			}		
		} else {
			customerInForm.setPassword(customerInDB.getPassword());
		}

		customerInForm.setEnabled(customerInDB.isEnabled());
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());
		customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
        customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());

		
		repo.save(customerInForm);
	}

	public String updateResetPasswordToken(String email) throws CustomerNotFoundException {
		Customer customer = repo.findByEmail(email);
		if(customer != null) {
			String token=RandomString.make(30);
			customer.setResetPasswordToken(token);
			repo.save(customer);
			return token;
		}else {
			throw new CustomerNotFoundException("Could not find any customer with the email "+email);
		}
		
	}
	
	public Customer getByResetPasswordToken(String token) {
		return repo.findByResetPasswordToken(token);
	}
	
	public void updatePassword(String token, String newPassword) throws CustomerNotFoundException {
		// TODO Auto-generated method stub
		Customer customer = repo.findByResetPasswordToken(token);
		if (customer == null) {
			throw new CustomerNotFoundException("No customer found: invalid token");
		}

		customer.setPassword(newPassword);
		customer.setResetPasswordToken(null);
		encodePassword(customer);

		repo.save(customer);
	}	
	
	
}
