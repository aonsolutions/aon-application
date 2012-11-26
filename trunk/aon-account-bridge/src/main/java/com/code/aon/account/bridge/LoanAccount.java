package com.code.aon.account.bridge;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.account.IAccount;
import com.code.aon.accounting.Loan;
import com.code.aon.common.ITransferObject;
import com.esferalia.aon.entity.master.LoanAccountDB;

@Entity
@Table(name="loan_account")
public class LoanAccount extends LoanAccountDB implements IAccount {

	private static final long serialVersionUID = 1L;

	@Transient
	public ITransferObject getLinkedTo() {
		return getLoan();
	}
	public void setLinkedTo(ITransferObject to) {
		setLoan((Loan) to);
	}	

	@Transient
	public String getAccountDescription() {
		return (getLoan()==null) ? null : getLoan().getDescription();
	}
	
}