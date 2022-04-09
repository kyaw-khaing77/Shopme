package com.shopme.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Brand;
import com.shopme.common.exception.BrandNotFoundException;

@Service
public class BrandService {

	@Autowired
	private BrandRepository repo;
	
	public static final int BRANDS_PER_PAGE = 10;
	
	public List<Brand> listAll(){
		return (List<Brand>)repo.findAll();
	}
	
	public void listAllByPage(int pageNum,PagingAndSortingHelper helper){
		helper.listEntities(pageNum, BRANDS_PER_PAGE, repo);
	}
	
	public Brand save(Brand brand){
		return repo.save(brand);
	}
	
	public Brand get(Integer id) throws BrandNotFoundException {
		try {
			Brand brand = repo.findById(id).get();
			return brand;
		}catch (NoSuchElementException e) {
            throw new BrandNotFoundException("Could not find any Category with ID "+id);
		}
	}
	
	public void delete(Integer id) throws BrandNotFoundException {
		Long countById = repo.countById(id);
		if(countById == null || countById == 0) {
			throw new BrandNotFoundException("Count not find any brand with Id"+ id);
		}
		
		repo.deleteById(id);
	}
	
	public String checkUnique(Integer id,String name) {
		Boolean isCreatingNew =(id == null || id == 0 );
		Brand brand = repo.findByName(name);
		if(isCreatingNew) {
			if(brand!= null) {
				return "Duplicate";
			}
		}else {
			if(id != brand.getId() && brand != null ) {
				return "Duplicate";
			}
		}
		
		return "OK";
	}
	
	
}
