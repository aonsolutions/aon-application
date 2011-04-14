package com.code.aon.manager;

import static com.code.aon.ldap.IAonObjectClasses.DOMAIN;
import static com.code.aon.ldap.IAonObjectClasses.DOMAIN_APPLICATION;
import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;
import static com.code.aon.ldap.IAonObjectClasses.TOP;
import static com.code.aon.ldap.IAonObjectClasses.USER;

import java.util.LinkedList;
import java.util.List;

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
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.enumeration.DomainCapability;
import com.code.aon.manager.enumeration.DomainType;

@EntryObject(baseDN="ou=domains",mainObjectClass=DOMAIN, objectClasses={TOP})
public class Domain implements ILdapTransferObject {

	private static final long serialVersionUID = -4808900608917312113L;

	private Name id;
	
	private String commonName;
	
	private String organizationName;
	
	private Integer status;
	
	private Domain parentDomain;
	
	private DomainType type;
	
	private boolean userManagement;
	
	private boolean domainManagement;
	
	private boolean documentManagement;
	
	private byte[] jpegLogo;
	
	private String subDomainSuffix;
	
	private Integer maxDocumentSize;
	
	private Integer maxTotalDocumentSize;
	
	private Integer dataBaseId;
	
	private DomainUser administrator;
	
