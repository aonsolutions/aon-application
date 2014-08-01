package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.SalaryCostDB;
import com.esferalia.aon.salary.ISalaryItem;
import com.esferalia.aon.salary.enumeration.DeductionType;

@Entity
@Table(name="salary_cost")
public class SalaryCost extends SalaryCostDB implements ISalaryItem<DeductionType>{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	@Transient
	public String getName() {
		return getCostConcept();
	}
		
}
