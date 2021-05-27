package com.code.aon.accounting;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.audit.IAuditable;
import com.esferalia.aon.entity.master.AccountEntryDB;

@Entity
@Table(name="account_entry")
public class AccountEntry extends AccountEntryDB implements IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<AccountEntryDetail> detail = new HashSet<AccountEntryDetail>();
	
	@OneToMany(mappedBy = "accountEntry", cascade={CascadeType.REMOVE})
	public Set<AccountEntryDetail> getDetail() {
		return detail;
	}
	public void setDetail(Set<AccountEntryDetail> detail) {
		this.detail = detail;
	}

	@Transient
	public boolean isInvoiceEntry() {
        return getType() == AccountEntryType.SALES_INVOICE || getType() == AccountEntryType.PURCHASE_INVOICE || getType() == AccountEntryType.EXPENSE_INVOICE;
	}

}