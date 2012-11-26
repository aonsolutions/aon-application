package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.esferalia.aon.entity.master.SalaryDeductionDB;
import com.esferalia.aon.salary.deduction.IDeduction;

@Entity
@Table(name="salary_deduction")
public class SalaryDeduction extends SalaryDeductionDB implements IDeduction {

	private static final long serialVersionUID = 1L;
	@Override
	@Transient
	public String getName() {
		return getDeductionConcept();
	}

}
