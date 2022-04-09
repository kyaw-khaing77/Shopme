package com.shopme.question;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.ControllerHelper;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.question.Question;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.product.ProductService;

@RestController
public class QuestionRestController {
	
	@Autowired
	private QuestionService questionService;
	
	@Autowired
	private ControllerHelper controllerHelper;
	
	@Autowired
	private ProductService productService;
	

	@PostMapping("/post_question")
	public String saveReview(@RequestBody Question question,HttpServletRequest request) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		
		Product product = null;
		try {
			product = productService.getProduct(question.getProduct().getId());
		}catch (ProductNotFoundException e) {
			return "error/404";
		}
		
		question.setProduct(product);
		question.setAsker(customer);
		
		questionService.save(question);
		
		return "Your question has been posted and waiting for approval";
		
	}
}
