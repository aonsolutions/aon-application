package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import net.aonsolutions.occam.api.model.type.FinanceTrackingType;

public class FinanceTracking implements Serializable, HasAudit  {

	private static final long serialVersionUID = -5057462585779434987L;
	
	private boolean deleted;
	private boolean checked;
	
	private boolean lastTracking;
	private Integer id;
	private RegistryBank registryBank;
	private Integer bankStatementLink;
	private Finance finance;
	private PayMethodTypeDetail payMethodTypeDetail;
	private Integer domain;
	private Date trackingDate;
	private FinanceTrackingType type;
	private String description;
	private double amount;
	private boolean recorded;
	private Integer accountEntry;
	
	private Account payAccount;
	
	private String creationUser;
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;

	public Integer getId() {
		return this.id;
	}
	public FinanceTracking setId(Integer id) {
		this.id = id;
		return this;
	}

	public RegistryBank getRegistryBank() {
		return this.registryBank;
	}
	public FinanceTracking setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
		return this;
	}

	public Integer getBankStatementLink() {
		return this.bankStatementLink;
	}
	public FinanceTracking setBankStatementLink(Integer bankStatementLink) {
		this.bankStatementLink = bankStatementLink;
		return this;
	}

	public Finance getFinance() {
		return this.finance;
	}
	public FinanceTracking setFinance(Finance finance) {
		this.finance = finance;
		return this;
	}

	public PayMethodTypeDetail getPayMethodTypeDetail() {
		return this.payMethodTypeDetail;
	}
	public FinanceTracking setPayMethodTypeDetail(PayMethodTypeDetail payMethodTypeDetail) {
		this.payMethodTypeDetail = payMethodTypeDetail;
		return this;
	}

	public Integer getDomain() {
		return this.domain;
	}
	public FinanceTracking setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Date getTrackingDate() {
		return this.trackingDate;
	}
	public FinanceTracking setTrackingDate(Date trackingDate) {
		this.trackingDate = trackingDate;
		return this;
	}

	public FinanceTrackingType getType() {
		return this.type;
	}
	public FinanceTracking setType(FinanceTrackingType type) {
		this.type = type;
		return this;
	}

	public String getDescription() {
		return this.description;
	}
	public FinanceTracking setDescription(String description) {
		this.description = description;
		return this;
	}

	public double getAmount() {
		return this.amount;
	}
	public FinanceTracking setAmount(double amount) {
		this.amount = amount;
		return this;
	}

	public boolean isRecorded() {
		return this.recorded;
	}
	public FinanceTracking setRecorded(boolean recorded) {
		this.recorded = recorded;
		return this;
	}
	public Integer getAccountEntry() {
		return accountEntry;
	}
	public FinanceTracking setAccountEntry(Integer accountEntry) {
		this.accountEntry = accountEntry;
		return this;
	}

	public Account getPayAccount() {
		return payAccount;
	}
	public FinanceTracking setPayAccount(Account payAccount) {
		this.payAccount = payAccount;
		return this;
	}

	@Override
	public String getCreationUser() {
		return this.creationUser;
	}
	public FinanceTracking setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	@Override
	public Timestamp getCreationDate() {
		return this.creationDate;
	}
	public FinanceTracking setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	@Override
	public String getModificationUser() {
		return this.modificationUser;
	}
	public FinanceTracking setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	@Override
	public Timestamp getModificationDate() {
		return this.modificationDate;
	}
	public FinanceTracking setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	public boolean isLastTracking() {
		return lastTracking;
	}
	public FinanceTracking setLastTracking(boolean lastTracking) {
		this.lastTracking = lastTracking;
		return this;
	}

	public boolean isChecked() {
		return checked;
	}
	public FinanceTracking setChecked(boolean checked) {
		this.checked = checked;
		return this;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public FinanceTracking setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}
	public boolean isFractioned() {
		return getType() == FinanceTrackingType.FRACTIONED;
	}
	public boolean isBatched() {
		return getType() == FinanceTrackingType.BATCHED;
	}
	public boolean isSettled() {
		return getType() == FinanceTrackingType.SETTLED;
	}
	
}

