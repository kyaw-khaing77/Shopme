package com.shopme.admin.customer;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@Controller
public class CustomerController {

	@Autowired
	private CustomerService service;
	
	@GetMapping("/customers")
	public String findAll() {
		
		return "redirect:/customers/page/1?sortField=firstName&sortDir=asc";

	}
	
	@GetMapping("/customers/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "listCustomers",moduleURL = "/customers") PagingAndSortingHelper helper,
			               @PathVariable("pageNum") int pageNum) {
		
		service.listByPage(pageNum,helper);		
		
		return "customers/customers";
	}
	
	@PostMapping("/customers/save")
	public String saveCustomer(Customer customer,RedirectAttributes redirectAttributes) {
	    service.save(customer);
	    redirectAttributes.addFlashAttribute("message", "The customer ID "+customer.getId()+" has been updated");
	    return "redirect:/customers";
	}
	
	@GetMapping("/customers/{id}/enabled/{status}")
	public String updateCustomerEnableStatus(@PathVariable(name="id")Integer id,
			@PathVariable("status")boolean enabled,RedirectAttributes redirectAttributes) {
		service.updateCustomerEnableStatus(id, enabled);
		String status = enabled? "enabled":"disbled";
		String message = "The user ID "+id+" has been "+status;
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/customers";

	}
	
	@GetMapping("/customers/edit/{id}")
	public String editCustomer(@PathVariable(name="id")Integer id,Model model,RedirectAttributes redirectAttributes) {
		
	   try {
		    Customer customer = service.getCustomer(id);
		    List<Country> listCountries = service.listAllCountries();
		    
			model.addAttribute("pageTitle", String.format("Edit Customer (ID: %d)", id));
		    model.addAttribute("customer", customer);
		    model.addAttribute("listCountries", listCountries);

		    return "customers/customer_form";
		   
	   }catch (CustomerNotFoundException e) {
		   redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/customers";   
	   }
		

	}
	
	@GetMapping("/customers/detail/{id}")
	public String detailCustomer(@PathVariable("id")Integer id,Model model,RedirectAttributes redirectAttributes) {
		 try {
			    Customer customer = service.getCustomer(id);
			    
			    model.addAttribute("customer", customer);

			    return "customers/customer_detail_modal";
			   
		   }catch (CustomerNotFoundException e) {
			   redirectAttributes.addFlashAttribute("message", e.getMessage());
				return "redirect:/customers";   
		   }
	
	}
	
	
	@GetMapping("/customers/delete/{id}")
	public String deleteCustomer(@PathVariable(name="id")Integer id,RedirectAttributes redirectAttributes) {
		
	   try {
		   service.deleteCustomer(id);
		   redirectAttributes.addFlashAttribute("message","The customer ID "+ id +" has been deleted sucessfully.");
	   }catch (CustomerNotFoundException e) {
		   redirectAttributes.addFlashAttribute("message", e.getMessage());
	}
		
		return "redirect:/customers";

	}
	
	@GetMapping("/customers/export/csv")
	public void ExportCustomersDataToCsv(HttpServletResponse response) throws IOException {
		List<Customer> customerLists = service.listAllCustomers();
		CustomerCsvExporter exporter = new CustomerCsvExporter();
		exporter.export(customerLists, response);

	}
	
}
