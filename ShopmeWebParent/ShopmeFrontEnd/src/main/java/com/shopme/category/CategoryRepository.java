package com.shopme.category;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Category;

public interface CategoryRepository extends CrudRepository<Category, Integer> {

	@Query("Select c From Category c Where c.enabled = True Order By c.name ASC")
	public List<Category> findAllEnabled();
	
	@Query("Select c From Category c Where c.enabled = True and c.alias = ?1")
	public Category findByAliasEnabled(String alias);
}
