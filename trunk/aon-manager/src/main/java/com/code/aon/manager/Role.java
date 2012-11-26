/*
 * Created on 10-mar-2005
 *
 */
package com.code.aon.manager;

import static com.code.aon.ldap.IAonObjectClasses.ROLE;
import static com.code.aon.ldap.IAonObjectClasses.TOP;

import javax.naming.Name;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.dao.ldap.ILdapTransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;
import com.code.aon.ldap.ILdapConstants;

/**
 * 
 * @author Consulting & Development. Aimar Tellitu - 10-mar-20059
 * @since 1.0
 *
 */
@EntryObject(mainObjectClass=ROLE, objectClasses={TOP})
public class Role implements ILdapTransferObject {

	private static final long serialVersionUID = -3515823504544911369L;

	/** Security domain identifier. This can be Nominal or Concurrent. */
	private Name id;
	
	private String commonName;	

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
	@Attribute(name=ILdapConstants.COMMON_NAME_ATTRIBUTE,nullable=false)
	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}	

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Role o = (Role) obj;
		return ObjectUtils.equals(getCommonName(), o.getCommonName());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(commonName)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

}