package com.esferalia.aon.in.payroll.excel;

import java.util.Collection;
import java.util.Map;

import com.esferalia.aon.occam.api.model.Salary.Deduction;
import com.esferalia.aon.occam.api.model.Salary.Payment;

public class AggregatedAnnualEntry {
	private Double totalRaw;
	private Double totalDeduction;
	private Double totalLiquid;
	private Double extraProrration;
	private Double bonuses;
	private Double enterpriseSS;
	private Double enterpriseCost;
	private Double ccBase;
	private Double accBase;
	private Double moneyIrpfBase;
	private Double inKindIrpfBase;
	private Double totalIrpfBase;
	private String workplace;
	private Double rlc;
	private Double irpfCtaEsp;
	
	
	private Collection<Payment> payments;
	private Collection<Deduction> deductions;
	private Map<String, Double> daysAndHours;
	
	public Double getTotalRaw() {
		return totalRaw;
	}
	public void setTotalRaw(Double totalRaw) {
		this.totalRaw = totalRaw;
	}
	public Double getTotalDeduction() {
		return totalDeduction;
	}
	public void setTotalDeduction(Double totalDeduction) {
		this.totalDeduction = totalDeduction;
	}
	public Double getTotalLiquid() {
		return totalLiquid;
	}
	public void setTotalLiquid(Double totalLiquid) {
		this.totalLiquid = totalLiquid;
	}
	public Double getExtraProrration() {
		return extraProrration;
	}
	public void setExtraProrration(Double extraProrration) {
		this.extraProrration = extraProrration;
	}
	public Double getBonuses() {
		return bonuses;
	}
	public void setBonuses(Double bonuses) {
		this.bonuses = bonuses;
	}
	public Double getEnterpriseSS() {
		return enterpriseSS;
	}
	public void setEnterpriseSS(Double enterpriseSS) {
		this.enterpriseSS = enterpriseSS;
	}
	public Double getEnterpriseCost() {
		return enterpriseCost;
	}
	public void setEnterpriseCost(Double enterpriseCost) {
		this.enterpriseCost = enterpriseCost;
	}
	public Double getCcBase() {
		return ccBase;
	}
	public void setCcBase(Double ccBase) {
		this.ccBase = ccBase;
	}
	public Double getAccBase() {
		return accBase;
	}
	public void setAccBase(Double accBase) {
		this.accBase = accBase;
	}
	public Double getMoneyIrpfBase() {
		return moneyIrpfBase;
	}
	public void setMoneyIrpfBase(Double moneyIrpfBase) {
		this.moneyIrpfBase = moneyIrpfBase;
	}
	public Double getInKindIrpfBase() {
		return inKindIrpfBase;
	}
	public void setInKindIrpfBase(Double inKindIrpfBase) {
		this.inKindIrpfBase = inKindIrpfBase;
	}
	public Double getTotalIrpfBase() {
		return totalIrpfBase;
	}
	public void setTotalIrpfBase(Double irregularIrpfBase) {
		this.totalIrpfBase = irregularIrpfBase;
	}
	public Collection<Payment> getPayments() {
		return payments;
	}
	public void setPayments(Collection<Payment> payments) {
		this.payments = payments;
	}
	public Collection<Deduction> getDeductions() {
		return deductions;
	}
	public void setDeductions(Collection<Deduction> deductions) {
		this.deductions = deductions;
	}
	public Map<String, Double> getDaysAndHours() {
		return daysAndHours;
	}
	public void setDaysAndHours(Map<String, Double> daysAndHours) {
		this.daysAndHours = daysAndHours;
	}
	public String getWorkplace() {
		return workplace;
	}
	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}
	public Double getRlc() {
		return rlc;
	}
	public void setRlc(Double rlc) {
		this.rlc = rlc;
	}
	public Double getIrpfCtaEsp() {
		return irpfCtaEsp;
	}
	public void setIrpfCtaEsp(Double irpfCtaEsp) {
		this.irpfCtaEsp = irpfCtaEsp;
	}
	
}
