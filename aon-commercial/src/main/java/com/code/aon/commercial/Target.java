package com.code.aon.commercial;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;

import com.code.aon.commercial.enumeration.Advertising;
import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.config.IScopable;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.RegistryProfile;
import com.code.aon.registry.RegistrySeller;
import com.esferalia.aon.entity.master.TargetDB;

@Entity
@Table(name="target")
public class Target extends TargetDB implements ITaxInfo, IRegistry, IScopable {
	
	private static final long serialVersionUID = 1L;

	private boolean customer;
	private Set<RegistryItem> items = new HashSet<RegistryItem>();
	private Set<RegistrySeller> sellers = new HashSet<RegistrySeller>();
	private Set<ProjectCommercial> projects = new HashSet<ProjectCommercial>();
	private Set<RegistryAttachment> documents = new HashSet<RegistryAttachment>();
	private Set<RegistryProfile> profiles = new HashSet<RegistryProfile>();

	public Target() {
    	setTransaction(InvoiceTransactionType.NATIONAL);
    	setStatus(TargetStatus.ACTIVE);
    	setAdvertising(Advertising.ALLOWED);
	}

	@Formula("(select COUNT(*) from customer c where registry = c.registry)")
	public boolean isCustomer() {
		return customer;
	}

	public void setCustomer(boolean customer) {
		this.customer = customer;
	}
		
	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})	
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

	@OneToMany(mappedBy = "target", cascade={CascadeType.REMOVE})
	public Set<ProjectCommercial> getProjects() {
		return projects;
	}

	public void setProjects(Set<ProjectCommercial> projects) {
		this.projects = projects;
	}
	
	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAttachment> getDocuments() {
		return documents;
	}
	
	public void setDocuments(Set<RegistryAttachment> documents) {
		this.documents = documents;
	}	

	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryProfile> getProfiles() {
		return profiles;
	}

	public void setProfiles(Set<RegistryProfile> profiles) {
		this.profiles = profiles;
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