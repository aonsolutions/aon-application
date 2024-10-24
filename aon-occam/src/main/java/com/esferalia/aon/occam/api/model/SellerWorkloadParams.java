package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class SellerWorkloadParams extends SellerParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Byte customers;
	private Byte period; // 0 == Mes acutal, 1 == 2 Meses, 2 == 3 Meses
	
	public SellerWorkloadParams() {
		super();
	}

	public Byte getCustomers() {
		return customers;
	}

	public SellerWorkloadParams setCustomers(Byte customers) {
		this.customers = customers;
		return this;
	}
	
	public Byte getPeriod() {
		return period;
	}

	public SellerWorkloadParams setPeriod(Byte period) {
		this.period = period;
		return this;
	}
	
}
