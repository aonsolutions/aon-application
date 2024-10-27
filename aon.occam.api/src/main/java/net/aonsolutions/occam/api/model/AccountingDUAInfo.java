package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.LinkedList;

public class AccountingDUAInfo implements Serializable {
	
	private static final long serialVersionUID = 5286532148184217686L;
	
	private Integer id;
	private Integer domain;
	private String code;
	private double price;
	private double adjust;
	private double statisticalValue;
	
	private Account dutyAccount;
	private double dutyBase;
	private double dutyPercent;
	private double dutyTotal;

	private Account vatAccount;
	private LinkedList<InvoiceDetail> duaDetails;
	
	private boolean authCalcEnabled;
	
	public Integer getId() {
		return id;
	}
	public AccountingDUAInfo setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public AccountingDUAInfo setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public String getCode() {
		return code;
	}
	public AccountingDUAInfo setCode(String code) {
		this.code = code;
		return this;
	}

	public double getPrice() {
		return price;
	}
	public AccountingDUAInfo setPrice(double price) {
		this.price = price;
		return this;
	}
	
	public double getAdjust() {
		return adjust;
	}
	public AccountingDUAInfo setAdjust(double adjust) {
		this.adjust = adjust;
		return this;
	}
	
	public double getStatisticalValue() {
		return statisticalValue;
	}
	public AccountingDUAInfo setStatisticalValue(double statisticalValue) {
		this.statisticalValue = statisticalValue;
		return this;
	}
	
	public Account getDutyAccount() {
		return dutyAccount;
	}
	public AccountingDUAInfo setDutyAccount(Account dutyAccount) {
		this.dutyAccount = dutyAccount;
		return this;
	}

	public double getDutyBase() {
		return dutyBase;
	}

	public AccountingDUAInfo setDutyBase(double dutyBase) {
		this.dutyBase = dutyBase;
		return this;
	}

	public double getDutyPercent() {
		return dutyPercent;
	}

	public AccountingDUAInfo setDutyPercent(double dutyPercent) {
		this.dutyPercent = dutyPercent;
		return this;
	}

	public double getDutyTotal() {
		return dutyTotal;
	}

	public AccountingDUAInfo setDutyTotal(double dutyTotal) {
		this.dutyTotal = dutyTotal;
		return this;
	}
	public Account getVatAccount() {
		return vatAccount;
	}

	public AccountingDUAInfo setVatAccount(Account vatAccount) {
		this.vatAccount = vatAccount;
		return this;
	}

	public boolean isAuthCalcEnabled() {
		return authCalcEnabled;
	}
	public AccountingDUAInfo setAuthCalcEnabled(boolean authCalcEnabled) {
		this.authCalcEnabled = authCalcEnabled;
		return this;
	}
	public LinkedList<InvoiceDetail> getDuaDetails() {
		return duaDetails;
	}
	public AccountingDUAInfo setDuaDetails(LinkedList<InvoiceDetail> duaVats) {
		this.duaDetails = duaVats;
		return this;
	}
}
