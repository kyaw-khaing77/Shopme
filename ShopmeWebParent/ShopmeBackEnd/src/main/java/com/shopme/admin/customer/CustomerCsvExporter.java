package com.shopme.admin.customer;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.admin.AbstractExporter;
import com.shopme.common.entity.Customer;

public class CustomerCsvExporter extends AbstractExporter {

	public void export(List<Customer> customerLists , HttpServletResponse response) throws IOException {
		
		super.setResponseHeader(response, "text/csv", ".csv", "customer_");
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		
		String[] csvHeader = {"Customer ID","E-mail","First Name","Last Name","Phone Number","Address Line 1","Address Line 2","City","State","Postal Code"};
		String[] fieldMapper = {"id","email","firstName","lastName","phoneNumber","addressLine1","addressLine2","city","state","postalCode"};
		
        csvWriter.writeHeader(csvHeader);
		
		for(Customer customer:customerLists) {
			csvWriter.write(customer, fieldMapper);
		}
		
		csvWriter.close();
	}
}
