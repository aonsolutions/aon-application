package com.code.aon.ui.employee.controller;

import com.code.aon.employee.SalaryDeduction;


public class SalaryDeductions {
	
	private SalaryDeduction commonContingency;
	private SalaryDeduction unemployment;
	private SalaryDeduction jobTraining;
	private SalaryDeduction structuralOvertime;
	private SalaryDeduction nonStructuralOvertime;
	private SalaryDeduction irpf;
	private SalaryDeduction advancePayment;
	private SalaryDeduction inKid;
	private SalaryDeduction other;

	public SalaryDeduction getCommonContingency() {
		return commonContingency;
	}
	public void setCommonContingency(SalaryDeduction commonContingency) {
		this.commonContingency = commonContingency;
	}
	public SalaryDeduction getUnemployment() {
		return unemployment;
	}
	public void setUnemployment(SalaryDeduction unemployment) {
		this.unemployment = unemployment;
	}
	public SalaryDeduction getJobTraining() {
		return jobTraining;
	}
	public void setJobTraining(SalaryDeduction jobTraining) {
		this.jobTraining = jobTraining;
	}
	public SalaryDeduction getStructuralOvertime() {
		return structuralOvertime;
	}
	public void setStructuralOvertime(SalaryDeduction structuralOvertime) {
		this.structuralOvertime = structuralOvertime;
	}
	public SalaryDeduction getNonStructuralOvertime() {
		return nonStructuralOvertime;
	}
	public void setNonStructuralOvertime(SalaryDeduction nonStructuralOvertime) {
		this.nonStructuralOvertime = nonStructuralOvertime;
	}
	public SalaryDeduction getIrpf() {
		return irpf;
	}
	public void setIrpf(SalaryDeduction irpf) {
		this.irpf = irpf;
	}
	public SalaryDeduction getAdvancePayment() {
		return advancePayment;
	}
	public void setAdvancePayment(SalaryDeduction advancePayment) {
		this.advancePayment = advancePayment;
	}
	public SalaryDeduction getInKid() {
		return inKid;
	}
	public void setInKid(SalaryDeduction inKid) {
		this.inKid = inKid;
	}
	public SalaryDeduction getOther() {
		return other;
	}
	public void setOther(SalaryDeduction other) {
		this.other = other;
	}
	public Double getAportationTotal() {
		double total = (commonContingency==null?0.0:commonContingency.getAmount()); 
		total += (unemployment==null?0.0:unemployment.getAmount());
		total += (jobTraining==null?0.0:jobTraining.getAmount());
		total += (structuralOvertime==null?0.0:structuralOvertime.getAmount());
		total += (nonStructuralOvertime==null?0.0:nonStructuralOvertime.getAmount());
		return total;  
	}
	
}
