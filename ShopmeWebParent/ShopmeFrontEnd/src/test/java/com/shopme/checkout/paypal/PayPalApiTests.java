package com.shopme.checkout.paypal;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class PayPalApiTests {
	
	private static final String BASE_URL = "https://api.sandbox.paypal.com";
	private static final String GET_ORDER_API = "/v2/checkout/orders/";
	private static final String CLIENT_ID = "AeYFIl1px2124EZhnMTDmkauRvcGm55yMGkpQYLNsTQZqNLDW9U5U8J86qKpSNpPN-vvlGIjYwMG40L-";
	private static final String CLIENT_SECRET = "EDNUaGT4_mBVD2g5GbP_L025yePzqkYEwIglJCBmNS5-YPusTB-0fa752bXFaUz122hNSMWRfOU7IOCx";

	@Test
	public void testGetOrderDetails() {
		String orderId = "2G414052SV394161X";
		String requestURL = BASE_URL + GET_ORDER_API + orderId;

		//GetWithHeader
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Accept-Language", "en_US");
		headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

		// HttpEntity<String>: To get result as multiValueMap
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();
		
		// Send request with GET method, and Headers.
		/*
		 * ResponseEntity<String> response = restTemplate.exchange(requestURL, //
		 * HttpMethod.GET, request, String.class);
		 * 
		 * String result = response.getBody();
		 * 
		 * System.out.println(result);
		 */

		//Use exchange method also helps you to be able send a request to the Restful Service.
		//The result returned is ResponseEntity object. This object contains a lot of noteworthy information
		
		
		ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(requestURL, HttpMethod.GET, request,
				                                                              PayPalOrderResponse.class);
		PayPalOrderResponse orderResponse = response.getBody();
		
		

		System.out.println("Order ID: " + orderResponse.getId());
		System.out.println("Validated: " + orderResponse.validate(orderId));

	}
}
