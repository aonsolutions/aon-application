package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class EmployeeIrpf implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private boolean isNew;
	private boolean isDelete;
	private Integer salaryId;
	private String salaryType;
	private Date date;
	private Double moneyBase;
	private Double moneyQuote;
	private Double inkindBase;
	private Double inkindQuote;
	private Double irpfPercent;
	private Double employeeSSQuote;
	private Double totalIrpf;
	
	public EmployeeIrpf() {
		this.isNew = false;
		this.isDelete = false;
	}

	public boolean isNew() {
		return isNew;
	}

	public EmployeeIrpf setNew(boolean isNew) {
		this.isNew = isNew;
		return this;
	}
	
	public boolean isDelete() {
		return isDelete;
	}

	public EmployeeIrpf setDelete(boolean isDelete) {
		this.isDelete = isDelete;
		return this;
	}
	
	public Integer getSalaryId() {
		return salaryId;
	}

	public EmployeeIrpf setSalaryId(Integer salaryId) {
		this.salaryId = salaryId;
		return this;
	}

	public String getSalaryType() {
		return salaryType;
	}

	public EmployeeIrpf setSalaryType(String salaryType) {
		this.salaryType = salaryType;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public EmployeeIrpf setDate(Date date) {
		this.date = date;
		return this;
	}

	public Double getMoneyBase() {
		if(moneyBase != null && moneyBase <= 0.01) return 0.00;
		return null != moneyBase ? round(moneyBase, 2) : moneyBase;
	}

	public EmployeeIrpf setMoneyBase(Double moneyBase) {
		this.moneyBase = moneyBase;
		return this;
	}

//	public Double getMoneyPercent() {
//		return null != moneyPercent ? round(moneyPercent, 2) : moneyPercent;
//	}
//
//	public EmployeeIrpf setMoneyPercent(Double moneyPercent) {
//		this.moneyPercent = moneyPercent;
//		return this;
//	}

	public Double getMoneyQuote() {
		if(moneyQuote != null && moneyQuote <= 0.01) return 0.00;
		return null != moneyQuote ? round(moneyQuote, 2) : moneyQuote;
	}

	public EmployeeIrpf setMoneyQuote(Double moneyQuote) {
		this.moneyQuote = moneyQuote;
		return this;
	}

	public Double getInkindBase() {
		if(inkindBase != null && inkindBase <= 0.01) return 0.00;
		return null != inkindBase ? round(inkindBase, 2) : inkindBase;
	}

	public EmployeeIrpf setInkindBase(Double inkindBase) {
		this.inkindBase = inkindBase;
		return this;
	}

//	public Double getInkindPercent() {
//		return null != inkindPercent ? round(inkindPercent, 2) : inkindPercent;
//	}
//
//	public EmployeeIrpf setInkindPercent(Double inkindPercent) {
//		this.inkindPercent = inkindPercent;
//		return this;
//	}

	public Double getInkindQuote() {
		if(inkindQuote != null && inkindQuote <= 0.01) return 0.00;
		return null != inkindQuote ? round(inkindQuote, 2) : inkindQuote;
	}

	public EmployeeIrpf setInkindQuote(Double inkindQuote) {
		this.inkindQuote = inkindQuote;
		return this;
	}
	
	public Double getIrpfPercent() {
		return irpfPercent;
	}

	public EmployeeIrpf setIrpfPercent(Double irpfPercent) {
		this.irpfPercent = irpfPercent;
		return this;
	}

	public Double getEmployeeSSQuote() {
		if(employeeSSQuote != null && employeeSSQuote <= 0.01) return 0.00;
		return null != employeeSSQuote ? round(employeeSSQuote, 2) : employeeSSQuote;
	}

	public EmployeeIrpf setEmployeeSSQuote(Double employeeSSQuote) {
		this.employeeSSQuote = employeeSSQuote;
		return this;
	}

	public Double getTotalIrpf() {
		if(totalIrpf != null && totalIrpf <= 0.01) return 0.00;
		return null != totalIrpf ? round(totalIrpf, 2) : totalIrpf;
	}

	public EmployeeIrpf setTotalIrpf(Double totalIrpf) {
		this.totalIrpf = totalIrpf;
		return this;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}

	@Override
	public String toString() {
		return "EmployeeIrpf [isNew=" + isNew + ", isDelete=" + isDelete + ", salaryId=" + salaryId + ", salaryType="
				+ salaryType + ", date=" + date + ", moneyBase=" + moneyBase + ", moneyQuote=" + moneyQuote
				+ ", inkindBase=" + inkindBase + ", inkindQuote=" + inkindQuote + ", irpfPercent=" + irpfPercent
				+ ", employeeSSQuote=" + employeeSSQuote + ", totalIrpf=" + totalIrpf + "]";
	}
	
}
