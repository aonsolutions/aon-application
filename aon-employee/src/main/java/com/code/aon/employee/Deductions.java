package com.code.aon.employee;

import java.util.Collection;

import com.code.aon.employee.enumeration.DeductionType;



public class Deductions {
	
	private IDeduction commonContingency;
	private IDeduction unemployment;
	private IDeduction jobTraining;
	private IDeduction structuralOvertime;
	private IDeduction nonStructuralOvertime;
	private IDeduction irpf;
	private IDeduction advancePayment;
	private IDeduction inKind;
	private IDeduction other;

	
	public Deductions() {
		
	}
	public Deductions(Collection<SalaryDeduction> deductions) {
		for(SalaryDeduction sd: deductions){
			if (sd.getType() == DeductionType.COMMON_CONTINGENCY) {
				setCommonContingency(sd);
			} else if (sd.getType() == DeductionType.UNEMPLOYMENT) {
				setUnemployment(sd);
			} else if (sd.getType() == DeductionType.JOB_TRAINING) {
				setJobTraining(sd);
			} else if (sd.getType() == DeductionType.STRUCTURAL_OVERTIME) {
				setStructuralOvertime(sd);
			} else if (sd.getType() == DeductionType.NON_STRUCTURAL_OVERTIME) {
				setNonStructuralOvertime(sd);
			} else if (sd.getType() == DeductionType.IRPF) {
				setIrpf(sd);
			} else if (sd.getType() == DeductionType.ADVANCE_PAYMENT) {
				setAdvancePayment(sd);
			} else if (sd.getType() == DeductionType.IN_KIND) {
				setInKind(sd);
			} else if (sd.getType() == DeductionType.OTHER) {
				setOther(sd);
			}
		}
	}
	
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
	public IDeduction getInKind() {
		return inKind;
	}
	public void setInKind(IDeduction inKind) {
		this.inKind= inKind;
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
