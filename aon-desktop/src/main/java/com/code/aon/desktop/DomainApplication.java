package com.code.aon.desktop;

import java.util.logging.Logger;

import javax.naming.Name;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.BaseDN;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;
import com.code.aon.ldap.IAonObjectClasses;

@EntryObject(mainObjectClass=IAonObjectClasses.DOMAIN_APPLICATION, objectClasses={IAonObjectClasses.TOP})
public class DomainApplication implements ITransferObject, Cloneable {

	private static final long serialVersionUID = -1729654908005345126L;
	
	private static final Logger LOGGER = Logger.getLogger(DomainApplication.class.getName());;

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
	@Attribute(name="cn",nullable=false)
	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	
	@Attribute(name="status")
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
        	LOGGER.severe( "Error cloning DBConnection" );
        }
        return obj;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof DomainApplication) {
			DomainApplication o = (DomainApplication) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.id != null) ? id.hashCode() : super.hashCode();
	}	
	
}