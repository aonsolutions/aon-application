package com.esferalia.aon.occam.api.model.registry;

import java.util.Date;

public class RDirStaff {
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private String document;
	private String name;
	
	private Boolean shareHolder;
	private Boolean representative;
	private Boolean director;
	private Boolean representativeLabor;
	
	private Date dueDate;
	private Double percentShare;
	private Double nominalValue;
	private String ChargeDescription;
	
	public RDirStaff() {

	}

	public Integer getId() {
		return id;
	}

	public RDirStaff setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public RDirStaff setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}

	public RDirStaff setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public RDirStaff setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getName() {
		return name;
	}

	public RDirStaff setName(String name) {
		this.name = name;
		return this;
	}

	public Boolean getShareHolder() {
		return shareHolder;
	}

	public RDirStaff setShareHolder(Boolean shareHolder) {
		this.shareHolder = shareHolder;
		return this;
	}

	public Boolean getRepresentative() {
		return representative;
	}

	public RDirStaff setRepresentative(Boolean representative) {
		this.representative = representative;
		return this;
	}

	public Boolean getDirector() {
		return director;
	}

	public RDirStaff setDirector(Boolean director) {
		this.director = director;
		return this;
	}

	public Boolean getRepresentativeLabor() {
		return representativeLabor;
	}

	public RDirStaff setRepresentativeLabor(Boolean representativeLabor) {
		this.representativeLabor = representativeLabor;
		return this;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public RDirStaff setDueDate(Date dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	public Double getPercentShare() {
		return percentShare;
	}

	public RDirStaff setPercentShare(Double percentShare) {
		this.percentShare = percentShare;
		return this;
	}

	public Double getNominalValue() {
		return nominalValue;
	}
	
	public RDirStaff setNominalValue(Double nominalValue) {
		this.nominalValue = nominalValue;
		return this;
	}
	
	public String getChargeDescription() {
		return ChargeDescription;
	}
	
	public RDirStaff setChargeDescription(String chargeDescription) {
		ChargeDescription = chargeDescription;
		return this;
	}
}
