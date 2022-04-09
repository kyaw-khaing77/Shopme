package com.shopme.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.product.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

	@Query("Select p From Product p Where p.enabled = true "
			+ "And (p.category.id = ?1 OR p.category.allParentIDs Like %?2%)"
			+ "Order by p.name ASC")
	public Page<Product> listByCategory(Integer categoryId,String categoryIDMatch,Pageable page);
	
	public Product findByAlias(String alias);
	
	@Query(value = "Select * from products where enabled = true And "
			+ "Match(name,short_description,full_description) Against(?1)",
			nativeQuery = true)
	public Page<Product> search(String keyword,Pageable pageable);
	
	@Query("Update Product p SET p.averageRating = COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1), 0),"
			+ " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id =?1) "
			+ "WHERE p.id = ?1")
	@Modifying
	public void updateReviewCountAndAverageRating(Integer productId);

}
