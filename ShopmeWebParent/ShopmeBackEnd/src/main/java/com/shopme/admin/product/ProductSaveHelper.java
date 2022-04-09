package com.shopme.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.product.ProductImage;

public class ProductSaveHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);

	static void deleteExtraImagesWereRemovedOnForm(Product product) {
        String extraImageDir = "../product-images/"+product.getId()+"/extras";
        Path path = Paths.get(extraImageDir);
        
        try {
      	  Files.list(path).forEach(file ->{
      		  String filename = file.toFile().getName();
      		  
      		  if(!product.containsImageName(filename)) {
      			  try {
      				  Files.delete(file);
      				  LOGGER.info("Deleted extra image: "+filename);
      			  }catch (IOException e) {
						LOGGER.error("Could not delete extra image: "+filename);
					}
      		  }
      		  
      	  });
        }catch (IOException e) {
			LOGGER.error("Could not list directory: "+path);
		}
	}

	static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {

		if(imageIDs == null || imageIDs.length == 0) return;
		
		Set<ProductImage> images = new HashSet<>();
		
		for(int count = 0; count < imageIDs.length ; count++) {
			Integer id = Integer.parseInt(imageIDs[count]);
			String name = imageNames[count];
			images.add(new ProductImage(id, name, product));
			
		}
		product.setImages(images);
		
	}

	static void setProductDetails(String[] detailIDs,String[] detailNames, String[] detailValues, Product product) {
		if(detailNames == null || detailNames.length == 0) return;
		
		for(int count=0;count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];
			Integer id = Integer.parseInt(detailIDs[count]);
			if(id != 0 ) {
				product.addDetail(id, name,value);
			}else if(!name.isEmpty() && !value.isEmpty()) {
				product.addDetail(name, value);
			}
			
			
		}
		
	}

	static void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultiparts,
			Product savedProduct) throws IOException {
		if(!mainImageMultipart.isEmpty()) {
			String filename = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());

			String uploadDir = "../product-images/"+savedProduct.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, filename, mainImageMultipart);	
		}
		
		if(extraImageMultiparts.length > 0) {
		 String productExtraImageDir = "../product-images/"+savedProduct.getId()+"/extras";
     	 for(MultipartFile multipartFile:extraImageMultiparts) {
     		 if(multipartFile.isEmpty()) continue;
     		 
     		 String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			 FileUploadUtil.saveFile(productExtraImageDir, filename, multipartFile);	

     	    }
      }
	}

	static void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
       if(extraImageMultiparts.length > 0) {
      	 for(MultipartFile multipartFile:extraImageMultiparts) {
      		 if(!multipartFile.isEmpty()) {
      				String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
      				if(!product.containsImageName(filename)) {
          				product.addExtraImage(filename);
      				}
      			}
      	 }
       }
       
	}

	static void setMainImageName(MultipartFile mainImageMultipart,Product product) {
		if(!mainImageMultipart.isEmpty()) {
			String filename = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			product.setMainImage(filename);
		}
	}
	
	
}