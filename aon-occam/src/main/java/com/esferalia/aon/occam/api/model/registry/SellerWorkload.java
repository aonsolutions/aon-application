package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

import com.esferalia.aon.occam.api.model.project.ProjectHolder;

public class SellerWorkload extends Seller implements Serializable {

	private static final long serialVersionUID = -4021529272657422784L;
	
	TreeMap<Date, SellerWorkloadPeriod> periods;
	ProjectHolder projectHolder;
	
	public SellerWorkload() {
		super();
		periods = new TreeMap<Date, SellerWorkloadPeriod>();
	}

	public void addSellerWorkloadPeriod(Date date, Integer customers, Integer customersFees, Integer customersInvoices, Double netAmount, Double totalAmount) {
		SellerWorkloadPeriod sellerWorkloadPeriod = new SellerWorkloadPeriod()
				.setCustomers(customers)
				.setCustomerFees(customersFees)
				.setCustomerInvoices(customersInvoices)
				.setNetAmount(netAmount)
				.setTotalAmount(totalAmount);
		
		periods.put(date, sellerWorkloadPeriod);
	}
	
	public TreeMap<Date, SellerWorkloadPeriod> getPeriods(){
		return this.periods;
	}

	public ProjectHolder getProjectHolder() {
		return projectHolder;
	}

	public SellerWorkload setProjectHolder(ProjectHolder projectHolder) {
		this.projectHolder = projectHolder;
		return this;
	}
	
}
