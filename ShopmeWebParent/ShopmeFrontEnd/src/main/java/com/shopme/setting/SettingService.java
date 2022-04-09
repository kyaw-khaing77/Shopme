package com.shopme.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Currency;
import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingCategory;

@Service
public class SettingService {
	
	@Autowired
	private SettingRepository repo;
	@Autowired
	private CurrencyRepository currencyRepo;
	
	public List<Setting> getGeneralSetting() {
				
		return repo.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
		
	}
	
	public EmailSettingBag getEmailSettingBag() {
		List<Setting> settings= repo.findByCategory(SettingCategory.MAIL_SERVER);
		settings.addAll(repo.findByCategory(SettingCategory.MAIL_TEMPLATES));
		return new EmailSettingBag(settings);
	}
	
	public CurrencySettingBag getCurrencySettings() {
		List<Setting> settings = repo.findByCategory(SettingCategory.CURRENCY);
		return new CurrencySettingBag(settings);
	}

	public PaymentSettingBag getPaymentSettings() {
		List<Setting> settings = repo.findByCategory(SettingCategory.PAYMENT);
		return new PaymentSettingBag(settings);
	}
	
	public String getCurrencyCode() {
		Setting setting = repo.findByKey("CURRENCY_ID");
		Integer currencyId = Integer.parseInt(setting.getValue());
	    Currency currency = currencyRepo.findById(currencyId).get();
	    return currency.getCode();
	}
}
