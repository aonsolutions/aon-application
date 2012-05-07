package com.code.aon.company;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.code.aon.config.IScopable;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.RegistryAttachment;
import com.esferalia.aon.entity.master.EnterpriseDB;

@Entity
@Table(name="enterprise")
@PrimaryKeyJoinColumn(name="registry")
public class Enterprise extends EnterpriseDB implements IRegistry, IScopable {

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
