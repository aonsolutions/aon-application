package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.account.Account;
import com.code.aon.account.Loan;
import com.code.aon.common.ITransferObject;

@Entity
@Table(name="loan_account")
public class LoanAccount implements ITransferObject {

	private Integer id;
	
	private Loan loan;
	
	private Account account;


	@Id
	@Column(nullable=false)
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn( name="loan", nullable = false)
	public Loan getLoan() {
		return loan;
	}

	public void setLoan(Loan loan) {
		this.loan = loan;
	}

	@ManyToOne
	@JoinColumn( name="account", nullable = false)
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
}