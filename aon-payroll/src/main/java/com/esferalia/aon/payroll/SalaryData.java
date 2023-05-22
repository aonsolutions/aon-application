package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.SalaryDataDB;

@Entity
@Table(name="salary_data")
public class SalaryData extends SalaryDataDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		
}
