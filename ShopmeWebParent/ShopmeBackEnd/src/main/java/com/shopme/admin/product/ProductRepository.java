package com.shopme.admin.product;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.product.Product;

@Repository
public interface ProductRepository  extends SearchRepository<Product, Integer>{

	public Product findByName(String name);
	
	@Query("Update Product p SET p.enabled = ?2 Where p.id= ?1")
	@Modifying
	public void updateEnabledStatus(Integer id,Boolean enabled);
	
	public Long countById(Integer id);
	
	@Query("Select p from Product p where p.name LIKE %?1% "
			+ "OR p.shortDescription LIKE %?1% "
			+ "OR p.fullDescription LIKE %?1% "
			+ "OR p.brand.name LIKE %?1% "
			+ "OR p.category.name LIKE %?1%")
	public Page<Product> findAll(String keyword,Pageable pageable);
	
	@Query("Select p From Product p where p.category.id = ?1 "
			+ "OR p.category.allParentIDs LIKE %?2%")
	public Page<Product> findAll(Integer categoryId,String categoryIdMatch,Pageable pageable);
	
	@Query("Select p From Product p where (p.category.id = ?1 "
			+ "OR p.category.allParentIDs LIKE %?2%) AND "
			+ "(p.name LIKE %?3% "
			+ "OR p.shortDescription LIKE %?3% "
			+ "OR p.fullDescription LIKE %?3% "
			+ "OR p.brand.name LIKE %?3% "
			+ "OR p.category.name LIKE %?3%)")
	public Page<Product> searchInCategory(Integer categoryId,String categoryIdMatch,String keyword,Pageable pageable);
	
	@Query("Select p From Product p where p.name LIKE %?1%")
	public Page<Product> searchByProductName(String name,Pageable pageable);
	
	@Query("Update Product p SET p.averageRating = COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1), 0),"
			+ " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id =?1) "
			+ "WHERE p.id = ?1")
	@Modifying
	public void updateReviewCountAndAverageRating(Integer productId);
	                                                                                                                                                                  
	@Query("Update Product p SET p.answeredQuestionCount = (SELECT COUNT(q.id) FROM Question q WHERE q.product.id =?1 AND q.approved = true AND q.answer IS NOT NULL) "
			+ " WHERE p.id = ?1")
	@Modifying
	public void updateAnsweredQuestionCount(Integer productId);
	
}
