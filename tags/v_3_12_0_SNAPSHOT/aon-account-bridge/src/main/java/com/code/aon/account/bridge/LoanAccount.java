package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.accounting.Loan;
import com.code.aon.common.ITransferObject;

@Entity
@Table(name="loan_account")
public class LoanAccount implements ITransferObject, IAccount {

	private static final long serialVersionUID = 4415920212702994723L;

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
	@ForeignKey(name="FK_LOAN_ACCOUNT_LOAN")
	@Index(name="IDX_LOAN_ACCOUNT_LOAN")								
	public Loan getLoan() {
		return loan;
	}

	public void setLoan(Loan loan) {
		this.loan = loan;
	}

	@ManyToOne
	@JoinColumn( name="account", nullable = false)
	@ForeignKey(name="FK_LOAN_ACCOUNT_ACCOUNT")
	@Index(name="IDX_LOAN_ACCOUNT_ACCOUNT")							
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
}