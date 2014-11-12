package com.esferalia.aon.core.api.model;

import java.io.Serializable;

import com.esferalia.aon.core.commons.util.AonMathUtils;

public class AccountEntryDetail implements Serializable {

	
	private static final long serialVersionUID = -1136927363787696049L;

	public AccountEntryDetail() {
		
	}
	
	public AccountEntryDetail( Integer account, String accountCode, String accountDescription, 
			String concept,double debit, double credit,
			Integer balancingAccount,String balancingAccountCode,
			String balancingAccountDescription,String documentNumber) {
		setAccount(account);
		setAccountCode(accountCode);
		setAccountDescription(accountDescription);
		setConcept(concept);
		setDebit(debit);
		setCredit(credit);
		setBalancingAccount(account);
		setBalancingAccountCode(accountCode);
		setBalancingAccountDescription(accountDescription);
		setDocumentNumber(documentNumber);
	}
	
	public AccountEntryDetail(Integer id, Integer domain, Integer accountEntry,
			Integer account, String accountCode, String accountDescription, 
			Integer line, String concept,double debit, double credit,
			Integer balancingAccount,String balancingAccountCode,
			String balancingAccountDescription,String documentNumber) {
		this(account, accountCode, accountDescription, concept,
			debit, credit, balancingAccount,balancingAccountCode,
			balancingAccountDescription,documentNumber);
		setId(id);
		setDomain(domain);
		setAccountEntry(accountEntry);
		setLine(line);
	}

	private Integer id;
	private Integer domain;
	private Integer accountEntry;
	private Integer account;
	private String accountCode;
	private String accountDescription;
	private Integer line;
	private String concept;
	private double debit;
	private double credit;
	private Integer balancingAccount;
	private String balancingAccountCode;
	private String balancingAccountDescription;
	private String documentNumber;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDomain() {
		return domain;
	}

	public void setDomain(Integer domain) {
		this.domain = domain;
	}

	public Integer getAccountEntry() {
		return accountEntry;
	}

	public void setAccountEntry(Integer accountEntry) {
		this.accountEntry = accountEntry;
	}

	public Integer getAccount() {
		return account;
	}

	public void setAccount(Integer account) {
		this.account = account;
	}
	
	public String getAccountCode() {
		return accountCode;
	}
	
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getAccountDescription() {
		return accountDescription;
	}

	public void setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
	}

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public double getDebit() {
		return debit;
	}

	/**
	 * Asignación del valor "DEBE"
	 * El "DEBE" de un apunte contable no puede ser negativo. Si es 
	 * negativo se pasa el valor al "HABER" commo positivo.
	 * Tampoco debe haber valor en "DEBE" y "HABER" simultaneamente.
	 * 
	 * @param debit	El debe
	 */
	public void setDebit(double debit) {
		if (debit != 0) {
			if (debit < 0) {
				this.credit = AonMathUtils.absRounded(debit);
				debit = 0;
			} else {
				this.credit = 0;
			}
		}
		this.debit = AonMathUtils.round(debit);
	}

	public double getCredit() {
		return credit;
	}

	/**
	 * Asignación del valor "HABER"
	 * El "HABER" de un apunte contable no puede ser negativo. Si es 
	 * negativo se pasa el valor al "DEBE" commo positivo.
	 * Tampoco debe haber valor en "HABER" y "DEBE" simultaneamente.
	 * 
	 * @param credit El haber
	 */
	public void setCredit(double credit) {
		if (credit != 0) {
			if (credit < 0) {
				this.debit = AonMathUtils.absRounded(credit);
				credit = 0;
			} else {
				this.debit = 0;
			}
		}
		this.credit = AonMathUtils.round(credit);
	}

	public Integer getBalancingAccount() {
		return balancingAccount;
	}

	public void setBalancingAccount(Integer balancingAccount) {
		this.balancingAccount = balancingAccount;
	}
	
	public String getBalancingAccountCode() {
		return balancingAccountCode;
	}

	public void setBalancingAccountCode(String balancingAccountCode) {
		this.balancingAccountCode = balancingAccountCode;
	}
	
	public String getBalancingAccountDescription() {
		return balancingAccountDescription;
	}

	public void setBalancingAccountDescription(String balancingAccountDescription) {
		this.balancingAccountDescription = balancingAccountDescription;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	
}
