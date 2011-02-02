package com.code.aon.accounting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;

/**
 * TransferObject that represents an AccountEntryDetail.
 */
@Entity
@Table(name = "account_entry_link")
public class AccountEntryLink implements ITransferObject {

	private static final long serialVersionUID = 8760825591290900116L;

	private Integer id;
	private AccountEntry entryFrom;
	private AccountEntry entryTo;
	
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
	@JoinColumn( name="account_entry_from", nullable=false )
	@ForeignKey(name = "FK_ACCOUNT_ENTRY_LINK_FROM")
	@Index(name = "IDX_ACCOUNT_ENTRY_ACCOUNT_ENTRY_FROM")			
	public AccountEntry getEntryFrom() {
		return entryFrom;
	}
	public void setEntryFrom(AccountEntry entryFrom) {
		this.entryFrom = entryFrom;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="account_entry_to", nullable=false )
	@ForeignKey(name = "FK_ACCOUNT_ENTRY_LINK_TO")
	@Index(name = "IDX_ACCOUNT_ENTRY_ACCOUNT_ENTRY_TO")			
	public AccountEntry getEntryTo() {
		return entryTo;
	}
	public void setEntryTo(AccountEntry entryTo) {
		this.entryTo = entryTo;
	}


}