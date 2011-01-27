package com.code.aon.manager;

import static com.code.aon.ldap.IAonObjectClasses.DOMAIN_APPLICATION;
import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;
import static com.code.aon.ldap.IAonObjectClasses.TOP;

import javax.naming.Name;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.dao.ldap.ILdapTransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.BaseDN;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.NameResolver;

@EntryObject(mainObjectClass=DOMAIN_APPLICATION, objectClasses={TOP})
public class DomainApplication implements ILdapTransferObject, Cloneable {

	private static final long serialVersionUID = -1729654908005345126L;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplication.class);

	private Name id;
	
	private String commonName;
	
	private DBConnnection dataSource;
	
	private Integer status;
	
	public DomainApplication() {
		this.status = 0;
	}

	@Id
	public Name getId() {
		return id;
	}

	public void setId(Name id) {
		this.id = id;
	}
	
	public String getDomain() {
		return NameResolver.getValue( getId(), 2 );
	}
	
	@RDN
	@Attribute(name=COMMON_NAME_ATTRIBUTE,nullable=false)
	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	
	@Attribute(name=STATUS_ATTRIBUTE)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Cascade(CascadeType.ALL)
	@BaseDN("ou=bds,{parent}")
	@Attribute(name=DATA_SOURCE_ATTRIBUTE)
	public DBConnnection getDataSource() {
		return dataSource;
	}

	public void setDataSource(DBConnnection dataSource) {
		this.dataSource = dataSource;
	}
	
	public void construct( BasicLdap ldap ) {
		String domain = getDomain();
		Name profilesDN = NameResolver.getDomainApplicationProfilesDN(domain, getCommonName());
		if (! ldap.exists(profilesDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(profilesDN);
		}
		Name usersDN = NameResolver.getDomainApplicationUsersDN(domain, getCommonName());
		if (! ldap.exists(usersDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(usersDN);
		}
	}
	
	public static void delete( BasicLdap ldap, Name dn ) {
		String application = NameResolver.getFirstValue(dn);
		String domain = NameResolver.getValue(dn, 2);
		Name users = NameResolver.getDomainApplicationUsersDN(domain, application);
		if ( ldap.exists(users, ORGANIZATIONAL_UNIT) ) {
			ldap.deleteDepth(users, true);
		}
		Name profiles = NameResolver.getDomainApplicationProfilesDN(domain, application);
		if ( ldap.exists(profiles, ORGANIZATIONAL_UNIT) ) {
			ldap.deleteDepth(profiles, true);
		}		
		ldap.deleteDepth(dn, true);
	}	
	
	@Override
	public Object clone() {
        Object obj=null;
        try {
            obj=super.clone();
        } catch (CloneNotSupportedException ex) {
        	LOGGER.error( "Error cloning DBConnection", ex );
        }
        return obj;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final DomainApplication o = (DomainApplication) obj;
		if (o.getCommonName() == null && getCommonName() == null) {
			return new EqualsBuilder()
				.append(this.commonName, o.commonName)
				.append(this.dataSource, o.dataSource)				
				.append(this.status, o.status)
				.isEquals();
		}
		return ObjectUtils.equals(getCommonName(), o.getCommonName());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(commonName)
			.append(dataSource)
			.append(status)	
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}