package com.code.aon.finance;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Formula;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.account.IAccount;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.config.IScopable;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.CreditorStatus;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.RegistryAddInfo;
import com.code.aon.registry.RegistryAttachment;
import com.esferalia.aon.entity.master.CreditorDB;

@Entity
@Table(name="creditor")
public class Creditor extends CreditorDB implements IRegistry, ITaxInfo, IScopable, IAccount, IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Date updateDate;
	
	private Set<RegistryAttachment> documents = new HashSet<RegistryAttachment>();
	private Set<RegistryAddInfo> addInfos = new HashSet<RegistryAddInfo>();    

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

	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAddInfo> getAddInfos() {
		return addInfos;
	}
	public void setAddInfos(Set<RegistryAddInfo> addInfos) {
		this.addInfos = addInfos;
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
		return (getTransaction() == InvoiceTransactionType.INTRACOMMUNITY || getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY);
	}
	
    @Formula("IFNULL(modification_date, creation_date)")
    public Date getUpdateDate() {
    	return updateDate;
    }
    
    public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
}
