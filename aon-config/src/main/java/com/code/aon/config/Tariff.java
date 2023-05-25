package com.code.aon.config;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.TariffDB;

@Entity
@Table(name="tariff")
@Heritable
public class Tariff extends TariffDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    private Set<TariffCatalogue> catalogues = new HashSet<TariffCatalogue>();
    private Set<TariffAddInfo> addInfos = new HashSet<TariffAddInfo>();

	public Tariff() {
		setActive(true);
	}

    @OneToMany(mappedBy = "tariff", cascade={CascadeType.REMOVE})
	public Set<TariffCatalogue> getCatalogues() {
		return catalogues;
	}
	public void setCatalogues(Set<TariffCatalogue> catalogues) {
		this.catalogues = catalogues;
	}	

	@OneToMany(mappedBy = "tariff", cascade={CascadeType.REMOVE})
	public Set<TariffAddInfo> getAddInfos() {
		return addInfos;
	}
	public void setAddInfos(Set<TariffAddInfo> addInfos) {
		this.addInfos = addInfos;
	}	

} 
