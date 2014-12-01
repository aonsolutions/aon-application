package com.code.aon.finance;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.account.IAccount;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.config.IScopable;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.CreditorStatus;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.RegistryAttachment;
import com.esferalia.aon.entity.master.CreditorDB;

@Entity
@Table(name="creditor")
public class Creditor extends CreditorDB implements IRegistry, ITaxInfo, IScopable, IAccount, IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<RegistryAttachment> documents = new HashSet<RegistryAttachment>();

	public Creditor() {
    	setTransaction(InvoiceTransactionType.NATIONAL);
    	setStatus(CreditorStatus.ACTIVE);
	}

	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAttachment> getDocuments() {
		return documents;
	}
	public void setDocuments(Set<RegistryAttachment> documents) {
		this.documents = documents;
	}

	@Transient
	public boolean isSurcharge() {
		return false;
	}

	@Transient
	public boolean isWithholdingFarmer() {
		return false;
	}

	@Transient
	public boolean isVatFree() {
		return (getTransaction() != InvoiceTransactionType.NATIONAL);
	}

	@Transient
	public boolean isRetentionFree() {
		return (getTransaction() != InvoiceTransactionType.NATIONAL && getTransaction() != InvoiceTransactionType.OTHER_ISP);
	}
	
}
