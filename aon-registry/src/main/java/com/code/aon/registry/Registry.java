package com.code.aon.registry;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryType;

/**
 * Transfer Object that represents the Registry.
 * 
 * @author Consulting & Development. Eugenio Castellano - 27-ene-2005
 * @since 1.0
 */
@Entity
@Table(name="registry")
@Inheritance(strategy=InheritanceType.JOINED )
@org.hibernate.annotations.Table( appliesTo = "registry", indexes = { @Index(name="IDX_REGISTRY", columnNames={"name"})})
public class Registry implements ITransferObject, IConfidentialable {
	
	private static final long serialVersionUID = 8635760095705923309L;

	private Integer id;
	private String document;
	private DocumentType documentType;
	private Country documentCountry;
	private String name;
	private String alias;
	private RegistryType type;
	private Country nationality;
	private SecurityLevel securityLevel;
	private Set<RegistryAddress> addresses = new HashSet<RegistryAddress>();
	private Set<RegistryMedia> medias = new HashSet<RegistryMedia>();
	private Set<RegistryPayMethod> payMethods = new HashSet<RegistryPayMethod>();
	private Set<RegistrySegment> segments = new HashSet<RegistrySegment>();
	
	private RegistryDocument registryDocument;

	public Registry() {
		this.documentType = DocumentType.NIF;
		this.documentCountry  = Country.ES;
		this.nationality = Country.ES;
		this.securityLevel = SecurityLevel.OFFICIAL;
	}

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(length=16)
	@Index(name="IDX_REGISTRY_DOCUMENT")
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	
	@Column(name="document_type")
	public DocumentType getDocumentType() {
		return documentType;
	}
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}
	
	@Column(name="document_country")
	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.common.enumeration.Country") })
	public Country getDocumentCountry() {
		return documentCountry;
	}
	public void setDocumentCountry(Country documentCountry) {
		this.documentCountry = documentCountry;
	}

	@Column(length=64)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(length=32)
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public RegistryType getType() {
		return type;
	}
	public void setType(RegistryType type) {
		this.type = type;
	}

	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.common.enumeration.Country") })
	public Country getNationality() {
		return nationality;
	}
	public void setNationality(Country nationality) {
		this.nationality = nationality;
	}

    @Column(name = "security_level")
    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }
    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }

	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAddress> getAddresses() {
		return this.addresses;
	}
	public void setAddresses( Set<RegistryAddress> addresses ) {
		this.addresses = addresses;
	}
	public void addAddress(RegistryAddress address) {
		address.setRegistry( this );
		this.addresses.add( address );
	}

	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryMedia> getMedias() {
		return this.medias;
	}
	public void setMedias( Set<RegistryMedia> medias ) {
		this.medias = medias;
	}
	public void addMedia(RegistryMedia media) {
		media.setRegistry( this );
		this.medias.add( media );
	}
	
	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryPayMethod> getPayMethods() {
		return this.payMethods;
	}
	public void setPayMethods(Set<RegistryPayMethod> payMethods) {
		this.payMethods = payMethods;
	}

	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistrySegment> getSegments() {
		return this.segments;
	}
	public void setSegments(Set<RegistrySegment> segments) {
		this.segments = segments;
	}

	@Transient
	public RegistryDocument getRegistryDocument() {
		if (registryDocument == null) {
			registryDocument = new RegistryDocument();
		}
		registryDocument.setDocument(getDocument());
		registryDocument.setType(getDocumentType());
		registryDocument.setCountry(getDocumentCountry());
		return registryDocument;
	}
	@Transient
	public boolean isValidDocument() {
		return getRegistryDocument().isValid();
	}
	@Transient
	public boolean isDocumentValidable() {
		return getRegistryDocument().isValidable();
	}
	
    @Transient
    public String getFullName() {
    	return (StringUtils.isEmpty(getName())) ? "" : getName();
    }

	@Transient
	@SuppressWarnings("unchecked")
	public RegistryAddress getDefaultAddress() throws ManagerBeanException {
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), getId());
		criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.MAIN);
		Iterator iter = rAddressBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAddress)iter.next(); 
		}
		return null;
	}
	@Transient
	public RegistryMedia getPhone() throws ManagerBeanException{
		return getRegistryMedia(MediaType.FIXED_PHONE);
	}
	
	@Transient
	public RegistryMedia getCellular() throws ManagerBeanException{
		return getRegistryMedia(MediaType.CELLULAR);
	}

	@Transient
	public RegistryMedia getFax() throws ManagerBeanException{
		return getRegistryMedia(MediaType.FAX);
	}

	@Transient
	public RegistryMedia getEmail() throws ManagerBeanException{
		return getRegistryMedia(MediaType.EMAIL);
	}

	@Transient 
	@SuppressWarnings("unchecked")
	private RegistryMedia getRegistryMedia(MediaType type) throws ManagerBeanException{
		IManagerBean rMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rMediaBean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID), getId());
		criteria.addEqualExpression(rMediaBean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_MEDIA_TYPE), type);
		Iterator iter = rMediaBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryMedia)iter.next();
		}
		return null;
	}

	@Transient
	@SuppressWarnings("unchecked")
	public RegistryPayMethod getPayMethod() throws ManagerBeanException {
		IManagerBean rPayMethodBean = BeanManager.getManagerBean(RegistryPayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rPayMethodBean.getFieldName(IRegistryAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), getId());
		Iterator iter = rPayMethodBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryPayMethod)iter.next(); 
		}
		return null;
	}

	@Transient
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	@Transient
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Registry o = (Registry) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.alias, o.alias)
				.append(this.document, o.document)				
				.append(this.documentType, o.documentType)
				.append(this.documentCountry, o.documentCountry)
				.append(this.name, o.name)
				.append(this.type, o.type)
				.append(this.nationality, o.nationality)
				.append(this.securityLevel, o.securityLevel)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(alias)
			.append(document)
			.append(documentType)
			.append(documentCountry)
			.append(id)	
			.append(name)			
			.append(type)
			.append(nationality)
			.append(securityLevel)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
	@Transient 
	@SuppressWarnings({ "unchecked" })
	public String getPhones() throws ManagerBeanException{
		IManagerBean rMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rMediaBean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID), getId());
		criteria.addEqualExpression(rMediaBean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_MEDIA_TYPE), MediaType.FIXED_PHONE);
		Iterator iter = rMediaBean.getList(criteria).iterator();
		String phones = "";
		while(iter.hasNext()){
			RegistryMedia media = (RegistryMedia)iter.next();
			phones += media.getValue()+", ";
		}
		return (phones=="")?"":phones.substring(0, phones.length()-2);
	}
}
