package com.shopme.admin.user.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.User;

@Controller
public class AccountController {
	
	@Autowired
	private UserService service;
	
	@GetMapping("/account")
	public String viewDetail(@AuthenticationPrincipal ShopmeUserDetails loggedUser,Model model) {
		String email = loggedUser.getUsername();
		User user = service.getByEmail(email);
		model.addAttribute("user", user);
		return "users/account_form";
		
	}
	
	@PostMapping("/account/update")
	public String saveDetails(User user,RedirectAttributes redirectAttributes,
			@RequestParam("image")MultipartFile multipartFile,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException {
		
	//	String filename = multipartFile.getOriginalFilename();
		if(!multipartFile.isEmpty()) {
			
			String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			
			user.setPhotos(filename);
			
			User saveduser = service.updateAccount(user);

			String uploadDir = "user-photos/"+saveduser.getId();		
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, filename, multipartFile);
			
		}else {
			if(user.getPhotos().isEmpty()) {
				user.setPhotos(null);
			}
			service.updateAccount(user);
		}
		
		loggedUser.setFirstName(user.getFirstName());
		loggedUser.setLastName(user.getLastName());
		
		redirectAttributes.addFlashAttribute("message", "User account details have been successfully updated.");
		
		return "redirect:/account";
	}


}
