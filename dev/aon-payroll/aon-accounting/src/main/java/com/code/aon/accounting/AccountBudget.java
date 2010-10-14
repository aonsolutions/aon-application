package com.code.aon.accounting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
@Table(name = "account_budget")
public class AccountBudget implements ITransferObject, IConfidentialable {
	
	private static final long serialVersionUID = -8220548741479656597L;

	private Integer id;
	private String period;
    private Account account;
    private SecurityLevel securityLevel;

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
	@ForeignKey(name = "FK_ACCOUNT_BUDGET_PERIOD")
	@Index(name = "IDX_ACCOUNT_BUDGET_ACCOUNT_PERIOD")	
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="account", nullable=false )
	@ForeignKey(name = "FK_ACCOUNT_BUDGET_ACCOUNT")
	@Index(name = "IDX_ACCOUNT_BUDGET_ACCOUNT")
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() !=  getClass()) return false;
		final AccountBudget o = (AccountBudget) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.period, o.period)
			.append(this.account, o.account)
			.append(this.securityLevel, o.securityLevel)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.id)
			.append(this.period)
			.append(this.account)
			.append(this.securityLevel)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}