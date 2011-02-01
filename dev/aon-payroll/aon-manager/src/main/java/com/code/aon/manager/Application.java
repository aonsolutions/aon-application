package com.code.aon.manager;

import static com.code.aon.ldap.IAonObjectClasses.APPLICATION;
import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;
import static com.code.aon.ldap.IAonObjectClasses.TOP;

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
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.NameResolver;

@EntryObject(baseDN="ou=applications",mainObjectClass=APPLICATION, objectClasses={TOP})
public class Application implements ILdapTransferObject {

	private static final long serialVersionUID = 8038659081389444525L;

	private Name id;
	
	private String commonName;
	
	private String description;
	
	private String serverId;
	
	private Boolean contratable;
	
	@Id
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

	@Attribute(name=DESCRIPTION_ATTRIBUTE,length=1024,nullable=false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Attribute(name="serverId",nullable=false)
	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	@Attribute(name="contratable")
	public Boolean getContratable() {
		return contratable;
	}

	public void setContratable(Boolean contratable) {
		this.contratable = contratable;
	}

	public static void delete( BasicLdap ldap, Name dn ) {
		String application = NameResolver.getFirstValue(dn);
		Name profilesDN = NameResolver.getApplicationProfilesDN(application);
		if ( ldap.exists(profilesDN, ORGANIZATIONAL_UNIT) ) {
			ldap.deleteDepth(profilesDN, true);
		}
		Name rolesDN = NameResolver.getApplicationRolesDN(application);
		if ( ldap.exists(rolesDN, ORGANIZATIONAL_UNIT) ) {
			ldap.deleteDepth(rolesDN, true);
		}
		ldap.deleteDepth(dn, true);
	}	
	
	public void construct( BasicLdap ldap ) {
		Name rolesDN = NameResolver.getApplicationRolesDN(getCommonName());
		if (! ldap.exists(rolesDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(rolesDN);
		}
		Name profilesDN = NameResolver.getApplicationProfilesDN(getCommonName());
		if (! ldap.exists(profilesDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(profilesDN);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Application o = (Application) obj;
		if (o.getCommonName() == null && getCommonName() == null) {
			return new EqualsBuilder()
				.append(this.commonName, o.commonName)
				.append(this.contratable, o.contratable)
				.append(this.description, o.description)				
				.append(this.serverId, o.serverId)				
				.isEquals();
		}
		return ObjectUtils.equals(getCommonName(), o.getCommonName());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(commonName)
			.append(contratable)
			.append(description)
			.append(serverId)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("commonName", commonName ).
			append("contratable", contratable ).
			append("description", StringUtils.abbreviate(description, 64)).
			append("serverId", serverId ).
			toString();
	}
	
}