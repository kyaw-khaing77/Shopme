package com.shopme.admin.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.order.OrderDetailRepository;
import com.shopme.common.entity.order.OrderDetail;

@Service
public class OrderDetailReportService extends AbstractReportService {
	
	@Autowired
	private OrderDetailRepository repo;

	@Override
	protected List<ReportItem> getReportDataByDateRangeInternal(Date startDate, Date endDate, ReportType reportType) {
		List<OrderDetail> listDetails = null;
		if(reportType.equals(ReportType.CATEGORY)) {
			listDetails = repo.findWithCategoryAndTimeBetween(startDate, endDate);
		}else if(reportType.equals(ReportType.PRODUCT)) {
			listDetails = repo.findWithProductAndTimeBetween(startDate, endDate);
		}
		//printRawData(listDetails);
		
		List<ReportItem> listReportItems=createReportData(listDetails,reportType);
		
		return listReportItems;
	}

	private List<ReportItem> createReportData(List<OrderDetail> listDetails, ReportType reportType) {
		List<ReportItem> listReportItems = new ArrayList<>();
		
		for(OrderDetail orderDetail : listDetails) {
			String identifier = "";
			if(reportType.equals(ReportType.CATEGORY)) {
				identifier = orderDetail.getProduct().getCategory().getName();
			}else if(reportType.equals(ReportType.PRODUCT)) {
				identifier = orderDetail.getProduct().getShortName();
			}
			
			ReportItem reportItem = new ReportItem(identifier);
			
			float grossSales = orderDetail.getSubtotal() + orderDetail.getShippingCost();
			float netSales = orderDetail.getSubtotal() - orderDetail.getProductCost();
			
			int itemIndex = listReportItems.indexOf(reportItem);
			if(itemIndex >= 0) {
				reportItem = listReportItems.get(itemIndex);
				reportItem.addGrossSales(grossSales);
				reportItem.addNetSales(netSales);
				reportItem.incraseProductsCount(orderDetail.getQuantity());
			}else {
				listReportItems.add(new ReportItem(identifier,grossSales,netSales,orderDetail.getQuantity()));
			}
		}
		
		//printReportData(listReportItems);
		return listReportItems;
	}

	private void printReportData(List<ReportItem> listReportItems) {
		for(ReportItem item : listReportItems) {
			System.out.printf("%-20s, %10.2f, %10.2f, %d \n ",
					item.getIdentifier(),item.getGrossSales(),
					item.getNetSales(),item.getProductsCount());
		}
		
	}

	private void printRawData(List<OrderDetail> listDetails) {
		for(OrderDetail detail : listDetails) {
			System.out.printf("%d, %-20s, %10.2f, %10.2f, %10.2f \n ",
					detail.getQuantity(),detail.getProduct().getShortName().substring(0, 20),
					detail.getSubtotal(),detail.getProductCost(),detail.getShippingCost());
		}
		
	}

}
