package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.SalaryDeductionDB;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;

@Entity
@Table(name="salary_deduction")
public class SalaryDeduction extends SalaryDeductionDB implements IDeduction {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	@Override
	@Transient
	public String getName() {
		return getDeductionConcept();
	}
	
	// TODO 
	private DeductionType deductionType;
	
	@Transient
	public DeductionType getDeductionType() {
		if(this.getType()!=null){
			deductionType = this.getType();
		}
		return deductionType;
	}
	public void setDeductionType(DeductionType deductionType) {
		this.deductionType = deductionType;
		this.setType(deductionType);
	}

}
