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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Index;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
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
@org.hibernate.annotations.Table( appliesTo = "registry", indexes = { @Index(name="IDX_REIGSTRY", columnNames={"name","surname"})})
public class Registry implements ITransferObject {
	
	private static final long serialVersionUID = 8635760095705923309L;

	/** The id. */
	private Integer id;

	/** The document. */
	private String document;

	/** The name. */
	private String name;

	/** The surname. */
	private String surname;

	/** The alias. */
	private String alias;
	
	/** The Registry type. */
	private RegistryType type;

	/** The addresses. */
	private Set<RegistryAddress> addresses = new HashSet<RegistryAddress>();
	
	/** The medias. */
	private Set<RegistryMedia> medias = new HashSet<RegistryMedia>();

    /** The segments. */
	private Set<RegistrySegment> segments = new HashSet<RegistrySegment>();

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the alias.
	 * 
	 * @return the alias
	 */
	@Column(length=32)
	public String getAlias() {
		return alias;
	}

	/**
	 * Sets the alias.
	 * 
	 * @param alias the alias
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Gets the document.
	 * 
	 * @return the document
	 */
	@Column(length=16)
	@Index(name="IDX_REGISTRY_DOCUMENT")
	public String getDocument() {
		return document;
	}

	/**
	 * Sets the document.
	 * 
	 * @param document the document
	 */
	public void setDocument(String document) {
		this.document = document;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@Column(length=64)
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the surname.
	 * 
	 * @return the surname
	 */
	@Column(length=64)
	public String getSurname() {
		return surname;
	}

	/**
	 * Sets the surname.
	 * 
	 * @param surname the surname
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public RegistryType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the type
	 */
	public void setType(RegistryType type) {
		this.type = type;
	}

	/**
	 * Gets the addresses.
	 * 
	 * @return the addresses
	 */
	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAddress> getAddresses() {
		return this.addresses;
	}
	
	/**
	 * Sets the addresses.
	 * 
	 * @param addresses the addresses
	 */
	public void setAddresses( Set<RegistryAddress> addresses ) {
		this.addresses = addresses;
	}

	/**
	 * Adds an address to the set of addresses.
	 * 
	 * @param address the address
	 */
	public void addAddress(RegistryAddress address) {
		address.setRegistry( this );
		this.addresses.add( address );
	}

	/**
	 * Gets the medias.
	 * 
	 * @return the medias
	 */
	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryMedia> getMedias() {
		return this.medias;
	}

	/**
	 * Sets the medias.
	 * 
	 * @param medias the medias
	 */
	public void setMedias( Set<RegistryMedia> medias ) {
		this.medias = medias;
	}
	
	/**
	 * Adds a media to the set of medias.
	 * 
	 * @param media the media
	 */
	public void addMedia(RegistryMedia media) {
		media.setRegistry( this );
		this.medias.add( media );
	}
	
	/**
	 * Gets the segments.
	 * 
	 * @return the segments
	 */
	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistrySegment> getSegments() {
		return segments;
	}

	/**
	 * Sets the segments.
	 * 
	 * @param segments the new segments
	 */
	public void setSegments(Set<RegistrySegment> segments) {
		this.segments = segments;
	}

    @Transient
    public String getFullName() {
    	return ((getName() == null) ? "" : getName()) + " " + ((getSurname() == null) ? "" : getSurname());
    }

    /**
	 * Gets the default address.
	 * 
	 * @return the default address
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
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
	
	/**
	 * Gets the phone.
	 * 
	 * @return the phone
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@Transient
	public RegistryMedia getPhone() throws ManagerBeanException{
		return getRegistryMedia(MediaType.FIXED_PHONE);
	}
	
	/**
	 * Gets the cellular.
	 * 
	 * @return the cellular
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@Transient
	public RegistryMedia getCellular() throws ManagerBeanException{
		return getRegistryMedia(MediaType.CELLULAR);
	}
	
	/**
	 * Gets the fax.
	 * 
	 * @return the fax
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@Transient
	public RegistryMedia getFax() throws ManagerBeanException{
		return getRegistryMedia(MediaType.FAX);
	}
	
	/**
	 * Gets the email.
	 * 
	 * @return the email
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@Transient
	public RegistryMedia getEmail() throws ManagerBeanException{
		return getRegistryMedia(MediaType.EMAIL);
	}
	
	/**
	 * Gets the registry media of the type passed as parameter.
	 * 
	 * @param type the type
	 * 
	 * @return the registry media
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
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

    /**
	 * Gets the pay method.
	 * 
	 * @return the pay method
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
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
				.append(this.name, o.name)
				.append(this.surname, o.surname)				
				.append(this.type, o.type)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(alias)
			.append(document)
			.append(id)	
			.append(name)			
			.append(surname)
			.append(type)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}