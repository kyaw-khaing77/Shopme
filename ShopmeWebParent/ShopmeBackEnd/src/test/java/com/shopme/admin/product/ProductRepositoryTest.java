package com.shopme.admin.product;

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

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.product.Product;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTest {
	
	@Autowired
	private ProductRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void createProduct() {
		Brand brand = entityManager.find(Brand.class, 37);
		Category category = entityManager.find(Category.class, 5);
		
		Product product = new Product();
		product.setName("Acer Inspiron 3000");
		product.setAlias("acer_inspiron_3000");
		product.setShortDescription("short desctption");
		product.setFullDescription("full description");
		
		product.setBrand(brand);
		product.setCategory(category);
		
		product.setPrice(678);
		product.setCost(600);
		product.setEnabled(true);
		product.setInStock(true);
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testListAllProduct() {
		Iterable<Product> listProducts = repo.findAll();
		
		listProducts.forEach(product -> System.out.println(product));
	}
	
	@Test
	public void testGetProduct() {
		Product product = repo.findById(2).get();
		System.out.println(product);
		assertThat(product).isNotNull();
	}
	
	@Test 
	public void testUpdateProduct() {
		Product product = repo.findById(1).get();
		System.out.println(product);
		
		Product updateProduct = entityManager.find(Product.class, 1);
		assertThat(updateProduct.getPrice()).isEqualTo(499);
	}
	
	@Test
	public void testDeleteProduct() {
		repo.deleteById(3);
		
		Optional<Product> result = repo.findById(3);
		
		assertThat(!result.isPresent());
	}
	
	@Test
	public void testSaveProductWithImages() {
		Integer productId = 1;
		Product product = repo.findById(productId).get();
		
		product.setMainImage("main image.jpg");
		product.addExtraImage("extra image1.jpg");
		product.addExtraImage("extra image2.jpg");
		product.addExtraImage("extra image3.jpg");
		
		Product savedProduct = repo.save(product);
        assertThat(savedProduct.getImages()).size().isEqualTo(3);
	}
	
	@Test
	public void testSaveProductWithDetails() {
		Integer productId = 1;
		Product product = repo.findById(productId).get();
		
		product.addDetail("Device Memory", "128 GB");
		product.addDetail("CPU Model", "MediaTek");
		product.addDetail("OS", "Andriod 10");

		
		Product savedProduct = repo.save(product);
        assertThat(savedProduct.getDetails()).isNotEmpty();
	}
	
	@Test
	public void testUpdateReviewCountAndAverageRating() {
		Integer productId = 7;
		repo.updateReviewCountAndAverageRating(productId);
	}
	
	@Test
	public void testUpdateApprovedQuestionCount() {
		Integer productId = 5;
		repo.updateAnsweredQuestionCount(productId);

	}
	 
}
