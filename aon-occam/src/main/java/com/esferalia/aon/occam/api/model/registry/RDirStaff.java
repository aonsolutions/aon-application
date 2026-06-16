package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.watson.util.AonUtils;

public class RDirStaff implements Serializable {
	
	private static final long serialVersionUID = -1378893419649521080L;
	
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
	private Integer shareNumber;
	private Double nominalValue;
	private String chargeDescription;
	
	private boolean dirty;
	private boolean removed;
	
	
	public Integer getId() {
		return id;
	}
	public RDirStaff setId(Integer id) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.id , id) );
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public RDirStaff setDomain(Integer domain) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.domain , domain) );
		this.domain = domain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}
	public RDirStaff setRegistry(Integer registry) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.registry , registry) );
		this.registry = registry;
		return this;
	}

	public String getDocument() {
		return document;
	}
	public RDirStaff setDocument(String document) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.document , document) );
		this.document = document;
		return this;
	}

	public String getName() {
		return name;
	}
	public RDirStaff setName(String name) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.name, name) );
		this.name = name;
		return this;
	}

	public Boolean getShareHolder() {
		return shareHolder;
	}
	public RDirStaff setShareHolder(Boolean shareHolder) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.shareHolder, shareHolder) );
		this.shareHolder = shareHolder;
		return this;
	}

	public Boolean getRepresentative() {
		return representative;
	}
	public RDirStaff setRepresentative(Boolean representative) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.representative, representative) );
		this.representative = representative;
		return this;
	}

	public Boolean getDirector() {
		return director;
	}
	public RDirStaff setDirector(Boolean director) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.director, director) );
		this.director = director;
		return this;
	}

	public Boolean getRepresentativeLabor() {
		return representativeLabor;
	}
	public RDirStaff setRepresentativeLabor(Boolean representativeLabor) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.representativeLabor, representativeLabor) );
		this.representativeLabor = representativeLabor;
		return this;
	}

	public Date getDueDate() {
		return dueDate;
	}
	public RDirStaff setDueDate(Date dueDate) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.dueDate, dueDate) );
		this.dueDate = dueDate;
		return this;
	}

	public Double getPercentShare() {
		return percentShare;
	}
	public RDirStaff setPercentShare(Double percentShare) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.percentShare, percentShare) );
		this.percentShare = percentShare;
		return this;
	}

	public Integer getShareNumber() {
		return null == shareNumber ? 0 : shareNumber;
	}
	public RDirStaff setShareNumber(Integer shareNumber) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.shareNumber, shareNumber) );
		this.shareNumber = shareNumber;
		return this;
	}
	
	public Double getNominalValue() {
		return nominalValue;
	}
	public RDirStaff setNominalValue(Double nominalValue) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.nominalValue, nominalValue) );
		this.nominalValue = nominalValue;
		return this;
	}
	
	public String getChargeDescription() {
		return chargeDescription;
	}
	public RDirStaff setChargeDescription(String chargeDescription) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.chargeDescription, chargeDescription) );
		this.chargeDescription = chargeDescription;
		return this;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	public RDirStaff setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	public RDirStaff setRemoved(boolean removed) {
		this.removed = removed;
		return this;
	}
	
}
