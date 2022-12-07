package com.esferalia.aon.occam.api.model.mod145;

import java.io.Serializable;

public class IrpfDataDescendients implements Serializable {
	
	private static final long serialVersionUID = -7570826244671231873L;
	
	private Integer id;
	private Integer domain;
	private Integer irpfData;
	private Integer birthYear;
	private Integer adoptionYear;
	private Byte disabilityLevel;
	private boolean dependence;
	private boolean uniqueParent;
	
	private boolean deleted;
	
	public IrpfDataDescendients() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public IrpfDataDescendients setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public IrpfDataDescendients setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getIrpfData() {
		return irpfData;
	}

	public IrpfDataDescendients setIrpfData(Integer irpfData) {
		this.irpfData = irpfData;
		return this;
	}

	public Integer getBirthYear() {
		return birthYear;
	}

	public IrpfDataDescendients setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
		return this;
	}

	public Integer getAdoptionYear() {
		return adoptionYear;
	}

	public IrpfDataDescendients setAdoptionYear(Integer adoptionYear) {
		this.adoptionYear = adoptionYear;
		return this;
	}

	public Byte getDisabilityLevel() {
		return disabilityLevel;
	}

	public IrpfDataDescendients setDisabilityLevel(Byte disabilityLevel) {
		this.disabilityLevel = disabilityLevel;
		return this;
	}

	public boolean isDependence() {
		return dependence;
	}

	public IrpfDataDescendients setDependence(boolean dependence) {
		this.dependence = dependence;
		return this;
	}

	public boolean isUniqueParent() {
		return uniqueParent;
	}

	public IrpfDataDescendients setUniqueParent(boolean uniqueParent) {
		this.uniqueParent = uniqueParent;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public IrpfDataDescendients setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

}
