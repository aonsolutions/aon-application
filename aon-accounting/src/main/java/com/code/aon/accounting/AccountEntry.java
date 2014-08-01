package com.code.aon.accounting;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.AccountEntryDB;

@Entity
@Table(name="account_entry")
public class AccountEntry extends AccountEntryDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<AccountEntryDetail> detail = new HashSet<AccountEntryDetail>();
	
	@OneToMany(mappedBy = "accountEntry", cascade={CascadeType.REMOVE})
	public Set<AccountEntryDetail> getDetail() {
		return detail;
	}
	public void setDetail(Set<AccountEntryDetail> detail) {
		this.detail = detail;
	}
}