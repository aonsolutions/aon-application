package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.SalaryDataDB;

@Entity
@Table(name="salary_data")
public class SalaryData extends SalaryDataDB {
	
	private static final long serialVersionUID = 1L;

		
}
