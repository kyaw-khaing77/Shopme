package com.shopme.cartItem;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.Utility;
import com.shopme.address.AddressService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import com.shopme.customer.CustomerService;
import com.shopme.shipping.ShippingRateService;

@Controller
public class CartItemController {

	@Autowired
	private CartItemService service;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private ShippingRateService shippingRateService;
	
	@Autowired
	private AddressService addressService;;
	
	@GetMapping("/cart")
	public String viewCart(Model model,HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		List<CartItem> cartItems = service.listCartItems(customer);

		
		float estimatedTotal = 0.0F;
		for(CartItem item : cartItems) {
			estimatedTotal += item.getSubTotal();
		}
		
		Address defaultAddress = addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;
		boolean usePrimaryAddressDefault = false;
		
		if(defaultAddress != null) {
			shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
		}else {
			shippingRate = shippingRateService.getShippingRateForCustomer(customer);
			usePrimaryAddressDefault = true;
		}
		
		model.addAttribute("usePrimaryAddressDefault", usePrimaryAddressDefault);		
		model.addAttribute("shippingSupported", shippingRate != null);
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("estimatedTotal", estimatedTotal);
		
		return "cart/shopping_cart";
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		return customerService.findCustomerByEmail(email);
	}
}
