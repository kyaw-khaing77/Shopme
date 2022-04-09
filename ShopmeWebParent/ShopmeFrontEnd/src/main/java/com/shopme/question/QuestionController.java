package com.shopme.question;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.ControllerHelper;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.question.Question;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.common.exception.QuestionNotFoundException;
import com.shopme.customer.CustomerNotFoundException;
import com.shopme.product.ProductService;
import com.shopme.question.vote.QuestionVoteService;
import com.shopme.review.ReviewService;

@Controller
public class QuestionController {

	private String defaultRedirectURL = "redirect:/questions/page/1?sortField=askTime&sortDir=desc";
	
	@Autowired
	private QuestionService questionService;
	
	@Autowired
	private ControllerHelper controllerHelper;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private QuestionVoteService questionVoteService;
	
	@GetMapping("/questions")
	public String listFirstPage(Model model) {
				
		return defaultRedirectURL;
	}
	
	@GetMapping("/questions/page/{pageNum}") 
	public String listReviewsByCustomerByPage(Model model, HttpServletRequest request,
							@PathVariable(name = "pageNum") int pageNum,
							String keyword, String sortField, String sortDir) throws CustomerNotFoundException {
				
		Customer asker = controllerHelper.getAuthenticatedCustomer(request);
				
		Page<Question> page = questionService.listByAskerByPage(asker, keyword, pageNum, sortField, sortDir);	
		List<Question> listQuestions = page.getContent();

		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("moduleURL", "/questions");

		model.addAttribute("listQuestions", listQuestions);

		long startCount = (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
				
		model.addAttribute("startCount", startCount);

		long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
		
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		model.addAttribute("endCount", endCount);
		
		return "questions/questions_customer";
	}
	
	@GetMapping("/questions/detail/{id}")
	public String viewQuestion(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,HttpServletRequest request) {
		
		Customer asker = controllerHelper.getAuthenticatedCustomer(request);
		
		try {
			Question question = questionService.getByCustomerAndId(asker, id);
						
			model.addAttribute("question", question);

			return "questions/question_detail_modal";
			
		} catch (QuestionNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;	
		}
	}
	
	@GetMapping("/questions/{productAlias}/page/{pageNum}") 
	public String ListQuestionByPage(Model model,
							@PathVariable(name = "pageNum") int pageNum,
							@PathVariable(name = "productAlias") String productAlias,
							String sortField, String sortDir,
							HttpServletRequest request) {
		
		Product product = null;
		try {
			product = productService.getProduct(productAlias);
		}catch(ProductNotFoundException e){
			return "error/404";
		}
		
		
		Page<Question> page = questionService.listByProduct(product, pageNum, sortField, sortDir);	
		List<Question> listQuestions = page.getContent();
		
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		if(customer!= null) {
			questionVoteService.makeQuestionsVotedForProductByCustomer(listQuestions,product.getId() , customer.getId());			
		}
			
		model.addAttribute("product", product);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("moduleURL", "/reviews");

		model.addAttribute("listQuestions", listQuestions);

		long startCount = (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
				
		model.addAttribute("startCount", startCount);

		long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
		
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		model.addAttribute("endCount", endCount);
		model.addAttribute("pageTitle", "Reviews for "+product.getShortName());
		
		return "questions/questions_product";
	}
	
	@GetMapping("/questions/{productAlias}")
	public String ListQuestionByFirstPage(Model model,
			@PathVariable(name = "productAlias") String productAlias,HttpServletRequest request) {
         return ListQuestionByPage(model, 1, productAlias, "askTime", "desc",request);
	}

}
