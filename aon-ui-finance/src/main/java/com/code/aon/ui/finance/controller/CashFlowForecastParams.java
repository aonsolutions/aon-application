package com.code.aon.ui.finance.controller;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.common.AonVersion;
import com.code.aon.registry.RegistryBank;

public class CashFlowForecastParams implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Date date;
	private Date startDate;
	private Date dueDate;
	private String description;
	private Double amount;
	private Integer paymentDay;
	private RegistryBank registryBank;

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	public Integer getPaymentDay() {
		return paymentDay;
	}
	public void setPaymentDay(Integer paymentDay) {
		this.paymentDay = paymentDay;
	}
	
	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}
	
	public void initialize() {
		setDate(null);
		setStartDate(null);
		setDueDate(null);
		setDescription( null );
		setAmount(null);
		setPaymentDay(null);
		setRegistryBank(null);
	}

	
}
