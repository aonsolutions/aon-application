package com.code.aon.commercial;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;

import com.code.aon.config.IScopable;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.RegistryAttachment;
import com.esferalia.aon.entity.master.TargetDB;

@Entity
@Table(name="target")
public class Target extends TargetDB implements ITaxInfo, IRegistry, IScopable {
	
	private static final long serialVersionUID = 1L;

	private boolean customer;
	private Set<TargetItem> items = new HashSet<TargetItem>();
	private Set<TargetSeller> sellers = new HashSet<TargetSeller>();
	private Set<ProjectCommercial> projects = new HashSet<ProjectCommercial>();
	private Set<RegistryAttachment> documents = new HashSet<RegistryAttachment>();
	private Set<TargetProfile> profiles = new HashSet<TargetProfile>();
	
	@OneToMany(mappedBy = "target", cascade={CascadeType.REMOVE})	
	public Set<TargetItem> getItems() {
		return items;
	}

	public void setItems(Set<TargetItem> items) {
		this.items = items;
	}

	@OneToMany(mappedBy = "target", cascade={CascadeType.REMOVE})	
	public Set<TargetSeller> getSellers() {
		return sellers;
	}

	public void setSellers(Set<TargetSeller> sellers) {
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

	@OneToMany(mappedBy = "target", cascade={CascadeType.REMOVE})
	public Set<TargetProfile> getProfiles() {
		return profiles;
	}

	public void setProfiles(Set<TargetProfile> profiles) {
		this.profiles = profiles;
	}
	
	@Transient
	public boolean isTaxFree() {
		return (getTransaction() != InvoiceTransactionType.NATIONAL);
	}

	@Formula("(select COUNT(*) from customer c where registry = c.registry)")
	public boolean isCustomer() {
		return customer;
	}
	public void setCustomer(boolean customer) {
		this.customer = customer;
	}
		
}