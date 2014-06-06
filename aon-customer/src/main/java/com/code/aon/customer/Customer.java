package com.code.aon.customer;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Where;

import com.code.aon.account.IAccount;
import com.code.aon.config.IScopable;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITariffable;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.RegistryProfile;
import com.code.aon.registry.RegistrySeller;
import com.esferalia.aon.entity.master.CustomerDB;

@Entity
@Table(name="customer")
public class Customer extends CustomerDB implements ITaxInfo, IRegistry, IScopable, IAccount, ITariffable {
	
	private static final long serialVersionUID = 1L;

    private Set<RegistryAttachment> documents = new HashSet<RegistryAttachment>();
	private Set<RegistryItem> items = new HashSet<RegistryItem>();
	private Set<RegistrySeller> sellers = new HashSet<RegistrySeller>();
	private Set<RegistryProfile> profiles = new HashSet<RegistryProfile>();    

    public Customer() {
    	setTransaction(InvoiceTransactionType.NATIONAL);
    	setStatus(CustomerStatus.ACTIVE);
    	setProjectGrouped(true);
    	setDeliveryGrouped(true);
    	setDeliveryValuated(true);
    }

    @OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAttachment> getDocuments() {
		return documents;
	}
	public void setDocuments(Set<RegistryAttachment> documents) {
		this.documents = documents;
	}	
	
	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})	
	@Where(clause = "type=0")
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
		return (getTransaction() != InvoiceTransactionType.NATIONAL && getTransaction() != InvoiceTransactionType.OTHER_ISP);
	}
	
}
