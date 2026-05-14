package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class EnterpriseData implements Serializable, HasAudit {

	private static final String ICC_PREFIX = "ICC_";

	private static final long serialVersionUID = 3926586279837688509L;
	
	private boolean deleted;
	private boolean dirty;
	
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
	
	public boolean isDeleted() {
		return deleted;
	}
	public boolean isNotDeleted() {
		return !isDeleted();
	}
	public EnterpriseData setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	public boolean isDirty() {
		return dirty;
	}
	public boolean isNotDirty() {
		return !isDirty();
	}
	public EnterpriseData setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	
	public Integer getId() {
		return id;
	}
	public EnterpriseData setId(Integer id) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.id , id) );
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public EnterpriseData setDomain(Integer domain) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.domain, domain) );
		this.domain = domain;
		return this;
	}

	public Integer getEnterprise() {
		return enterprise;
	}
	public EnterpriseData setEnterprise(Integer enterprise) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.enterprise, enterprise) );
		this.enterprise = enterprise;
		return this;
	}

	public String getName() {
		return name;
	}
	public EnterpriseData setName(String name) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.name , name) );
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
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.expression , expression) );
		this.expression = expression;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}
	public EnterpriseData setStartDate(Date startDate) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.startDate , startDate) );
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}
	public EnterpriseData setEndDate(Date endDate) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.endDate , endDate) );
		this.endDate = endDate;
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
	
	public boolean inRange(Date atDate) {
		if (atDate == null) return false;
		return getStartDate() != null 
			&& !atDate.before(getStartDate())
			&& (getEndDate() == null || !atDate.after(getEndDate()))
		;
	}

	public boolean isCommunicationData() {
		return AonStringUtils.startsWith( getName(), ICC_PREFIX);
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