	public Domain() {
		this.status = 0;
		this.type = DomainType.ENTERPRISE;
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

	@Attribute(name=DOMAIN_TYPE_ATTRIBUTE)
	public DomainType getType() {
		return type;
	}

	public void setType(DomainType type) {
		this.type = type;
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
	
	@Attribute(name=DOCUMENT_MANAGEMENT_ATTRIBUTE)
	public boolean isDocumentManagement() {
		return documentManagement;
	}

	public void setDocumentManagement(boolean documentManagement) {
		this.documentManagement = documentManagement;
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
	
	@Attribute(name=MAX_DOCUMENT_SIZE_ATTRIBUTE)
	public Integer getMaxDocumentSize() {
		return maxDocumentSize;
	}

	public void setMaxDocumentSize(Integer maxDocumentSize) {
		this.maxDocumentSize = maxDocumentSize;
	}

	@Attribute(name=MAX_TOTAL_DOCUMENT_SIZE_ATTRIBUTE)
	public Integer getMaxTotalDocumentSize() {
		return maxTotalDocumentSize;
	}

	public void setMaxTotalDocumentSize(Integer maxTotalDocumentSize) {
		this.maxTotalDocumentSize = maxTotalDocumentSize;
	}

	@Attribute(name=DATA_BASE_ID_ATTRIBUTE)
	public Integer getDataBaseId() {
		return dataBaseId;
	}

	public void setDataBaseId(Integer dataBaseId) {
		this.dataBaseId = dataBaseId;
	}

	@Cascade(CascadeType.ALL)
	@BaseDN("ou=users,{this}")
	@Attribute(name=ADMINISTRATOR_ATTRIBUTE)
	public DomainUser getAdministrator() {
		return administrator;
	}

	public void setAdministrator(DomainUser administrator) {
		this.administrator = administrator;
	}
	
	public List<DomainCapability> getCapabilities() {
		List<DomainCapability> list = new LinkedList<DomainCapability>();
		if ( isDocumentManagement() ) {
			list.add(DomainCapability.DOCUMENTAL);
		}
		if ( getUserManagement() ) {
			list.add(DomainCapability.MULTI_USER);
		}
		if ( getDomainManagement() ) {
			list.add(DomainCapability.MULTI_DOMAIN);
		}
		return list;
	}

	public void setCapabilities( List<DomainCapability> list ) {
		setDocumentManagement( list.contains(DomainCapability.DOCUMENTAL) );
		setUserManagement( list.contains(DomainCapability.MULTI_USER) );
		setDomainManagement( list.contains(DomainCapability.MULTI_DOMAIN) );
	}
	
	public static void delete( BasicLdap ldap, Name dn ) {
		String domain = NameResolver.getFirstValue(dn);
		Name usersDN = NameResolver.getUsersDN(domain);
		if ( ldap.exists(usersDN, ORGANIZATIONAL_UNIT) ) {
			String oc = NameResolver.getObjectClass(USER);
			for( Entry entry : ldap.getList(usersDN, oc, OBJECT_CLASS_ATTRIBUTE) ) {
				DomainUser.delete(ldap, entry.getDN());
			}
		}
		Name applicationsDN = NameResolver.getDomainApplicationsDN( domain );
		if ( ldap.exists(applicationsDN, ORGANIZATIONAL_UNIT) ) {
			String oc = NameResolver.getObjectClass(DOMAIN_APPLICATION);
			for( Entry entry : ldap.getList(applicationsDN, oc, OBJECT_CLASS_ATTRIBUTE) ) {
				DomainApplication.delete(ldap, entry.getDN());
			}
		}
		Name bdsDN = NameResolver.getDomainBDsDN(domain);
		if ( ldap.exists(bdsDN, ORGANIZATIONAL_UNIT) ) {
			ldap.deleteDepth(bdsDN, true);
		}
		ldap.deleteDepth(dn, true);
	}		
	
	public void construct( BasicLdap ldap ) {
		Name applicationsDN = NameResolver.getDomainApplicationsDN(getCommonName());
		if (! ldap.exists(applicationsDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(applicationsDN);
		}
		Name bdsDN = NameResolver.getDomainBDsDN(getCommonName());
		if (! ldap.exists(bdsDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(bdsDN);
		}
		Name usersDN = NameResolver.getUsersDN(getCommonName());
		if (! ldap.exists(usersDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(usersDN);
		}
		Name aliasesDN = NameResolver.getAliasesDN(getCommonName());
		if (! ldap.exists(aliasesDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(aliasesDN);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Domain o = (Domain) obj;
		if (o.getCommonName() == null && getCommonName() == null) {
			return new EqualsBuilder()
				.append(this.administrator, o.administrator)		
				.append(this.dataBaseId, o.dataBaseId)
				.append(this.documentManagement, o.documentManagement)
				.append(this.domainManagement, o.domainManagement)							
				.append(this.jpegLogo, o.jpegLogo)				
				.append(this.maxDocumentSize, o.maxDocumentSize)
				.append(this.maxTotalDocumentSize, o.maxTotalDocumentSize)
				.append(this.organizationName, o.organizationName)				
				.append(this.parentDomain, o.parentDomain)				
				.append(this.status, o.status)
				.append(this.subDomainSuffix, o.subDomainSuffix)
				.append(this.type, o.type)
				.append(this.userManagement, o.userManagement)
				.isEquals();
		}
		return ObjectUtils.equals(getCommonName(), o.getCommonName());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(administrator)
			.append(commonName)
			.append(dataBaseId)
			.append(documentManagement)
			.append(domainManagement)
			.append(jpegLogo)
			.append(maxDocumentSize)
			.append(maxTotalDocumentSize)
			.append(organizationName)
			.append(parentDomain)
			.append(status)
			.append(subDomainSuffix)
			.append(type)
			.append(userManagement)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("administrator", (administrator != null) ? administrator.getUid() : "null" ).
			append("commonName", commonName ).
			append("dataBaseId", dataBaseId ).
			append("documentManagement", documentManagement ).
			append("domainManagement", domainManagement ).
			append("maxDocumentSize", maxDocumentSize ).
			append("maxTotalDocumentSize", maxTotalDocumentSize ).
			append("organizationName", organizationName).
			append("parentDomain", (parentDomain != null) ? parentDomain.getCommonName() : "null" ).
			append("status", status).
			append("subDomainSuffix", subDomainSuffix).
			append("type", type ).
			append("userManagement", userManagement).
			toString();
	}
	
}