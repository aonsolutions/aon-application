package com.code.aon.employee;



public class Deductions {
	
	private IDeduction commonContingency;
	private IDeduction unemployment;
	private IDeduction jobTraining;
	private IDeduction structuralOvertime;
	private IDeduction nonStructuralOvertime;
	private IDeduction irpf;
	private IDeduction advancePayment;
	private IDeduction inKid;
	private IDeduction other;

	public IDeduction getCommonContingency() {
		return commonContingency;
	}
	public void setCommonContingency(IDeduction commonContingency) {
		this.commonContingency = commonContingency;
	}
	public IDeduction getUnemployment() {
		return unemployment;
	}
	public void setUnemployment(IDeduction unemployment) {
		this.unemployment = unemployment;
	}
	public IDeduction getJobTraining() {
		return jobTraining;
	}
	public void setJobTraining(IDeduction jobTraining) {
		this.jobTraining = jobTraining;
	}
	public IDeduction getStructuralOvertime() {
		return structuralOvertime;
	}
	public void setStructuralOvertime(IDeduction structuralOvertime) {
		this.structuralOvertime = structuralOvertime;
	}
	public IDeduction getNonStructuralOvertime() {
		return nonStructuralOvertime;
	}
	public void setNonStructuralOvertime(IDeduction nonStructuralOvertime) {
		this.nonStructuralOvertime = nonStructuralOvertime;
	}
	public IDeduction getIrpf() {
		return irpf;
	}
	public void setIrpf(IDeduction irpf) {
		this.irpf = irpf;
	}
	public IDeduction getAdvancePayment() {
		return advancePayment;
	}
	public void setAdvancePayment(IDeduction advancePayment) {
		this.advancePayment = advancePayment;
	}
	public IDeduction getInKid() {
		return inKid;
	}
	public void setInKid(IDeduction inKid) {
		this.inKid = inKid;
	}
	public IDeduction getOther() {
		return other;
	}
	public void setOther(IDeduction other) {
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
