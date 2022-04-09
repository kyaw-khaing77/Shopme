package com.shopme.admin.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {

	@Autowired
	private CustomerService service;
	
	@PostMapping("/customers/check_unique_email")
	public String checkDuplicateEmail(int id,String email) {
		return service.isEmailUnique(id,email)?"OK":"Duplicated";
	}
	
}
