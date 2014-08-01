package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ContractLeaveDB;

@Entity
@Table(name="contract_leave")
public class ContractLeave extends ContractLeaveDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}

