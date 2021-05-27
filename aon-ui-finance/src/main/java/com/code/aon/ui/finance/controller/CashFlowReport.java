/**
 * 
 */
package com.code.aon.ui.finance.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.code.aon.AonVersion;

public class CashFlowReport implements Comparable<CashFlowReport>, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static final int BEFORE = -1;
	private static final int EQUAL = 0;
	private static final int AFTER = 1;
	
	private Integer id; // ID del vtos. o de la previsión.
	private Date date;
	private String type;
	private String description;
	private String bankDescription;
	private boolean payment;
	private boolean systemProperty;
	private boolean disabled;
	private boolean undated;
	private Map<Integer,CashFlowBank> map;
	private double  amount;
	private double total;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getBankDescription() {
		return bankDescription;
	}
	public void setBankDescription(String bankDescription) {
		this.bankDescription = bankDescription;
	}

	public boolean isPayment() {
		return payment;
	}
	public void setPayment(boolean payment) {
		this.payment = payment;
	}
	
	public boolean isSystemProperty() {
		return systemProperty;
	}
	public void setSystemProperty(boolean systemProperty) {
		this.systemProperty = systemProperty;
	}
	
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public boolean isUndated() {
		return undated;
	}
	public void setUndated(boolean undated) {
		this.undated = undated;
	}

	public Map<Integer, CashFlowBank> getMap() {
		return map;
	}
	public void setMap(Map<Integer, CashFlowBank> map) {
		this.map = map;
	}
	public boolean isPrevision() {
		return ("Pr.".equals(getType()));
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public CashFlowReport getTo() {
		return this;
	}
	public double getBalance(Integer id) {
		if (getMap().containsKey(id)) {
			return getMap().get(id).getBalance();	
		}
		return 0.0;
	}
	
	@Override
	public int compareTo(CashFlowReport cfr) {
		
		if (this == cfr) return EQUAL;
		
		if (cfr == null) {
			return BEFORE;	
		}
		if (getDate() == null && cfr.getDate() == null) {
			return EQUAL;
		}
		if (getDate() == null) {
			return BEFORE;
		}
		if (cfr.getDate() == null) {
			return AFTER;	
		}
		return getDate().compareTo(cfr.getDate());
	}
}