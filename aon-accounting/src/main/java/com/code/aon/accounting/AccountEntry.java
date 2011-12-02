package com.code.aon.accounting;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.common.enumeration.SecurityLevel;

@Entity
@Table(name = "account_entry")
public class AccountEntry implements ITransferObject, IConfidentialable {
	
	private static final long serialVersionUID = -3297371099219203320L;

	private Integer id;
	private Period accountPeriod;
	private Date entryDate;
	private AccountEntryType type;
	private Integer journal;
	private String comments;
	private SecurityLevel securityLevel;
	private Set<AccountEntryDetail> detail = new HashSet<AccountEntryDetail>();
	
	private boolean regenerateSummaryOnUpdate = true;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="account_period", nullable=false)
	@ForeignKey(name = "FK_ACCOUNT_ENTRY_ACCOUNT_PERIOD")
	@Index(name = "IDX_ACCOUNT_ENTRY_ACCOUNT_PERIOD")
	public Period getAccountPeriod() {
		return accountPeriod;
	}
	public void setAccountPeriod(Period accountPeriod) {
		this.accountPeriod = accountPeriod;
	}

	@Column(name="entry_date")
	@Temporal(value=TemporalType.DATE)
	public Date getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	@Column(name="entry_type")
	public AccountEntryType getType() {
		return type;
	}
	public void setType(AccountEntryType type) {
		this.type = type;
	}

	public Integer getJournal() {
		return journal;
	}
	public void setJournal(Integer journal) {
		this.journal = journal;
	}

	@Column(name="security_level")
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	@Transient
	@Override
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}

	@Transient
	@Override
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}
	
	@Column(name="comments")
	@Lob
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	@OneToMany(mappedBy = "accountEntry", cascade={CascadeType.REMOVE})
	public Set<AccountEntryDetail> getDetail() {
		return detail;
	}
	public void setDetail(Set<AccountEntryDetail> detail) {
		this.detail = detail;
	}
	
    @Transient
    public boolean mustRegenerateSummaryOnUpdate() {
		return regenerateSummaryOnUpdate;
	}
	public void setRegenerateSummaryOnUpdate(boolean regenerateSummaryOnUpdate) {
		this.regenerateSummaryOnUpdate = regenerateSummaryOnUpdate;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AccountEntry o = (AccountEntry) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.accountPeriod,o.accountPeriod)
				.append(this.comments,o.comments)		
				.append(this.entryDate,o.entryDate)		
				.append(this.type,o.type)
				.append(this.journal,o.journal)		
				.append(this.securityLevel,o.securityLevel)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(accountPeriod)
			.append(comments)
			.append(entryDate)		
			.append(type)
			.append(journal)		
			.append(securityLevel)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
}