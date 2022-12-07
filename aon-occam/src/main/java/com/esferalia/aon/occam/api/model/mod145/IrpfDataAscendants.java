package com.esferalia.aon.occam.api.model.mod145;

import java.io.Serializable;

public class IrpfDataAscendants implements Serializable {

	private static final long serialVersionUID = -4049259871378615089L;

	private Integer id;
	private Integer domain;
	private Integer irpfData;
	private Integer birthYear;
	private Byte disabilityLevel;
	private boolean dependence;
	private boolean anotherDescendient;
	
	private boolean deleted;
	
	public IrpfDataAscendants() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public IrpfDataAscendants setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public IrpfDataAscendants setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getIrpfData() {
		return irpfData;
	}

	public IrpfDataAscendants setIrpfData(Integer irpfData) {
		this.irpfData = irpfData;
		return this;
	}

	public Integer getBirthYear() {
		return birthYear;
	}

	public IrpfDataAscendants setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
		return this;
	}

	public Byte getDisabilityLevel() {
		return disabilityLevel;
	}

	public IrpfDataAscendants setDisabilityLevel(Byte disabilityLevel) {
		this.disabilityLevel = disabilityLevel;
		return this;
	}

	public boolean isDependence() {
		return dependence;
	}

	public IrpfDataAscendants setDependence(boolean dependence) {
		this.dependence = dependence;
		return this;
	}

	public boolean isAnotherDescendient() {
		return anotherDescendient;
	}

	public IrpfDataAscendants setAnotherDescendient(boolean anotherDescendient) {
		this.anotherDescendient = anotherDescendient;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public IrpfDataAscendants setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}
}
