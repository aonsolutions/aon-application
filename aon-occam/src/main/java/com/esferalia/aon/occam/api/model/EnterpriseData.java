package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.watson.util.AonStringUtils;

public class EnterpriseData implements Serializable, HasAudit {
	
	private static final String ICC_PREFIX = "ICC_";

	private static final long serialVersionUID = 3926586279837688509L;
	
	private Integer id;
	private Integer domain;
	private Integer enterprise;
	private String name;
	private String expression;
	private Date startDate;
	private Date endDate;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;	
    
	private boolean deleted;
	private boolean updated;
	
	public Integer getId() {
		return id;
	}
	public EnterpriseData setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public EnterpriseData setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getEnterprise() {
		return enterprise;
	}
	public EnterpriseData setEnterprise(Integer enterprise) {
		this.enterprise = enterprise;
		return this;
	}

	public String getName() {
		return name;
	}
	public EnterpriseData setName(String name) {
		this.name = name;
		return this;
	}

	public EnterpriseDataNames getDataName() {
		return EnterpriseDataNames.safeValueOf(getName()).orElse(null);
	}
	public EnterpriseData setDataName( EnterpriseDataNames name) {
		setName(name == null ? null : name.name());
		return this;
	}
 
 	public String getExpression() {
		return expression;
	}
	public EnterpriseData setExpression(String expression) {
		this.expression = expression;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}
	public EnterpriseData setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}
	public EnterpriseData setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	public EnterpriseData setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	@Deprecated public boolean isRemoved() {return isDeleted();}
	@Deprecated public EnterpriseData setIsRemoved(boolean isRemoved) {return setDeleted(isRemoved);}
	
	public boolean isUpdated() {
		return updated;
	}
	public EnterpriseData setUpdated(boolean updated) {
		this.updated = updated;
		return this;
	}

	// ---------------------------------------------------------- AUDIT
	@Override
	public String getCreationUser() {
		return creationUser;
	}

	public EnterpriseData setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	public EnterpriseData setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	@Override
	public String getModificationUser() {
		return modificationUser;
	}

	public EnterpriseData setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	@Override
	public Date getModificationDate() {
		return modificationDate;
	}

	public EnterpriseData setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	public boolean isCommunicationData() {
		return AonStringUtils.startsWith(getName(), ICC_PREFIX);
	}

	public boolean isUnique() {
		return isCommunicationData();
	}

	public boolean allowsOverlap() {
		return !isCommunicationData();
	}

	public boolean allowsNullStartDate() {
		return !isCommunicationData();
	}
}
