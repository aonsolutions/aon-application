package com.code.aon.accounting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.common.enumeration.SecurityLevel;

@Entity
@Table(name = "account_summary")
public class AccountSummary implements ITransferObject,IConfidentialable {
	
	private static final long serialVersionUID = -8220548741479656597L;

	private Integer id;
	private String accountPeriod;
	private Account account;
    private SecurityLevel securityLevel;
    private Date entryDate;
	private double debit;
	private double credit;

	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="account_period", length=4, nullable=false)
	@Index(name = "IDX_ACCOUNT_SUMMARY_ACCOUNT_PERIOD")
	public String getAccountPeriod() {
		return accountPeriod;
	}
	public void setAccountPeriod(String accountPeriod) {
		this.accountPeriod = accountPeriod;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="account", nullable=false )
	@ForeignKey(name = "FK_ACCOUNT_SUMMARY_ACCOUNT")
	@Index(name = "IDX_ACCOUNT_SUMMARY_ACCOUNT")				
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
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

	@Column(name="entry_date")
    @Temporal(TemporalType.DATE)
    public Date getEntryDate() {
        return entryDate;
    }
    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    @Column(nullable=true)
    public double getDebit() {
        return debit;
    }
    public void setDebit(double debit) {
        this.debit = debit;
    }

    @Column(nullable=true)
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() !=  getClass()) return false;
		final AccountSummary o = (AccountSummary) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.accountPeriod, o.accountPeriod)
			.append(this.account, o.account)
			.append(this.securityLevel, o.securityLevel)
			.append(this.entryDate, o.entryDate)
			.append(this.debit, o.debit)
			.append(this.credit, o.credit)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.id)
			.append(this.accountPeriod)
			.append(this.account)
			.append(this.securityLevel)
			.append(this.entryDate)
			.append(this.debit)
			.append(this.credit)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}