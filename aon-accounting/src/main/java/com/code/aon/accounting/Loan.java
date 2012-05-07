package com.code.aon.accounting;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.accounting.enumeration.LoanStatus;
import com.code.aon.common.enumeration.SecurityLevel;
import com.esferalia.aon.entity.master.LoanDB;

@Entity
@Table(name="loan")
public class Loan extends LoanDB {

	private static final long serialVersionUID = 6332808498569171281L;

	public Loan() {
		setSecurityLevel(SecurityLevel.OFFICIAL);
		setStatus(LoanStatus.ACTIVE);
	}
	
}