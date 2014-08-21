package com.code.aon.aio.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;

public class DashboardPygEntry implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String period;
	private Date date;
	private double income;
	private double purchase;
	private double expense;
	private double payrollExpense;
	private double amortization;
	private double amount;
	private byte type;
	
	private String[] seriesKeys;
	private double[] seriesValues;
	private List<Integer> types ;

	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getIncome() {
		return income;
	}
	public void setIncome(double income) {
		this.income = income;
	}
	public double getExpense() {
		return expense;
	}
	public void setExpense(double expense) {
		this.expense = expense;
	}
	public double getPayrollExpense() {
		return payrollExpense;
	}
	public void setPayrollExpense(double payrollExpense) {
		this.payrollExpense = payrollExpense;
	}
	public double getAmortization() {
		return amortization;
	}
	public void setAmortization(double amortization) {
		this.amortization = amortization;
	}
	public double getPurchase() {
		return purchase;
	}
	public void setPurchase(double purchase) {
		this.purchase = purchase;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getAmountC() {
		return type==0?amount:0.0;
	}
	public double getAmountP() {
		return type==1?amount:0.0;
	}
	
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public double getProfit() {
		return CommonUtil.round( getIncome() - getExpense() - getPurchase());
	}
	public String[] getSeriesKeys() {
		return seriesKeys;
	}
	public void setSeriesKeys(String[] seriesKeys) {
		this.seriesKeys = seriesKeys;
	}
	public double[] getSeriesValues() {
		return seriesValues;
	}
	public void setSeriesValues(double[] seriesValues) {
		this.seriesValues = seriesValues;
	}
	
	public List<Integer> getTypes() {
		return types;
	}
	
	public void setTypes(List<Integer> types) {
		this.types = types;
	}
	
}
