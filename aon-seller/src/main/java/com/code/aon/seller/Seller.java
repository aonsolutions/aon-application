package com.code.aon.seller;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.code.aon.registry.IRegistry;
import com.code.aon.registry.RegistryAttachment;
import com.esferalia.aon.entity.master.SellerDB;

@Entity
@Table(name="seller")
public class Seller extends SellerDB implements IRegistry {

	private static final long serialVersionUID = 1L;

	private Set<RegistryAttachment> documents = new HashSet<RegistryAttachment>();

	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAttachment> getDocuments() {
		return documents;
	}
	public void setDocuments(Set<RegistryAttachment> documents) {
		this.documents = documents;
	}	

}