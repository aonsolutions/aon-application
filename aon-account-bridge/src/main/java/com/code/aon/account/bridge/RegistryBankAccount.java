package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.registry.RegistryBank;

@Entity
@Table(name="rbank_account")
public class RegistryBankAccount implements ITransferObject, IAccount {
	
	private static final long serialVersionUID = -327952223428600895L;

	private Integer id;
	private RegistryBank registryBank;
	private Account account;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="rbank", nullable = false)
	@ForeignKey(name="FK_RBANK_ACCOUNT_RBANK")
	@Index(name="IDX_RBANK_ACCOUNT_RBANK")							
	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	@ManyToOne
	@JoinColumn(name="account", nullable = false)
	@ForeignKey(name="FK_RBANK_ACCOUNT_ACCOUNT")
	@Index(name="IDX_RBANK_ACCOUNT_ACCOUNT")						
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	
	@Transient
	public ITransferObject getLinkedTo() {
		return getRegistryBank();
	}
	public void setLinkedTo(ITransferObject to) {
		setRegistryBank((RegistryBank) to);
	}	

	@Transient
	public String getAccountDescription() {
		return (getRegistryBank()==null) ? null : getRegistryBank().getFullName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final RegistryBankAccount o = (RegistryBankAccount) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.account,o.account)
				.append(this.registryBank,o.registryBank)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.account)
			.append(this.registryBank)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}