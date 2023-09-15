package com.code.aon.accounting;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.account.IAccount;
import com.code.aon.accounting.enumeration.LoanStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.SecurityLevel;
import com.esferalia.aon.entity.master.LoanDB;

@Entity
@Table(name="loan")
public class Loan extends LoanDB implements IAccount {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public Loan() {
		setSecurityLevel(SecurityLevel.OFFICIAL);
		setStatus(LoanStatus.ACTIVE);
	}
	
}