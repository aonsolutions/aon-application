package com.code.aon.customer;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Where;

import com.code.aon.account.IAccount;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.config.IScopable;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITariffable;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.RegistryAddInfo;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.RegistryProfile;
import com.code.aon.registry.RegistrySeller;
import com.esferalia.aon.entity.master.CustomerDB;

@Entity
@Table(name="customer")
public class Customer extends CustomerDB implements IRegistry, ITaxInfo, IScopable, IAccount, ITariffable, IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Date updateDate;
	
	private boolean skipUpdateTarget;

	private Set<RegistryAttachment> documents = new HashSet<RegistryAttachment>();
	private Set<RegistryItem> items = new HashSet<RegistryItem>();
	private Set<RegistrySeller> sellers = new HashSet<RegistrySeller>();
	private Set<RegistryProfile> profiles = new HashSet<RegistryProfile>();    
	private Set<RegistryAddInfo> addInfos = new HashSet<RegistryAddInfo>();    

    public Customer() {
    	setTransaction(InvoiceTransactionType.NATIONAL);
    	setStatus(CustomerStatus.ACTIVE);
    	setProjectGrouped(true);
    	setDeliveryGrouped(true);
    	setDeliveryValuated(true);
    	setSkipUpdateTarget(false);
    }

	@Transient
	public boolean isSkipUpdateTarget() {
		return skipUpdateTarget;
	}
	public void setSkipUpdateTarget(boolean skipUpdateCustomer) {
		this.skipUpdateTarget = skipUpdateCustomer;
	}

    @OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAttachment> getDocuments() {
		return documents;
	}
	public void setDocuments(Set<RegistryAttachment> documents) {
		this.documents = documents;
	}	
	
	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})	
	@Where(clause = "type=1")
	public Set<RegistryItem> getItems() {
		return items;
	}
	public void setItems(Set<RegistryItem> items) {
		this.items = items;
	}

	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})	
	public Set<RegistrySeller> getSellers() {
		return sellers;
	}
	public void setSellers(Set<RegistrySeller> sellers) {
		this.sellers = sellers;
	}

	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryProfile> getProfiles() {
		return profiles;
	}
	public void setProfiles(Set<RegistryProfile> profiles) {
		this.profiles = profiles;
	}	

	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAddInfo> getAddInfos() {
		return addInfos;
	}
	public void setAddInfos(Set<RegistryAddInfo> addInfos) {
		this.addInfos = addInfos;
	}	

    @Transient
	public Customer getInvoicingCustomer() {
		return (getInvoicingGroup() != null && getInvoicingGroup().getId() != null) ? getInvoicingGroup().getCustomer() : this;
    }

	@Transient
	public boolean isWithholdingFarmer() {
		return false;
	}
	
	@Transient
	public boolean isVatAccrualPayment() {
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
