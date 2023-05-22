package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.SalaryBonusDB;
import com.esferalia.aon.salary.ISalaryItem;
import com.esferalia.aon.salary.enumeration.BonusType;

@Entity
@Table(name="salary_bonus")
public class SalaryBonus extends SalaryBonusDB implements ISalaryItem<BonusType>{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	@Transient
	public String getName() {
		return getBonusConcept();
	}
	
	@Override
	@Transient
	public BonusType getType() {
		return BonusType.SOCIAL_SECURITY;
	}
		
}
