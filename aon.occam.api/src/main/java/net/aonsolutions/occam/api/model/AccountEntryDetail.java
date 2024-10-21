package net.aonsolutions.occam.api.model;

import java.sql.Timestamp;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonMathUtils;

import net.aonsolutions.occam.api.model.metadata.AccountEntryDetailMetadata;

public class AccountEntryDetail extends AonEntity<AccountEntryDetailMetadata> implements HasAudit {

	
	private static final long serialVersionUID = -1136927363787696049L;

	private boolean deleted;
	
	private Integer id;
	private Integer domain;
	private Integer accountEntry;
	private Integer line;
	
	private Account account;
	private String concept;
	private double debit;
	private double credit;
	private Account balancingAccount;
	private String documentNumber;
	
	private String creationUser;
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public AccountEntryDetail markAsClean() {
		super.markAsClean();
		return this; 
	}

	public boolean isDeleted() {
		return deleted;
	}
	public AccountEntryDetail setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	public Integer getId() {
		return id;
	}
	public AccountEntryDetail setId(Integer id) {
		checkIfDirty( this.id,id, AccountEntryDetailMetadata.ID);
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public AccountEntryDetail setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, AccountEntryDetailMetadata.DOMAIN);
		this.domain = domain;
		return this;
	}

	public Integer getAccountEntry() {
		return accountEntry;
	}
	public AccountEntryDetail setAccountEntry(Integer accountEntry) {
		checkIfDirty( this.accountEntry,accountEntry, AccountEntryDetailMetadata.ACCOUNT_ENTRY);
		this.accountEntry = accountEntry;
		return this;
	}

	public Optional<Account> getAccount() {
		return Optional.ofNullable(account);
	}
	public AccountEntryDetail setAccount(Account account) {
		checkIfDirty( this.account,account, AccountEntryDetailMetadata.ACCOUNT);
		this.account = account;
		return this;
	}
	
	public String getAccountCode() {
		return getAccount().map(Account::getCode).orElse(null);
	}
	public String getAccountDescription() {
		return getAccount().map(Account::getDescription).orElse(null);
	}

	public Integer getLine() {
		return line;
	}
	public AccountEntryDetail setLine(Integer line) {
		checkIfDirty( this.line,line, AccountEntryDetailMetadata.LINE);
		this.line = line;
		return this;
	}

	public String getConcept() {
		return concept;
	}
	public AccountEntryDetail setConcept(String concept) {
		checkIfDirty( this.concept,concept, AccountEntryDetailMetadata.CONCEPT);
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
		checkIfDirty( this.debit,debit, AccountEntryDetailMetadata.DEBIT);
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
		checkIfDirty( this.credit,credit, AccountEntryDetailMetadata.CREDIT);
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

	public Optional<Account> getBalancingAccount() {
		return Optional.ofNullable(balancingAccount);
	}
	public AccountEntryDetail setBalancingAccount(Account balancingAccount) {
		checkIfDirty( this.balancingAccount,balancingAccount, AccountEntryDetailMetadata.BALANCING_ACCOUNT);
		this.balancingAccount = balancingAccount;
		return this;
	}
	
	public String getBalancingAccountCode() {
		return getBalancingAccount().map(Account::getCode).orElse(null);
	}
	public String getBalancingAccountDescription() {
		return getBalancingAccount().map(Account::getDescription).orElse(null);
	}
	
	public String getDocumentNumber() {
		return documentNumber;
	}
	public AccountEntryDetail setDocumentNumber(String documentNumber) {
		checkIfDirty( this.documentNumber,documentNumber, AccountEntryDetailMetadata.DOCUMENT_NUMBER);
		this.documentNumber = documentNumber;
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
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public AccountEntryDetail setCreationDate(Timestamp creationDate) {
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
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public AccountEntryDetail setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	static AccountEntryDetail clone(AccountEntryDetail detail) {
		return new AccountEntryDetail()
			.setDeleted(detail.deleted)
			.setId(detail.id)
			.setDomain(detail.domain)
			.setAccountEntry(detail.accountEntry)
			.setAccount(detail.account)
			.setLine(detail.line)
			.setConcept(detail.concept)
			.setDebit(detail.debit)
			.setCredit(detail.credit)
			.setBalancingAccount(detail.balancingAccount)
			.setDocumentNumber(detail.documentNumber)
			.setCreationUser(detail.creationUser)
			.setCreationDate(detail.creationDate)
			.setModificationUser(detail.modificationUser)
			.setModificationDate(detail.modificationDate)
		;
	}
	
}
