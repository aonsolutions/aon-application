package com.esferalia.aon.salary.deduction;


import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.salary.enumeration.DeductionType;

public class Deductions {
	
	double total  ; 
	double socialSecurityContributions ; 
	
	private Map<DeductionType, IDeduction> map;

	public Deductions() {
		map = new HashMap<DeductionType, IDeduction>();	
	}
	
	public IDeduction getCommonContingency() {
		return map.get(DeductionType.COMMON_CONTINGENCY);
	}
	public void setCommonContingency(IDeduction d) {
		put(DeductionType.COMMON_CONTINGENCY, d);
	}


	public IDeduction getUnemployment() {
		return map.get(DeductionType.UNEMPLOYMENT);
	}
	public void setUnemployment(IDeduction d) {
		put(DeductionType.UNEMPLOYMENT, d);
	}
	
	public IDeduction getJobTraining() {
		return map.get(DeductionType.JOB_TRAINING);
	}
	public void setJobTraining(IDeduction d) {
		put(DeductionType.JOB_TRAINING, d);
	}
	
	public IDeduction getStructuralOvertime() {
		return map.get(DeductionType.STRUCTURAL_OVERTIME);
	}
	public void setStructuralOvertime(IDeduction d) {
		put(DeductionType.STRUCTURAL_OVERTIME, d);
	}
	
	public IDeduction getNonStructuralOvertime() {
		return map.get(DeductionType.NON_STRUCTURAL_OVERTIME);
	}
	public void setNonStructuralOvertime(IDeduction d) {
		put(DeductionType.NON_STRUCTURAL_OVERTIME, d);
	}
	
	public IDeduction getIrpf() {
		return map.get(DeductionType.IRPF);
	}
	public void setIrpf(IDeduction d) {
		put(DeductionType.IRPF, d);
	}
	
	public IDeduction getAdvancePayment() {
		return map.get(DeductionType.ADVANCE_PAYMENT);
	}
	public void setAdvancePayment(IDeduction d) {
		put(DeductionType.ADVANCE_PAYMENT, d);
	}
	
	public IDeduction getInKind() {
		return map.get(DeductionType.IN_KIND);
	}
	public void setInKind(IDeduction d) {
		put(DeductionType.IN_KIND, d);
	}

	public IDeduction getOther() {
		return map.get(DeductionType.OTHER);
	}
	public void setOther(IDeduction d) {
		put(DeductionType.OTHER, d);
	}

	private void put(DeductionType type, IDeduction d) {
		map.put(type,d);
	}

	public Double getTotal() {
		return this.total;  
	}
	
	public void setTotal(double total) {
		this.total = total;
	}

	public Double getSocialSecurityContributions() {
		return this.socialSecurityContributions;  
	}
	
	public void setSocialSecurityContributions(double socialSecurityContributions) {
		this.socialSecurityContributions = socialSecurityContributions;
	}

}
