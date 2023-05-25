package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.PayrollWorkPlaceDB;

@Entity
@Table(name="payroll_workplace")
public class PayrollWorkPlace extends PayrollWorkPlaceDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}
