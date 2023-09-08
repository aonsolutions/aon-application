package com.code.aon.seller;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.config.IScopable;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.RegistryAddInfo;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.seller.enumeration.SellerStatus;
import com.esferalia.aon.entity.master.SellerDB;

@Entity
@Table(name="seller")
public class Seller extends SellerDB implements IRegistry, IScopable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<RegistryAttachment> documents = new HashSet<RegistryAttachment>();
	private Set<RegistryAddInfo> addInfos = new HashSet<RegistryAddInfo>();

	public Seller() {
    	setStatus(SellerStatus.ACTIVE);
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

}