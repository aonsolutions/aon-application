package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class AccountEntryDetail implements Serializable, HasAudit {

	
	private static final long serialVersionUID = -1136927363787696049L;

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
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	private boolean dirty = true;
	
	public Integer getId() {
		return id;
	}

	public AccountEntryDetail setId(Integer id) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.id , id) );
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public AccountEntryDetail setDomain(Integer domain) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.domain , domain) );
		this.domain = domain;
		return this;
	}

	public Integer getAccountEntry() {
		return accountEntry;
	}

	public AccountEntryDetail setAccountEntry(Integer accountEntry) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.accountEntry , accountEntry) );
		this.accountEntry = accountEntry;
		return this;
	}

	public Integer getAccount() {
		return account;
	}

	public AccountEntryDetail setAccount(Integer account) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.account , account) );
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
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.line,line) );
		this.line = line;
		return this;
	}

	public String getConcept() {
		return concept;
	}

	public AccountEntryDetail setConcept(String concept) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.concept,concept) );
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
		this.setDirty( isDirty()?true:this.debit != debit );
		if (debit != 0) {
			if (debit < 0) {
				setCredit( AonMathUtils.absRounded(debit));
				debit = 0;
			} else {
				setCredit( 0 );
			}
		}
		this.debit = AonMathUtils.round(debit);
		return this;
	}

	public AccountEntryDetail addDebit(double debit) {
		double newDebit = (getDebit() - getCredit()) + debit;
		if (AonMathUtils.isZero(newDebit)) {
			credit = 0;
		}
		return setDebit(newDebit);
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
		this.setDirty( isDirty()?true:this.credit != credit );
		if (credit != 0) {
			if (credit < 0) {
				setDebit( AonMathUtils.absRounded(credit));
				credit = 0;
			} else {
				setDebit( 0 );
			}
		}
		this.credit = AonMathUtils.round(credit);
		return this;
	}
	public AccountEntryDetail addCredit(double credit) {
		double newCredit = (getCredit() - getDebit()) + credit;
		if (AonMathUtils.isZero(newCredit)) {
			debit = 0;
		}
		return setCredit(newCredit);
	}

	public Integer getBalancingAccount() {
		return balancingAccount;
	}

	public AccountEntryDetail setBalancingAccount(Integer balancingAccount) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.balancingAccount, balancingAccount) );
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
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.documentNumber, documentNumber) );
		this.documentNumber = documentNumber;
		return this;
	}
	
	// ---------------------------------------------------------- DIRTY
	public boolean isDirty() {
		return dirty;
	}
	public AccountEntryDetail setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	// ---------------------------------------------------------- AUDIT
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public AccountEntryDetail setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	public AccountEntryDetail setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public AccountEntryDetail setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	public AccountEntryDetail setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	public boolean isDeleted() {
		return (getId() != null && getId() < 0);
	}

	public static AccountEntryDetail clone(AccountEntryDetail detail) {
		return new AccountEntryDetail()
				.setId(detail.id)
				.setDomain(detail.domain)
				.setAccountEntry(detail.accountEntry)
				.setAccount(detail.account)
				.setAccountCode(detail.accountCode)
				.setAccountDescription(detail.accountDescription)
				.setLine(detail.line)
				.setConcept(detail.concept)
				.setDebit(detail.debit)
				.setCredit(detail.credit)
				.setBalancingAccount(detail.balancingAccount)
				.setBalancingAccountCode(detail.balancingAccountCode)
				.setBalancingAccountDescription(detail.balancingAccountDescription)
				.setDocumentNumber(detail.documentNumber)
				.setCreationUser(detail.creationUser)
				.setCreationDate(detail.creationDate)
				.setModificationUser(detail.modificationUser)
				.setModificationDate(detail.modificationDate);
	}
	
}
