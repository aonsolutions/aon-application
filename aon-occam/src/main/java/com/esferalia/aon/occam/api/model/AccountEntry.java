package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
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
	private Integer journal;
	private SecurityLevel securityLevel;
	private String comments;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	private boolean dirty = true;
	
	private LinkedList<AccountEntryDetail> details;
	
	public Integer getId() {
		return this.id;
	}
	public AccountEntry setId(Integer id) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.id , id) );
		this.id = id;
		return this;
	}

	public Integer getPeriod() {
		return this.period;
	}
	public AccountEntry setPeriod(Integer period) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.period , period) );
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
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.domain , domain) );
		this.domain = domain;
		return this;
	}

	public Date getEntryDate() {
		return this.entryDate;
	}
	public AccountEntry setEntryDate(Date entryDate) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.entryDate , entryDate) );
		this.entryDate = entryDate;
		return this;
	}

	public AccountEntryType getEntryType() {
		return this.entryType;
	}
	public AccountEntry setEntryType(AccountEntryType entryType) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.entryType , entryType) );
		this.entryType = entryType;
		return this;
	}
	
	public boolean isManual() {
		return getEntryType() == null || getEntryType().isManual();
	}

	public Integer getJournal() {
		return this.journal;
	}
	public AccountEntry setJournal(Integer journal) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.journal , journal) );
		this.journal = journal;
		return this;
	}

	public SecurityLevel getSecurityLevel() {
		return this.securityLevel;
	}
	public AccountEntry setSecurityLevel(SecurityLevel securityLevel) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.securityLevel , securityLevel) );
		this.securityLevel = securityLevel;
		return this;
	}

	public String getComments() {
		return this.comments;
	}
	public AccountEntry setComments(String comments) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.comments , comments) );
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
	
	public LinkedList<AccountEntryDetail> getDetails() {
		if (this.details == null) {
			this.details = new LinkedList<AccountEntryDetail>();
		}
		return details;
	}
	public AccountEntry setDetails(LinkedList<AccountEntryDetail> details) {
		this.details = details;
		return this;
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
		LinkedList<AccountEntryDetail> details = ori.details == null
				?null
				:new LinkedList<AccountEntryDetail>();
		if (ori.details != null) {
			for ( AccountEntryDetail detail : ori.details ) {
				details.add(AccountEntryDetail.clone(detail));
			}
		}
		return new AccountEntry()
			.setId(ori.id)
			.setPeriod(ori.period)
			.setPeriodName(ori.periodName)
			.setPeriodStatus(ori.periodStatus)
			.setDomain(ori.domain)
			.setEntryDate(ori.entryDate)
			.setEntryType(ori.entryType)
			.setJournal(ori.journal)
			.setSecurityLevel(ori.securityLevel)
			.setComments(ori.comments)
			.setCreationUser(ori.creationUser)
			.setCreationDate(ori.creationDate)
			.setModificationUser(ori.modificationUser)
			.setModificationDate(ori.modificationDate)
			.setDetails(details);
		
	}

}
