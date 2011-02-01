package com.code.aon.manager;

import static com.code.aon.ldap.IAonObjectClasses.DOMAIN;
import static com.code.aon.ldap.IAonObjectClasses.TOP;

import javax.naming.Name;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.code.aon.dao.ldap.ILdapTransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.BaseDN;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;

@EntryObject(baseDN="ou=domains",mainObjectClass=DOMAIN, objectClasses={TOP})
public class Domain implements ILdapTransferObject {

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
	
	private String subDomainSuffix;
	
	public Domain() {
		this.status = 0;
	}

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
	
	@Attribute(name=ORGANIZATION_NAME_ATTRIBUTE)
	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	@Attribute(name=HOST_ATTRIBUTE,length=256)
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Attribute(name=MAIL_ATTRIBUTE,length=256)
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Attribute(name=MOBILE_ATTRIBUTE)
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Attribute(name=DNS_MANAGEMENT_ATTRIBUTE)
	public Boolean getDnsManagement() {
		return dnsManagement;
	}

	public void setDnsManagement(Boolean dnsManagement) {
		this.dnsManagement = dnsManagement;
	}

	@Attribute(name=STATUS_ATTRIBUTE)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Attribute(name=USER_MANAGEMENT_ATTRIBUTE)
	public Boolean getUserManagement() {
		return userManagement;
	}

	public void setUserManagement(Boolean userManagement) {
		this.userManagement = userManagement;
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
	@Attribute(name=PARENT_DOMAIN_ATTRIBUTE)
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

	@Attribute(name="subDomainSuffix",length=256)
	public String getSubDomainSuffix() {
		return subDomainSuffix;
	}

	public void setSubDomainSuffix(String subDomainSuffix) {
		this.subDomainSuffix = subDomainSuffix;
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
				.append(this.dnsManagement, o.dnsManagement)				
				.append(this.domainManagement, o.domainManagement)				
				.append(this.host, o.host)				
				.append(this.jpegLogo, o.jpegLogo)				
				.append(this.mail, o.mail)				
				.append(this.mobile, o.mobile)				
				.append(this.organizationName, o.organizationName)				
				.append(this.parentDomain, o.parentDomain)				
				.append(this.status, o.status)
				.append(this.subDomainSuffix, o.subDomainSuffix)
				.append(this.userManagement, o.userManagement)
				.isEquals();
		}
		return ObjectUtils.equals(getCommonName(), o.getCommonName());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(commonName)
			.append(dnsManagement)
			.append(domainManagement)
			.append(host)
			.append(jpegLogo)
			.append(mail)
			.append(mobile)
			.append(organizationName)
			.append(parentDomain)
			.append(status)
			.append(subDomainSuffix)
			.append(userManagement)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("commonName", commonName ).
			append("dnsManagement", dnsManagement ).
			append("domainManagement", domainManagement ).
			append("host", host ).
			append("mail", mail).
			append("mobile", mobile).
			append("organizationName", organizationName).
			append("parentDomain", (parentDomain != null) ? parentDomain.getCommonName() : "null" ).
			append("status", status).
			append("subDomainSuffix", subDomainSuffix).
			append("userManagement", userManagement).
			toString();
	}
	
}