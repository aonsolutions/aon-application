package com.code.aon.customer;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.config.IScopable;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.RegistryAttachment;
import com.esferalia.aon.entity.master.CustomerDB;

@Entity
@Table(name="customer")
public class Customer extends CustomerDB implements ITaxInfo,IScopable,IRegistry{
	
	private static final long serialVersionUID = 1L;

    private Set<RegistryAttachment> documents = new HashSet<RegistryAttachment>();

    public Customer() {
    	setTransaction(InvoiceTransactionType.NATIONAL);
    	setStatus(CustomerStatus.ACTIVE);
    	setProjectGrouped(true);
    	setDeliveryGrouped(true);
    	setDeliveryValuated(true);
    }

    @Transient
	public Customer getInvoicingCustomer() {
		return (getInvoicingGroup() != null && getInvoicingGroup().getId() != null) ? getInvoicingGroup().getCustomer() : this;
    }

    @OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAttachment> getDocuments() {
		return documents;
	}
	public void setDocuments(Set<RegistryAttachment> documents) {
		this.documents = documents;
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
