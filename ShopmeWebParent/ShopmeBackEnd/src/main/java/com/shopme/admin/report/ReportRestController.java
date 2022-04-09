package com.shopme.admin.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportRestController {

	
	@Autowired
	private MasterOrderReportService service;
	
	@Autowired
	private OrderDetailReportService orderDetailReportService;
	
	@GetMapping("/reports/sales_by_date/{period}")
	public List<ReportItem> getReportDateByDatePeriod(@PathVariable("period") String period){
        
		switch(period) {
		case "last_7_days":
			return service.getReportDataLast7Days(ReportType.DAY);
		case "last_28_days":
		    return service.getReportDataLast28Days(ReportType.DAY);
		case "last_6_months":
			return service.getReportDataLast6Months(ReportType.MONTH);
		case "last_year":
			return service.getReportDataLastYear(ReportType.MONTH);
		 default:
			 return service.getReportDataLast7Days(ReportType.DAY);
		}
	}
	
	@GetMapping("/reports/sales_by_date/{startDate}/{endDate}")
	public List<ReportItem> getReportDateByDatePeriod(@PathVariable("startDate") String startDate,@PathVariable("endDate") String endDate) throws ParseException{
        
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startTime = dateFormatter.parse(startDate);
		System.out.println(startTime);
		Date endTime = dateFormatter.parse(endDate);
		System.out.println(endTime);
		return service.getReportDataByDateRange(startTime, endTime,ReportType.DAY);
	}
	
	@GetMapping("/reports/{groupBy}/{period}")
	public List<ReportItem> getReportDateByCategoryOrProduct(@PathVariable("groupBy") String groupBy,@PathVariable("period") String period) throws ParseException{
		
		ReportType reportType = ReportType.valueOf(groupBy.toUpperCase());
		
		switch(period) {
		case "last_7_days":
			return orderDetailReportService.getReportDataLast7Days(reportType);
		case "last_28_days":
		    return orderDetailReportService.getReportDataLast28Days(reportType);
		case "last_6_months":
			return orderDetailReportService.getReportDataLast6Months(reportType);
		case "last_year":
			return orderDetailReportService.getReportDataLastYear(reportType);
		 default:
			 return orderDetailReportService.getReportDataLast7Days(reportType);
		}
	}
	
	@GetMapping("/reports/{groupBy}/{startDate}/{endDate}")
	public List<ReportItem> getReportDataByCategoryOrProductDateRange(@PathVariable("groupBy") String groupBy,
			@PathVariable("startDate") String startDate,
			@PathVariable("endDate") String endDate) throws ParseException {
		
		ReportType reportType = ReportType.valueOf(groupBy.toUpperCase());
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startTime = dateFormatter.parse(startDate);
		Date endTime = dateFormatter.parse(endDate);

		return orderDetailReportService.getReportDataByDateRange(startTime, endTime, reportType);
	}
	
	
}
