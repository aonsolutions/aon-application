package com.code.aon.desktop;

import javax.naming.Name;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.code.aon.common.ITransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.BaseDN;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;
import com.code.aon.ldap.IAonObjectClasses;

@EntryObject(baseDN="ou=domains",mainObjectClass=IAonObjectClasses.DOMAIN, objectClasses={IAonObjectClasses.TOP})
public class Domain implements ITransferObject {

	private static final long serialVersionUID = -4808900608917312113L;

	private Name id;
	
	private String commonName;
	
	private String organizationName;
	
	private String host;
	
	private String mail;
	
	private String mobile;
	
	private Integer status;
	
	private Domain parentDomain;
	
	private boolean dnsManagement;
	
	private boolean userManagement;
	
	private boolean domainManagement;
	
	private byte[] jpegLogo;
	
	public Domain() {
		this.status = 0;
	}

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
	
	@Attribute(name="o")
	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	@Attribute(name="host",length=256)
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Attribute(name="mail",length=256)
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Attribute(name="mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Attribute(name="dnsManagement")
	public Boolean getDnsManagement() {
		return dnsManagement;
	}

	public void setDnsManagement(Boolean dnsManagement) {
		this.dnsManagement = dnsManagement;
	}

	@Attribute(name="status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Attribute(name="userManagement")
	public Boolean getUserManagement() {
		return userManagement;
	}

	public void setUserManagement(Boolean userManagement) {
		this.userManagement = userManagement;
	}
	
	@Attribute(name="domainManagement")	
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
	
	@Attribute(name="jpegLogo")
	public byte[] getJpegLogo() {
		return jpegLogo;
	}

	public void setJpegLogo(byte[] jpegLogo) {
		this.jpegLogo = jpegLogo;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof Domain) {
			Domain o = (Domain) obj;
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