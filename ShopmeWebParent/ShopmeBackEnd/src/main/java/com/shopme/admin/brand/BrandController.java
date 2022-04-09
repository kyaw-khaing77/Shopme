package com.shopme.admin.brand;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.exception.BrandNotFoundException;

@Controller
public class BrandController {
	
	@Autowired
	private BrandService service;
	
	@Autowired
	private CategoryService catService;
	
	@GetMapping("/brands")
	public String listAll() {	
		return "redirect:/brands/page/1?sortField=name&sortDir=asc";	
	}
	
	@GetMapping("/brands/page/{pageNum}")
	public String listAllByPage(@PagingAndSortingParam(listName = "listBrands",moduleURL = "/brands") PagingAndSortingHelper helper,
			                    @PathVariable("pageNum") int pageNum ) {	
		service.listAllByPage(pageNum, helper);	
		return "brands/brands";
		
	}

	@GetMapping("/brands/new")
	public String newBrand(Model model) {
	    List<Category> listCategories = catService.listCategoriesUsedInForm();
	    Brand brand = new Brand();
	    
	    model.addAttribute("brand", brand);
	    model.addAttribute("listCategories", listCategories);
	    model.addAttribute("pageTitle", "Create New Brand");
		
		return "brands/brand_form";
	}
		
	@PostMapping("/brands/save")
	public String saveBrand(Brand brand,@RequestParam("fileImage") MultipartFile multipartFile,RedirectAttributes redirect) throws IOException {
		
		if(!multipartFile.isEmpty()) {
			String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(filename);
			Brand savedBrand = service.save(brand);
			String uploadDir = "../brand-logos/"+savedBrand.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, filename, multipartFile);
			
			
		}else {
			service.save(brand);
		}
		
		
		redirect.addFlashAttribute("message", "The brand has been saved successfully!");
		
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/edit/{id}")
	public String editBrand(@PathVariable("id")Integer id,Model model,RedirectAttributes redirect) {
		try {
			Brand brand = service.get(id);
		    List<Category> listCategories = catService.listCategoriesUsedInForm();

			model.addAttribute("brand", brand);
		    model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "Edit Brand(ID: "+id+" )");
			return "brands/brand_form";
		}catch(BrandNotFoundException e) {
			redirect.addFlashAttribute("message", e.getMessage());
			return "redirect:/brands";
		}
	}
	
	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable("id")Integer id,RedirectAttributes redirect) {
		try {
			service.delete(id);
			String brandDir = "../brand-logos/"+id;
			FileUploadUtil.removeDir(brandDir);
			redirect.addFlashAttribute("message", "The brand ID "+id+" has been deleted successfully.");
			
		}catch(BrandNotFoundException e) {
			redirect.addFlashAttribute("message", e.getMessage());
		}
		
		return "redirect:/brands";
	}
}
