/*
 * Created on 10-mar-2005
 *
 */
package com.code.aon.manager;

import java.util.LinkedList;
import java.util.List;

import javax.naming.Name;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.dao.ldap.ILdapTransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.BaseDN;
import com.code.aon.dao.ldap.annotations.RDN;

/**
 * 
 * @author Consulting & Development. Aimar Tellitu - 10-mar-20059
 * @since 1.0
 *
 */
public class BasicProfile implements ILdapTransferObject {

	/** Security domain identifier. This can be Nominal or Concurrent. */
	private Name id;
	
	private String commonName;	
	
	private List<Role> roles;
	
	private String businessCategory;
	
	private String description;
	
	private String organizationName;
	
	private String organizationUnitName;

	@Id
	public Name getId() {
		return id;
	}
	
	/**
	 * @param id The id to set.
	 */
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
	
	@Attribute(name=DESCRIPTION_ATTRIBUTE, length=1024)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Attribute(name=ORGANIZATION_NAME_ATTRIBUTE)
	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	@Attribute(name=ORGANIZATIONAL_UNIT_NAME_ATTRIBUTE)
	public String getOrganizationUnitName() {
		return organizationUnitName;
	}

	public void setOrganizationUnitName(String organizationUnitName) {
		this.organizationUnitName = organizationUnitName;
	}
	
	@Attribute(name=BUSINESS_CATEGORY_ATTRIBUTE,length=128)
	public String getBusinessCategory() {
		return businessCategory;
	}

	public void setBusinessCategory(String businessCategory) {
		this.businessCategory = businessCategory;
	}	
	
	@BaseDN("ou=roles,{parent}")	
	@Attribute(name=MEMBER_ATTRIBUTE,baseClass="com.code.aon.manager.Role")
	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public String getRoleList() {
		if ( this.roles != null ) {
			List<String> list = new LinkedList<String>();
			for( Role role : this.roles ) {
				if ( role != null ) {
					list.add(role.getCommonName());
				}
			}
			return StringUtils.join(list, ", ");
		}
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final BasicProfile o = (BasicProfile) obj;
		if (o.getCommonName() == null && getCommonName() == null) {
			return new EqualsBuilder()
				.append(this.businessCategory, o.businessCategory)				
				.append(this.description, o.description)
				.append(this.organizationName, o.organizationName)
				.append(this.organizationUnitName, o.organizationUnitName)
				.isEquals();
		}
		return ObjectUtils.equals(getCommonName(), o.getCommonName());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(businessCategory)
			.append(commonName)
			.append(description)
			.append(organizationName)
			.append(organizationUnitName)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

}