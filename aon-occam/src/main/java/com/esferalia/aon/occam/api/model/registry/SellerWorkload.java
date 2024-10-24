package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

public class SellerWorkload extends Seller implements Serializable {

	private static final long serialVersionUID = -4021529272657422784L;
	
	TreeMap<Date, SellerWorkloadPeriod> periods;
	
	public SellerWorkload() {
		super();
		periods = new TreeMap<Date, SellerWorkloadPeriod>();
	}

	public void addSellerWorkloadPeriod(Date date, Integer customers, Integer customersFees, Double amount) {
		SellerWorkloadPeriod sellerWorkloadPeriod = new SellerWorkloadPeriod()
				.setCustomers(customers)
				.setCustomerFees(customersFees)
				.setAmount(amount);
		
		periods.put(date, sellerWorkloadPeriod);
	}
	
	public TreeMap<Date, SellerWorkloadPeriod> getPeriods(){
		return this.periods;
	}
	
}
