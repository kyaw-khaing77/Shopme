package com.shopme.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTests {

	@Autowired
	private CustomerRepository repo;
	
	@Autowired
	private TestEntityManager em;
	
	@Test
	public void testCreateCustomer1() {
		Integer countryId = 234;
		Country country = em.find(Country.class, countryId);
		
		Customer customer = new Customer();
		customer.setCountry(country);
		customer.setFirstName("David");
		customer.setLastName("Fountaine");
		customer.setPassword("psw");
		customer.setEmail("david@gmail.com");
		customer.setPhoneNumber("123-456");
		customer.setAddressLine1("No 422 L quarter");
		customer.setAddressLine2("NorthOkkalapa");
		customer.setCity("Pathein");
		customer.setState("Chaung thar");
		customer.setPostalCode("2323");
		customer.setCreatedTime(new Date());
		
		Customer savedCustomer = repo.save(customer);
		assertThat(savedCustomer.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateCustomer2() {
		Integer countryId = 106;
		Country country = em.find(Country.class, countryId);
		
		Customer customer = new Customer();
		customer.setCountry(country);
		customer.setFirstName("Sanya");
		customer.setLastName("Lad");
		customer.setPassword("psw");
		customer.setEmail("machial@gmail.com");
		customer.setPhoneNumber("123-456");
		customer.setAddressLine1("nani address 1");
		customer.setAddressLine2("nani address 2");
		customer.setCity("Mumbai");
		customer.setState("Muharashtra");
		customer.setPostalCode("400013");
		customer.setCreatedTime(new Date());
		
		Customer savedCustomer = repo.save(customer);
		assertThat(savedCustomer.getId()).isGreaterThan(0);
	}
	
	@Test
	public void findAllCustomers() {
		Iterable<Customer> customers = repo.findAll();
		customers.forEach(System.out::println);
		
		assertThat(customers).hasSizeGreaterThan(1);
	}
	
	@Test
	public void updateCustomer() {
		Integer customerId = 1;
		String customerFirstName = "Zen";
		Customer customer = repo.findById(customerId).get();
		
		customer.setFirstName(customerFirstName);
		customer.setEnabled(true);
		
		Customer savedCustomer = repo.save(customer);
		
		assertThat(savedCustomer.getFirstName()).isEqualTo(customerFirstName);
	}
	
	@Test
	public void deleteCustomer() {
		Integer customerId = 2;
		
		repo.deleteById(customerId);
		
		Optional<Customer> customer = repo.findById(customerId);
		
		assertThat(customer).isNotPresent();
		
	}
	
	@Test
	public void testCustomerAuthenticationType() {
		
		
	}
}
