package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

public class SellerWorkloadParams extends SellerParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Byte customers;
	private Byte period;
	private Date periodStart;
	private Date periodEnd;
	private Integer seller;
	private Integer taskHolder;
	
	private Boolean customerActive;
	private Boolean customerInactive;
	private Boolean customerBlocked;
	
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

	public Date getPeriodStart() {
		return periodStart;
	}

	public SellerWorkloadParams setPeriodStart(Date periodStart) {
		this.periodStart = periodStart;
		return this;
	}

	public Date getPeriodEnd() {
		return periodEnd;
	}

	public SellerWorkloadParams setPeriodEnd(Date periodEnd) {
		this.periodEnd = periodEnd;
		return this;
	}
	
	public Integer getSeller() {
		return seller;
	}

	public SellerWorkloadParams setSeller(Integer seller) {
		this.seller = seller;
		return this;
	}
	
	public Integer getTaskHolder() {
		return taskHolder;
	}

	public SellerWorkloadParams setTaskHolder(Integer taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}

	public Boolean getCustomerActive() {
		return customerActive;
	}

	public SellerWorkloadParams setCustomerActive(Boolean customerActive) {
		this.customerActive = customerActive;
		return this;
	}

	public Boolean getCustomerInactive() {
		return customerInactive;
	}

	public SellerWorkloadParams setCustomerInactive(Boolean customerInactive) {
		this.customerInactive = customerInactive;
		return this;
	}

	public Boolean getCustomerBlocked() {
		return customerBlocked;
	}

	public SellerWorkloadParams setCustomerBlocked(Boolean customerBlocked) {
		this.customerBlocked = customerBlocked;
		return this;
	}
	
	@Override
	public String toString() {
		return "SellerWorkloadParams [customers=" + customers + ", period=" + period + ", periodStart=" + periodStart
				+ ", periodEnd=" + periodEnd + ", seller=" + seller + ", taskHolder=" + taskHolder
				+ ", customerActive=" + customerActive + ", customerInactive=" + customerInactive
				+ ", customerBlocked=" + customerBlocked + "]";
	}
	
	
}
