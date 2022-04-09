package com.shopme.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Service
@Transactional
public class ProductService {

	public static final int PRODUCTS_PER_PAGE = 5;

	@Autowired
	private ProductRepository repo;
	
	public List<Product> listAll(){
	 return (List<Product>) repo.findAll();
	}
	
	public void listAllByPage(int pageNum,PagingAndSortingHelper helper,Integer categoryId){
		
		
		Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE,pageNum);
		String keyword = helper.getKeyword();
		Page<Product> page = null;
		
		if(keyword != null && !keyword.isEmpty()) {
			if(categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-"+String.valueOf(categoryId)+"-";
				page = repo.searchInCategory(categoryId, categoryIdMatch,keyword, pageable);
			}else {
				page =  repo.findAll(keyword, pageable);
			}
		
		}else {
			if(categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-"+String.valueOf(categoryId)+"-";
				page =  repo.findAll(categoryId, categoryIdMatch, pageable);
			}else {
				page = repo.findAll(pageable);
			}
		}
		
	   helper.updateModelAttriutes(pageNum, page);
	}
	
	public void searchByName(int pageNum,PagingAndSortingHelper helper) {
		Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
		String keyword = helper.getKeyword();
		Page<Product> page = repo.searchByProductName(keyword, pageable);
		helper.updateModelAttriutes(pageNum, page);
	}
	
	public Product save(Product product) {
		if(product.getId() == null) {
			product.setCreatedTime(new Date());
		}
		
		if(product.getAlias() == null || product.getAlias().isEmpty()) {
			String alias = product.getName().replaceAll(" ","_");
			product.setAlias(alias);
		}else {
			product.setAlias(product.getName().replaceAll(" ", "_"));
		}
		
		product.setUpdatedTime(new Date());
		
		Product savedProduct = repo.save(product);
		repo.updateReviewCountAndAverageRating(savedProduct.getId());
		
		return savedProduct;
	}
	
	public void saveProductPrice(Product productInForm) {
		Product productInDb = repo.findById(productInForm.getId()).get();
		productInDb.setCost(productInForm.getCost());
		productInDb.setPrice(productInForm.getPrice());
		productInDb.setDiscountPercent(productInForm.getDiscountPercent());
		
		repo.save(productInDb);
	}
	
	public String findByName(Integer id,String name) {
		Boolean isCreatingNew = (id == null || id == 0);
		Product product = repo.findByName(name);
		
		if(isCreatingNew) {
			if(product != null) {
				return "Duplicate";
			}
		}else {
			if(id != product.getId() && product != null ) {
				return "Duplicate";
			}
		}
		
		return "OK";
	}
	
	
	public void updateProductEnabledStatus(Integer id,boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}
	
	public void deleteProduct(Integer id) throws ProductNotFoundException {
		Long countById = repo.countById(id);
		
		if(countById == null || countById == 0) {
			throw new ProductNotFoundException("Could not find any product with ID "+id);
		}
		
		repo.deleteById(id);
	}
	
	
	public Product get(Integer id) throws ProductNotFoundException {
		try {
			Product product = repo.findById(id).get();
			return product;
		}catch(NoSuchElementException ex) {
			throw new ProductNotFoundException("Product not find any product with ID "+id);
		}
	}
}
