package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.SalaryCostDB;
import com.esferalia.aon.salary.deduction.IDeduction;

@Entity
@Table(name="salary_cost")
public class SalaryCost extends SalaryCostDB implements IDeduction {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	@Transient
	public String getName() {
		return getCostConcept();
	}

	@Transient
	@Override
	public String getExpression() {
		return String.valueOf(this.getAmount());
	}
		
}
