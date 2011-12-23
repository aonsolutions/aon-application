package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.PayrollWorkPlaceDB;

@Entity
@Table(name="payroll_workplace")
public class PayrollWorkPlace extends PayrollWorkPlaceDB {

	private static final long serialVersionUID = 1L;
	
}
