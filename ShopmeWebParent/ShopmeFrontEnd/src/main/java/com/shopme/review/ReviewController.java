package com.shopme.review;

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
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.common.exception.ReviewNotFoundException;
import com.shopme.customer.CustomerNotFoundException;
import com.shopme.product.ProductService;
import com.shopme.review.vote.ReviewVoteService;

@Controller
public class ReviewController {
	
	private String defaultRedirectURL = "redirect:/reviews/page/1?sortField=reviewTime&sortDir=desc";

	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ControllerHelper controllerHelper;
	
	@Autowired
	private ReviewVoteService reviewVoteService;
	
	
	@GetMapping("/reviews")
	public String listFirstPage(Model model) {
				
		return defaultRedirectURL;
	}
	
	@GetMapping("/reviews/page/{pageNum}") 
	public String listReviewsByCustomerByPage(Model model, HttpServletRequest request,
							@PathVariable(name = "pageNum") int pageNum,
							String keyword, String sortField, String sortDir) throws CustomerNotFoundException {
				
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
				
		Page<Review> page = reviewService.listByCustomerByPage(customer, keyword, pageNum, sortField, sortDir);		
		List<Review> listReviews = page.getContent();

		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("moduleURL", "/reviews");

		model.addAttribute("listReviews", listReviews);

		long startCount = (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
				
		model.addAttribute("startCount", startCount);

		long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
		
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		model.addAttribute("endCount", endCount);
		
		return "reviews/reviews_customer";
	}
	
	@GetMapping("/reviews/detail/{id}")
	public String viewReview(@PathVariable("id") Integer id, Model model, 
			RedirectAttributes ra, HttpServletRequest request) throws CustomerNotFoundException {
				
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		
		try {
			
			Review review = reviewService.getByCustomerAndId(customer, id);
						
			model.addAttribute("review", review);

			return "reviews/review_detail_modal";
			
		} catch (ReviewNotFoundException ex) {
						
			ra.addFlashAttribute("message", ex.getMessage());
			
			return defaultRedirectURL;		
		}
	}
	
	@GetMapping("/ratings/{productAlias}/page/{pageNum}") 
	public String ListReviewsByPage(Model model,
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
		
		
		Page<Review> page = reviewService.listByProduct(product, pageNum, sortField, sortDir);	
		List<Review> listReviews = page.getContent();

		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		if(customer!= null) {
			reviewVoteService.makeReviewsVotedForProductByCustomer(listReviews,product.getId() , customer.getId());			
		}
		
		
		model.addAttribute("product", product);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("moduleURL", "/reviews");

		model.addAttribute("listReviews", listReviews);

		long startCount = (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
				
		model.addAttribute("startCount", startCount);

		long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
		
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		model.addAttribute("endCount", endCount);
		model.addAttribute("pageTitle", "Reviews for "+product.getShortName());
		
		return "reviews/reviews_product";
	}
	
	@GetMapping("/ratings/{productAlias}")
	public String ListReviewsByFirstPage(Model model,
			@PathVariable(name = "productAlias") String productAlias,HttpServletRequest request) {
         return ListReviewsByPage(model, 1, productAlias, "reviewTime", "desc",request);
	}
	
	@GetMapping("/write_review/product/{productId}")
	public String showViewForm(@PathVariable("productId") Integer productId,Model model,HttpServletRequest request) {
		Review review = new Review();
		Product product = null;
		try {
			product = productService.getProduct(productId);
		}catch (ProductNotFoundException e) {
			return "error/404";
		}
		
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
				
		boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());		

		if (customerReviewed) {
			model.addAttribute("customerReviewed", customerReviewed);
		} else {
			boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
			
			if (customerCanReview) {
				model.addAttribute("customerCanReview", customerCanReview);
			} else {
				model.addAttribute("NoReviewPermission", true);
			}
		}	
		
		model.addAttribute("product", product);
		model.addAttribute("review", review);

		
		return "reviews/review_form";
	}
	
	@PostMapping("/post_review")
	public String saveReview(Model model,Review review,Integer productId,HttpServletRequest request) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		
		Product product = null;
		try {
			product = productService.getProduct(productId);
		}catch (ProductNotFoundException e) {
			return "error/404";
		}
		
		review.setProduct(product);
		review.setCustomer(customer);
		
		Review savedReview = reviewService.save(review);
		model.addAttribute("review", savedReview);
		
		return "reviews/review_done";
		
	}
	
	
}
