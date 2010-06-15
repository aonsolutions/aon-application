package com.code.aon.webmail;

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
import com.code.aon.ldap.IAonObjectClasses;

@EntryObject(mainObjectClass=IAonObjectClasses.CONTACT, objectClasses={IAonObjectClasses.TOP})
public class GroupContact implements ILdapTransferObject {

	private static final long serialVersionUID = 2735479153538882108L;

	private Name id;
	
	private String displayName;
	
	private String email;
	
	@Id
	public Name getId() {
		return id;
	}

	public void setId(Name id) {
		this.id = id;
	}
	
	@RDN
	@Attribute(name="displayName")
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Attribute(name="mail",length=64)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailLarge() {
		if ( getEmail() != null ) {
			String name = getDisplayName();
			if (! StringUtils.isAsciiPrintable(name) ) {
				name = "\"" + name + "\"";
			}
			return name + " &lt;" + getEmail() + "&gt;";			
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final GroupContact o = (GroupContact) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.displayName, o.displayName)
				.append(this.email, o.email)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(displayName)
			.append(email)
			.append(id)	
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}