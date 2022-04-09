package com.shopme.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Controller
public class ProductController {
	
	
	@Autowired
	private ProductService service;

	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/products")
	public String listFirstPage() {	
		return "redirect:/products/page/1?sortField=name&sortDir=asc";
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "listProducts",moduleURL = "/products") PagingAndSortingHelper helper,
			                 @PathVariable("pageNum") int pageNum,
			                 @Param("categoryId") Integer categoryId,
			                 Model model) {
		      		
		service.listAllByPage(pageNum,helper,categoryId);
		
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		if(categoryId != null) {
			model.addAttribute("categoryId", categoryId);
		}
		model.addAttribute("listCategories", listCategories);
	
		return "products/products";
		
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
 
		List<Brand> listBrands = brandService.listAll();
		
		Product product = new Product();
		
		product.setEnabled(true);
		product.setInStock(true);
		
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		model.addAttribute("numberOfExistExtraImages", 0);

		
		
		return "products/product_form";
	}
	
	@PostMapping("/products/save")
	public String saveProdct(Product product,
			@RequestParam(value = "fileImage",required = false) MultipartFile mainImageMultipart,
			@RequestParam(value = "extraImage",required = false) MultipartFile[] extraImageMultiparts,
			@RequestParam(name = "detailIDs",required = false) String[] detailIDs,
			@RequestParam(name = "detailNames",required = false) String[] detailNames,
			@RequestParam(name = "detailValues",required = false) String[] detailValues,
			@RequestParam(name = "imageIDs",required = false) String[] imageIDs,
			@RequestParam(name = "imageNames",required = false) String[] imageNames,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser,
			RedirectAttributes redirect)
			throws IOException {
		
		
		    if(!loggedUser.hasRole("Admin")&&  !loggedUser.hasRole("Editor")) {
		    	if(loggedUser.hasRole("Salesperson")) {
		    		service.saveProductPrice(product);
					redirect.addFlashAttribute("message", "The product has been saved successfully.");
					return "redirect:/products";	
		         }	    	
		    }
        
	    	ProductSaveHelper.setMainImageName(mainImageMultipart,product);
	    	ProductSaveHelper.setExistingExtraImageNames(imageIDs,imageNames,product);
	    	ProductSaveHelper.setNewExtraImageNames(extraImageMultiparts,product);
	    	ProductSaveHelper.setProductDetails(detailIDs,detailNames,detailValues,product);
            
	    	
			Product savedProduct = service.save(product);
			
			ProductSaveHelper.saveUploadedImages(mainImageMultipart,extraImageMultiparts,savedProduct);
			
			ProductSaveHelper.deleteExtraImagesWereRemovedOnForm(product);
		
		    redirect.addFlashAttribute("message", "The product has been saved successfully.");
		
		    return "redirect:/products";
	}
	

	
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable("id")Integer id,@PathVariable("status")boolean enabled,RedirectAttributes redirect) {

        service.updateProductEnabledStatus(id, enabled);		
        
        String status = enabled?"enabled":"disabled";
        
		redirect.addFlashAttribute("message", "The product ID "+id+" has been "+status);
		
		return "redirect:/products";
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable("id")Integer id,RedirectAttributes redirect) {

       try {
    	   service.deleteProduct(id);
  		   String productExtraImageDir = "../product-images/"+id+"/extras";
		   String productImageDir = "../product-images/"+id;

           FileUploadUtil.removeDir(productExtraImageDir);
           FileUploadUtil.removeDir(productImageDir);

           
   		   redirect.addFlashAttribute("message", "The product ID "+id+" has been deleted successfully");
    	   
       }catch (ProductNotFoundException e) {
    	   redirect.addFlashAttribute("message", e);
	}
	
		return "redirect:/products";
	}
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id,Model model,
			RedirectAttributes redirect,@AuthenticationPrincipal ShopmeUserDetails loggedUser) {
		try {
			Product product = service.get(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberOfExistExtraImages = product.getImages().size();
			
			boolean isReadOnlyForSalePerson = false;
			
		    if(!loggedUser.hasRole("Admin")&&  !loggedUser.hasRole("Editor")) {
		    	if(loggedUser.hasRole("Salesperson")) {
		    		isReadOnlyForSalePerson = true;
		         }	    	
		    }

			model.addAttribute("isReadOnlyForSalePerson", isReadOnlyForSalePerson);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("product", product);
			model.addAttribute("pageTitle", "Edit Product(ID : "+id +" )");
			model.addAttribute("numberOfExistExtraImages", numberOfExistExtraImages);
            
			
			return "products/product_form";
		}catch (ProductNotFoundException e) {
			redirect.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}
	
	@GetMapping("/products/detail/{id}")
	public String viewProductDetail(@PathVariable("id") Integer id,Model model,RedirectAttributes redirect) {
		try {
			Product product = service.get(id);			
			model.addAttribute("product", product);
		
			return "products/product_detail_modal";
		}catch (ProductNotFoundException e) {
			redirect.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}


}
