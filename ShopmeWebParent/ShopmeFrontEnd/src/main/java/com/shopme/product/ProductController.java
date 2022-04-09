package com.shopme.product;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.ControllerHelper;
import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.question.Question;
import com.shopme.common.exception.CategoryNotFoundException;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.question.QuestionService;
import com.shopme.question.vote.QuestionVoteService;
import com.shopme.review.ReviewService;
import com.shopme.review.vote.ReviewVoteService;

@Controller
public class ProductController {

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private QuestionService questionService;
	
	@Autowired
	private ControllerHelper controllerHelper;
	
	@Autowired
	private ReviewVoteService reviewVoteService;
	
	@Autowired
	private QuestionVoteService questionVoteService;


	@GetMapping("/c/{categories_alias}")
	public String viewCategoryFirstPage(@PathVariable("categories_alias") String alias,
			   Model model) {
		return viewCategoryByPage(alias, 1, model);
	}
	
	@GetMapping("/c/{categories_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable("categories_alias") String alias,
			   @PathVariable("pageNum") int pageNum,
			   Model model) {
		
		try {
				Category category = categoryService.getCategory(alias);
				
				
				List<Category> categoryParents = categoryService.getCategoryParents(category);
				
				Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());
				
				List<Product> listProducts = pageProducts.getContent();
				
				long startCount = (pageNum - 1)*ProductService.PRODUCTS_PER_PAGE+1;
				long endCount   = startCount+ProductService.PRODUCTS_PER_PAGE-1;
				if(endCount > pageProducts.getTotalElements()) {
					endCount = pageProducts.getTotalElements();
				}
					
				model.addAttribute("startCount", startCount);
				model.addAttribute("endCount", endCount);
			    model.addAttribute("totalPages", pageProducts.getTotalPages());
			    model.addAttribute("totalItems", pageProducts.getTotalElements());
			    model.addAttribute("currentPage", pageNum);
				model.addAttribute("listProducts", listProducts);
				model.addAttribute("categoryParents", categoryParents);		
				model.addAttribute("pageTitle", category.getName());
				model.addAttribute("category", category);
				return "product/products_by_category";
		}catch (CategoryNotFoundException e) {
				return "error/404";
			
        }
	}
	
	@GetMapping("/p/{product_alias}")
	public String viewProductDetail(@PathVariable("product_alias") String alias,Model model,HttpServletRequest request) {
		try {
			Product product = productService.getProduct(alias);
			List<Category> categoryParents = categoryService.getCategoryParents(product.getCategory());
			Page<Review> listReviews = reviewService.list3MostRecentReviewsByProduct(product);
			Page<Question> listQuestions = questionService.list3MostRecentQuestionByProduct(product);
			
			Customer customer = controllerHelper.getAuthenticatedCustomer(request);
			if(customer != null) {
				boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());
				
				reviewVoteService.makeReviewsVotedForProductByCustomer(listReviews.getContent(),product.getId() , customer.getId());
				questionVoteService.makeQuestionsVotedForProductByCustomer(listQuestions.getContent(),product.getId() , customer.getId());
				
				if(customerReviewed) {
					model.addAttribute("customerReviewed", customerReviewed);
				}else {
					boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
					model.addAttribute("customerReviewed", customerCanReview);
				}
				
				model.addAttribute("canAskQuestion", true);
			}else {
				model.addAttribute("canAskQuestion", false);
			}

			model.addAttribute("categoryParents", categoryParents);	
			model.addAttribute("product", product);
			model.addAttribute("pageTitle", product.getShortName());
			model.addAttribute("listReviews", listReviews);
			model.addAttribute("listQuestions", listQuestions);
			model.addAttribute("questionListSize", listQuestions.getTotalElements());
			
			return "product/product_detail";
		}catch(ProductNotFoundException e) {
			return "error/404";
		}
	}

	
	@GetMapping("/search")
	public String search(@Param("keyword") String keyword,Model model) {
		return searchByPage(keyword, 1, model);
		
	}
	
	
	@GetMapping("/search/page/{pageNum}")
	public String searchByPage(@Param("keyword") String keyword,
			@PathVariable("pageNum") int pageNum,Model model) {
            
		    Page<Product> pageProducts = productService.search(keyword, pageNum);
		    List<Product> listResult   = pageProducts.getContent();
		    
		    long startCount = (pageNum - 1)*ProductService.SEARCH_RESULTS_PER_PAGE+1;
			long endCount   = startCount+ProductService.SEARCH_RESULTS_PER_PAGE-1;
			if(endCount > pageProducts.getTotalElements()) {
				endCount = pageProducts.getTotalElements();
			}
				
			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);
		    model.addAttribute("totalPages", pageProducts.getTotalPages());
		    model.addAttribute("totalItems", pageProducts.getTotalElements());
		    model.addAttribute("currentPage", pageNum);
			model.addAttribute("listResult", listResult);	
			model.addAttribute("keyword", keyword);	
			model.addAttribute("pageTitle", keyword +" - Search Result");
			
			return "product/search_result";
	
	}
}
