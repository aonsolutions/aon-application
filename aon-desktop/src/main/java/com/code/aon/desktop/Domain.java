package com.code.aon.desktop;

import static com.code.aon.ldap.IAonObjectClasses.DOMAIN;
import static com.code.aon.ldap.IAonObjectClasses.TOP;
import static com.code.aon.ldap.ILdapConstants.COMMON_NAME_ATTRIBUTE;
import static com.code.aon.ldap.ILdapConstants.DOMAIN_MANAGEMENT_ATTRIBUTE;
import static com.code.aon.ldap.ILdapConstants.ORGANIZATION_NAME_ATTRIBUTE;

import javax.naming.Name;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.BaseDN;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;

@EntryObject(baseDN="ou=domains",mainObjectClass=DOMAIN, objectClasses={TOP})
public class Domain implements ITransferObject {

	private static final long serialVersionUID = -4808900608917312113L;

	public static final String  DOMAIN_PARENT_DOMAIN = "Domain_parentDomain";
	
	private Name id;
	
	private String commonName;
	
	private String organizationName;
	
	private Domain parentDomain;
	
	private boolean domainManagement;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Name getId() {
		return id;
	}

	public void setId(Name id) {
		this.id = id;
	}
	
	@RDN
	@Attribute(name=COMMON_NAME_ATTRIBUTE,nullable=false)
	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	
	@Attribute(name=ORGANIZATION_NAME_ATTRIBUTE)
	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	@Attribute(name=DOMAIN_MANAGEMENT_ATTRIBUTE)	
	public Boolean getDomainManagement() {
		return domainManagement;
	}

	public void setDomainManagement(Boolean domainManagement) {
		this.domainManagement = domainManagement;
	}

	@Cascade(CascadeType.ALL)
	@BaseDN("{this}")
	@Attribute(name="parentDomain")
	public Domain getParentDomain() {
		return parentDomain;
	}

	public void setParentDomain(Domain parentDomain) {
		this.parentDomain = parentDomain;
	}	

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Domain o = (Domain) obj;
		if (o.getCommonName() == null && getCommonName() == null) {
			return new EqualsBuilder()
				.append(this.commonName, o.commonName)
				.append(this.domainManagement, o.domainManagement)									
				.append(this.organizationName, o.organizationName)				
				.append(this.parentDomain, o.parentDomain)				
				.isEquals();
		}
		return ObjectUtils.equals(getCommonName(), o.getCommonName());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(commonName)
			.append(domainManagement)
			.append(organizationName)
			.append(parentDomain)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}