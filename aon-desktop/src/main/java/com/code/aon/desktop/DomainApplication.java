package com.code.aon.desktop;

import static com.code.aon.ldap.IAonObjectClasses.DOMAIN_APPLICATION;
import static com.code.aon.ldap.IAonObjectClasses.TOP;
import static com.code.aon.ldap.ILdapConstants.COMMON_NAME_ATTRIBUTE;
import static com.code.aon.ldap.ILdapConstants.STATUS_ATTRIBUTE;

import javax.naming.Name;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.BaseDN;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;

@EntryObject(mainObjectClass=DOMAIN_APPLICATION, objectClasses={TOP})
public class DomainApplication implements ITransferObject, Cloneable {

	private static final long serialVersionUID = -1729654908005345126L;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplication.class);

	private Name id;
	
	private String commonName;
	
	private DBConnnection dataSource;
	
	private Integer status;

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
	
	@Attribute(name=STATUS_ATTRIBUTE)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@BaseDN("ou=bds,{parent}")
	@Attribute(name="dataSource")
	public DBConnnection getDataSource() {
		return dataSource;
	}

	public void setDataSource(DBConnnection dataSource) {
		this.dataSource = dataSource;
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