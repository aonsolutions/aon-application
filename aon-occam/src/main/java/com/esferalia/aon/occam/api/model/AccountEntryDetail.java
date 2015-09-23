package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonMathUtils;

public class AccountEntryDetail implements Serializable {

	
	private static final long serialVersionUID = -1136927363787696049L;

	public AccountEntryDetail() {
		
	}
	
//	public AccountEntryDetail( Integer account, String accountCode, String accountDescription, 
//			String concept,double debit, double credit,
//			Integer balancingAccount,String balancingAccountCode,
//			String balancingAccountDescription,String documentNumber) {
//		setAccount(account);
//		setAccountCode(accountCode);
//		setAccountDescription(accountDescription);
//		setConcept(concept);
//		setDebit(debit);
//		setCredit(credit);
//		setBalancingAccount(account);
//		setBalancingAccountCode(accountCode);
//		setBalancingAccountDescription(accountDescription);
//		setDocumentNumber(documentNumber);
//	}
//	
//	public AccountEntryDetail(Integer id, Integer domain, Integer accountEntry,
//			Integer account, String accountCode, String accountDescription, 
//			Integer line, String concept,double debit, double credit,
//			Integer balancingAccount,String balancingAccountCode,
//			String balancingAccountDescription,String documentNumber) {
//		this(account, accountCode, accountDescription, concept,
//			debit, credit, balancingAccount,balancingAccountCode,
//			balancingAccountDescription,documentNumber);
//		setId(id);
//		setDomain(domain);
//		setAccountEntry(accountEntry);
//		setLine(line);
//	}

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

	public AccountEntryDetail setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public AccountEntryDetail setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getAccountEntry() {
		return accountEntry;
	}

	public AccountEntryDetail setAccountEntry(Integer accountEntry) {
		this.accountEntry = accountEntry;
		return this;
	}

	public Integer getAccount() {
		return account;
	}

	public AccountEntryDetail setAccount(Integer account) {
		this.account = account;
		return this;
	}
	
	public String getAccountCode() {
		return accountCode;
	}
	
	public AccountEntryDetail setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}

	public String getAccountDescription() {
		return accountDescription;
	}

	public AccountEntryDetail setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
		return this;
	}

	public Integer getLine() {
		return line;
	}

	public AccountEntryDetail setLine(Integer line) {
		this.line = line;
		return this;
	}

	public String getConcept() {
		return concept;
	}

	public AccountEntryDetail setConcept(String concept) {
		this.concept = concept;
		return this;
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
	public AccountEntryDetail setDebit(double debit) {
		if (debit != 0) {
			if (debit < 0) {
				this.credit = AonMathUtils.absRounded(debit);
				debit = 0;
			} else {
				this.credit = 0;
			}
		}
		this.debit = AonMathUtils.round(debit);
		return this;
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
	public AccountEntryDetail setCredit(double credit) {
		if (credit != 0) {
			if (credit < 0) {
				this.debit = AonMathUtils.absRounded(credit);
				credit = 0;
			} else {
				this.debit = 0;
			}
		}
		this.credit = AonMathUtils.round(credit);
		return this;
	}

	public Integer getBalancingAccount() {
		return balancingAccount;
	}

	public AccountEntryDetail setBalancingAccount(Integer balancingAccount) {
		this.balancingAccount = balancingAccount;
		return this;
	}
	
	public String getBalancingAccountCode() {
		return balancingAccountCode;
	}

	public AccountEntryDetail setBalancingAccountCode(String balancingAccountCode) {
		this.balancingAccountCode = balancingAccountCode;
		return this;
	}
	
	public String getBalancingAccountDescription() {
		return balancingAccountDescription;
	}

	public AccountEntryDetail setBalancingAccountDescription(String balancingAccountDescription) {
		this.balancingAccountDescription = balancingAccountDescription;
		return this;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public AccountEntryDetail setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
		return this;
	}

	public void print() {
		System.out.println("\tid:{"+id+"}"+
			"domain:{"+domain+"}"+
			"accountEntry:{"+accountEntry+"}"+
			"account:{"+account+"}"+
			"accountCode:{"+accountCode+"}"+
			"accountDescription:{"+accountDescription+"}"+
			"line:{"+line+"}"+
			"concept:{"+concept+"}"+
			"debit:{"+debit+"}"+
			"credit:{"+credit+"}"+
			"balancingAccount:{"+balancingAccount+"}"+
			"balancingAccountCode:{"+balancingAccountCode+"}"+
			"balancingAccountDescription:{"+balancingAccountDescription+"}"+
			"documentNumber:{"+documentNumber+"}");
	}
	
}
