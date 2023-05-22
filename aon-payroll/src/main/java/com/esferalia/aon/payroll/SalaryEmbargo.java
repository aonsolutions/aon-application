package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.SalaryEmbargoDB;
import com.esferalia.aon.salary.ISalaryItem;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;

@Entity
@Table(name="salary_embargo")
public class SalaryEmbargo extends SalaryEmbargoDB implements IDeduction {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	@Transient
	public String getName() {
		return getDescription();
	}

	@Override
	@Transient
	public DeductionType getType() {
		return DeductionType.OTHER;
	}

	@Transient
	public String getExpression() {
		return null;
	}
		
}
