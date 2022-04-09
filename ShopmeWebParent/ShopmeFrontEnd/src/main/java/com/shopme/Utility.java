package com.shopme;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.shopme.security.oauth.CustomerOAuth2User;
import com.shopme.setting.CurrencySettingBag;
import com.shopme.setting.EmailSettingBag;

public class Utility {

	public static String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		
		return siteURL.replace(request.getServletPath(), "");
	}
	
	public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settings) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		
		mailSender.setHost(settings.getHost());
		mailSender.setPort(Integer.parseInt(settings.getPort()));
		mailSender.setUsername(settings.getUsername());
		mailSender.setPassword(settings.getPassword());
		
		Properties mailProperties = new Properties();
		mailProperties.setProperty("mail.smtp.auth", settings.getSmtpAuth());
		mailProperties.setProperty("mail.smtp.starttls.enable", settings.getSmtpSecured());
				
		mailSender.setJavaMailProperties(mailProperties);
		
		return mailSender;
	}
	
	public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		
		if(principal == null) return null;
		
		String customerEmail = null;
		if(principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
			customerEmail = request.getUserPrincipal().getName();
		}else if(principal instanceof OAuth2AuthenticationToken){
			OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) principal;
			CustomerOAuth2User oAuth2User= (CustomerOAuth2User) oAuth2AuthenticationToken.getPrincipal();
			customerEmail = oAuth2User.getEmail();
		}
		
		return customerEmail;
	}
	
	public static String formatCurrency(float amout,CurrencySettingBag settings) {
		String symbol = settings.getSymbol();
		String symbolPositon = settings.getSymbolPosition();
		String decimalPointType = settings.getDecimalPointType();
		String thousanPointType = settings.getThousandPointType();
		int decimalDigit = settings.getDecimalDigits();
		
		String pattern = symbolPositon.equals("before")?symbol:"";
		pattern += "###,###";
		if(decimalDigit > 0 ) {
			pattern += ".";
			for(int count = 1;count <= decimalDigit ;count++) {
				pattern += "#";
			}
		}
		
		pattern += symbolPositon.equals("after")?symbol:"";
		
		char thousandSeparator = thousanPointType.equals("POINT")?'.':',';
		char decimalSeparator = decimalPointType.equals("POINT")?'.':',';
		
		DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
		decimalFormatSymbols.setDecimalSeparator(decimalSeparator);
		decimalFormatSymbols.setGroupingSeparator(thousandSeparator);
		
		DecimalFormat formatter = new DecimalFormat(pattern, decimalFormatSymbols);
		
		return formatter.format(amout);

	}
}
