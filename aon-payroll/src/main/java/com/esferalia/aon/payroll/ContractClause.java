package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.ContractClauseDB;

@Entity
@Table(name="contract_clause")
@Heritable
public class ContractClause extends ContractClauseDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	
}
