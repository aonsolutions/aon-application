package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.type.AccountEntryOrder;
import net.aonsolutions.occam.api.model.type.AccountEntryType;
import net.aonsolutions.occam.api.model.type.SecurityLevel;

public class AccountEntryParams implements Serializable {

	private static final long serialVersionUID = 7399522390660289406L;
	
	private String domainName; 
	private Integer domain;
	private String user;
	private Integer accountEntryId;
	private Integer period;
	private Date fromDate;
	private Date toDate;
	private AccountEntryType type;
	private Integer journal;
	private Integer activity;
	private SecurityLevel securityLevel;
	
	private Integer account;
	private String accountCode;
	private String accountDescription;
	private Double debit;
	private Double credit;
	private String concept;
	private String document;
	
	private Date fromCreationDate;
	private Date toCreationDate;
	private String creationUser;
	private Date fromModificationDate;
	private Date toModificationDate;
	private String modificationUser;
	
	private String comments;
	private AccountEntryOrder order;
	
	public Optional<String> getDomainName() {
		return Optional.ofNullable(domainName);
	}
	public AccountEntryParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public Optional<Integer> getDomain() {
		return Optional.ofNullable(domain);
	}
	public AccountEntryParams setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Optional<String> getUser() {
		return Optional.ofNullable(user);
	}
	public AccountEntryParams setUser(String user) {
		this.user = user;
		return this;
	}

	public Optional<Integer> getAccountEntryId() {
		return Optional.ofNullable(accountEntryId);
	}
	public AccountEntryParams setAccountEntryId(Integer accountEntryId) {
		this.accountEntryId = accountEntryId;
		return this;
	}

	public Optional<Integer> getPeriod() {
		return Optional.ofNullable(period);
	}
	public AccountEntryParams setPeriod(Integer period) {
		this.period = period;
		return this;
	}

	public Optional<Date> getFromDate() {
		return Optional.ofNullable(fromDate);
	}
	public AccountEntryParams setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public Optional<Date> getToDate() {
		return Optional.ofNullable(toDate);
	}
	public AccountEntryParams setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}

	public Optional<AccountEntryType> getType() {
		return Optional.ofNullable(type);
	}
	public AccountEntryParams setType(AccountEntryType type) {
		this.type = type;
		return this;
	}

	public Optional<Integer> getJournal() {
		return Optional.ofNullable(journal);
	}
	public AccountEntryParams setJournal(Integer journal) {
		this.journal = journal;
		return this;
	}

	public Optional<Integer> getActivity() {
		return Optional.ofNullable(activity);
	}
	public AccountEntryParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}

	public Optional<SecurityLevel> getSecurityLevel() {
		return Optional.ofNullable(securityLevel);
	}
	public AccountEntryParams setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}

	public Optional<Integer> getAccount() {
		return Optional.ofNullable(account);
	}
	public AccountEntryParams setAccount(Integer account) {
		this.account = account;
		return this;
	}

	public Optional<String> getAccountCode() {
		return Optional.ofNullable(accountCode);
	}
	public AccountEntryParams setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}

	public Optional<String> getAccountDescription() {
		return Optional.ofNullable(accountDescription);
	}
	public AccountEntryParams setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
		return this;
	}

	public Optional<Double> getDebit() {
		return Optional.ofNullable(debit);
	}
	public AccountEntryParams setDebit(Double debit) {
		this.debit = debit;
		return this;
	}

	public Optional<Double> getCredit() {
		return Optional.ofNullable(credit);
	}
	public AccountEntryParams setCredit(Double credit) {
		this.credit = credit;
		return this;
	}

	public Optional<String> getConcept() {
		return Optional.ofNullable(concept);
	}
	public AccountEntryParams setConcept(String concept) {
		this.concept = concept;
		return this;
	}

	public Optional<String> getDocument() {
		return Optional.ofNullable(document);
	}
	public AccountEntryParams setDocument(String document) {
		this.document = document;
		return this;
	}

	public Optional<Date> getFromCreationDate() {
		return Optional.ofNullable(fromCreationDate);
	}
	public AccountEntryParams setFromCreationDate(Date fromCreationDate) {
		this.fromCreationDate = fromCreationDate;
		return this;
	}

	public Optional<Date> getToCreationDate() {
		return Optional.ofNullable(toCreationDate);
	}
	public AccountEntryParams setToCreationDate(Date toCreationDate) {
		this.toCreationDate = toCreationDate;
		return this;
	}

	public Optional<String> getCreationUser() {
		return Optional.ofNullable(creationUser);
	}
	public AccountEntryParams setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Optional<Date> getFromModificationDate() {
		return Optional.ofNullable(fromModificationDate);
	}
	public AccountEntryParams setFromModificationDate(Date fromModificationDate) {
		this.fromModificationDate = fromModificationDate;
		return this;
	}

	public Optional<Date> getToModificationDate() {
		return Optional.ofNullable(toModificationDate);
	}
	public AccountEntryParams setToModificationDate(Date toModificationDate) {
		this.toModificationDate = toModificationDate;
		return this;
	}

	public Optional<String> getModificationUser() {
		return Optional.ofNullable(modificationUser);
	}
	public AccountEntryParams setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Optional<String> getComments() {
		return Optional.ofNullable(comments);
	}
	public AccountEntryParams setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public Optional<AccountEntryOrder> getOrder() {
		return Optional.ofNullable(order);
	}
	public AccountEntryParams setOrder(AccountEntryOrder order) {
		this.order = order;
		return this;
	}

	public boolean hasDetailProperties() {
		return getAccount().filter(AonMathUtils::isNotZero).isPresent()
			|| getAccountCode().filter(AonStringUtils::isNotBlank).isPresent()
			|| getAccountDescription().filter(AonStringUtils::isNotBlank).isPresent()
			|| getDebit().filter(AonMathUtils::isNotZero).isPresent()
			|| getCredit().filter(AonMathUtils::isNotZero).isPresent()
			|| getConcept().filter(AonStringUtils::isNotBlank).isPresent()
			|| getDocument().filter(AonStringUtils::isNotBlank).isPresent()
		;
	}

}
