package com.code.aon.supplier;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.config.IScopable;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.RegistryAttachment;
import com.esferalia.aon.entity.master.SupplierDB;

@Entity
@Table(name="supplier")
public class Supplier extends SupplierDB implements ITaxInfo, IScopable, IRegistry {
	
	private static final long serialVersionUID = 1L;

	private Set<RegistryAttachment> documents = new HashSet<RegistryAttachment>();

	@Transient
	public boolean isSurcharge() {
		return false;
	}
	@Transient
	public boolean isTaxFree() {
		return (getTransaction() != InvoiceTransactionType.NATIONAL);
	}
	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAttachment> getDocuments() {
		return documents;
	}
	
	public void setDocuments(Set<RegistryAttachment> documents) {
		this.documents = documents;
	}	

}