package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.admin.AbstractExporter;
import com.shopme.common.entity.Category;


public class CategoryCsvExporter extends AbstractExporter {
	
	public void export(List<Category> listCategory,HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv", "categories_");
		
		ICsvBeanWriter writer = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = {"Categories ID","Category Name"};
		String[] fieldMapping = {"id","name"};
		
		writer.writeHeader(csvHeader);
		
		for(Category category:listCategory) {
		  category.setName(category.getName().replace("--", " "));
		  writer.write(category, fieldMapping);
		}
		
		writer.close();
		
		
	}

}
