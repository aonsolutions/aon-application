package com.code.aon.academy;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.AlumnLoanDB;

/**
 * The Class AlumnLoan.
 */
@Entity
@Table(name="alumn_loan")
public class AlumnLoan extends AlumnLoanDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
