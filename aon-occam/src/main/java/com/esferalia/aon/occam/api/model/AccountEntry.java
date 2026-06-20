package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class AccountEntry implements Serializable, HasAudit {

	private static final long serialVersionUID = 369125336534396707L;

	private Integer id;
	private Integer period;
	private String periodName;
	private AccountPeriodStatus periodStatus;
	private Integer domain;
	private Date entryDate;
	private AccountEntryType entryType;
	private Integer activity;
	private String activityDescription;
	private Integer journal;
	private SecurityLevel securityLevel;
	
	private String comments;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	private boolean dirty = true;
	
	private boolean undeductible; 
	
	private LinkedList<AccountEntryDetail> details;
	
	public Integer getId() {
		return this.id;
	}
	public AccountEntry setId(Integer id) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.id , id) );
		this.id = id;
		return this;
	}

	public Integer getPeriod() {
		return this.period;
	}
	public AccountEntry setPeriod(Integer period) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.period , period) );
		this.period = period;
		return this;
	}

	public String getPeriodName() {
		return periodName;
	}
	public AccountEntry setPeriodName(String periodName) {
		this.periodName = periodName;
		return this;
	}
	public AccountPeriodStatus getPeriodStatus() {
		return periodStatus;
	}
	public AccountEntry setPeriodStatus(AccountPeriodStatus periodStatus) {
		this.periodStatus = periodStatus;
		return this;
	}
	public boolean isPeriodActive() {
		return (getPeriodStatus() == null || getPeriodStatus().isActive());
	}
	
	public Integer getDomain() {
		return this.domain;
	}
	public AccountEntry setDomain(Integer domain) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.domain , domain) );
		this.domain = domain;
		return this;
	}

	public Date getEntryDate() {
		return this.entryDate;
	}
	public AccountEntry setEntryDate(Date entryDate) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.entryDate , entryDate) );
		this.entryDate = entryDate;
		return this;
	}

	public AccountEntryType getEntryType() {
		return this.entryType;
	}
	public AccountEntry setEntryType(AccountEntryType entryType) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.entryType , entryType) );
		this.entryType = entryType;
		return this;
	}
	public Integer getActivity() {
		return activity;
	}
	public AccountEntry setActivity(Integer activity) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.activity,activity) );
		this.activity = activity;
		return this;
	}
	public String getActivityDescription() {
		return activityDescription;
	}
	public AccountEntry setActivityDescription(String activityDescription) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.activity,activity) );
		this.activityDescription = activityDescription;
		return this;
	}
	public boolean isInvoice() {
		return getEntryType() == null || getEntryType().isInvoice();
	}

	public Integer getJournal() {
		return this.journal;
	}
	public AccountEntry setJournal(Integer journal) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.journal , journal) );
		this.journal = journal;
		return this;
	}

	public SecurityLevel getSecurityLevel() {
		return this.securityLevel;
	}
	public AccountEntry setSecurityLevel(SecurityLevel securityLevel) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.securityLevel , securityLevel) );
		this.securityLevel = securityLevel;
		return this;
	}

	public String getComments() {
		return this.comments;
	}
	public AccountEntry setComments(String comments) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.comments , comments) );
		this.comments = comments;
		return this;
	}

	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	public AccountEntry setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
		return this;
	}
	
	public boolean isUndeductible() {
		return undeductible;
	}
	public AccountEntry setUndeductible(boolean undeductible) {
		this.undeductible = undeductible;
		return this;
	}
	
	public LinkedList<AccountEntryDetail> getDetails() {
		if (this.details == null) this.details = new LinkedList<>();
		return details;
	}
	public AccountEntry setDetails(LinkedList<AccountEntryDetail> details) {
		this.details = details;
		return this;
	}
	public Stream<AccountEntryDetail> getDetailsStream() {
		return AonCollectionUtils.stream(getDetails());
	}
	public AccountEntry addDetail( AccountEntryDetail detail) {
		getDetails().add(detail);
		return this;
	}
	
	public int getDetailsSize() {
		int i = 0;
		for (AccountEntryDetail aed : getDetails()) {
			i = i + (aed.isDeleted() ? 0 : 1);
		}
		return i;
	}
	
	public AccountEntryDetail getLastDetail() {
		AccountEntryDetail aed = null;
		if (!getDetails().isEmpty()) {
			for (int i = (getDetails().size() - 1); i >= 0; i--) {
				aed = getDetails().get(i);
				if (!aed.isDeleted()) {
					break;
				}
			}
		}
		return aed;
	}

	// ---------------------------------------------------------- DIRTY
	public boolean isDirty() {
		return dirty?dirty:areDetailsDirty();
	}
	public AccountEntry setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	private boolean areDetailsDirty() {
		for (AccountEntryDetail aed : getDetails()) {
			if (aed.isDirty()) return true;
		}
		return false;
	}
	
	// ---------------------------------------------------------- AUDIT
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public AccountEntry setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	public AccountEntry setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public AccountEntry setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	public AccountEntry setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public static AccountEntry clone(AccountEntry ori) {
		return new AccountEntry()
			.setId(ori.id)
			.setPeriod(ori.period)
			.setPeriodName(ori.periodName)
			.setPeriodStatus(ori.periodStatus)
			.setDomain(ori.domain)
			.setEntryDate(ori.entryDate)
			.setEntryType(ori.entryType)
			.setActivity(ori.getActivity())
			.setActivityDescription(ori.getActivityDescription())
			.setJournal(ori.journal)
			.setSecurityLevel(ori.securityLevel)
			.setUndeductible(ori.isUndeductible())
			.setComments(ori.comments)
			.setCreationUser(ori.creationUser)
			.setCreationDate(ori.creationDate)
			.setModificationUser(ori.modificationUser)
			.setModificationDate(ori.modificationDate)
			.setDetails(
				ori.details == null
					? null
					: ori.getDetailsStream()
				        .map(AccountEntryDetail::clone)
				        .collect(Collectors.toCollection(LinkedList::new)));
	}
}
