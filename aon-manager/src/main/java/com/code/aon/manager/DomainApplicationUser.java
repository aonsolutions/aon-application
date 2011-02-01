/*
 * Created on 10-mar-2005
 *
 */
package com.code.aon.manager;

import static com.code.aon.ldap.IAonObjectClasses.DOMAIN_APPLICATION_USER;
import static com.code.aon.ldap.IAonObjectClasses.GROUP_OF_NAMES;
import static com.code.aon.ldap.IAonObjectClasses.TOP;

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
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;
import com.code.aon.ldap.NameResolver;

/**
 * 
 * @author Consulting & Development. Aimar Tellitu - 10-mar-20059
 * @since 1.0
 *
 */
@EntryObject(mainObjectClass=DOMAIN_APPLICATION_USER, objectClasses={TOP, GROUP_OF_NAMES})
public class DomainApplicationUser implements ILdapTransferObject {

	private static final long serialVersionUID = -8564661655482298057L;

	/** Security domain identifier. This can be Nominal or Concurrent. */
	private Name id;
	
	private String commonName;	
	
	private List<Name> profiles;
	
	private String businessCategory;
	
	private String description;
	
	private String organizationName;
	
	private String organizationUnitName;
	
	private Integer status;
	
	public DomainApplicationUser() {
		this.status = 0;
	}

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

	public String getAppplication() {
		return NameResolver.getValue( getId(), 2 );
	}

	public String getDomain() {
		return NameResolver.getValue( getId(), 4 );
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
	
	@Attribute(name=STATUS_ATTRIBUTE)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}	
	
	@Attribute(name=MEMBER_ATTRIBUTE, baseClass="javax.naming.ldap.LdapName")
	public List<Name> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<Name> profiles) {
		this.profiles = profiles;
	}
	
	public String getProfileList() {
		if ( this.profiles != null ) {
			List<String> list = new LinkedList<String>();
			for( Name name : this.profiles ) {
				if ( name != null ) {
					list.add( NameResolver.getValue(name, 0));
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
		final DomainApplicationUser o = (DomainApplicationUser) obj;
		if (o.getCommonName() == null && getCommonName() == null) {
			return new EqualsBuilder()
				.append(this.businessCategory, o.businessCategory)				
				.append(this.description, o.description)
				.append(this.organizationName, o.organizationName)
				.append(this.organizationUnitName, o.organizationUnitName)
				.append(this.status, o.status)
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
			.append(status)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

}