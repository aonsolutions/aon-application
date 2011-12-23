package com.code.aon.registry;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Index;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.MediaType;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.RegistryDB;

@Entity
@Table(name="registry")
@Inheritance(strategy=InheritanceType.JOINED )
@org.hibernate.annotations.Table( appliesTo = "registry", indexes = { @Index(name="IDX_REGISTRY", columnNames={"name"})})
public class Registry extends RegistryDB implements IRegistry{
	
	private static final long serialVersionUID = 1L;
	
	private Set<RegistryAddress> addresses = new HashSet<RegistryAddress>();
	private Set<RegistryMedia> medias = new HashSet<RegistryMedia>();
	private Set<RegistryPayMethod> payMethods = new HashSet<RegistryPayMethod>();
	private Set<RegistrySegment> segments = new HashSet<RegistrySegment>();
	private RegistryDocument registryDocument;
	
	public Registry() {
		super();
		setDocumentType(DocumentType.NIF);
		setDocumentCountry(Country.ES);
		setNationality( Country.ES );
		setSecurityLevel( SecurityLevel.OFFICIAL);
	}
	
	@Override
	@Transient 
	public Registry getRegistry() {
		return this;
	}
	@Override
	public void setRegistry(Registry registry) {
		throw new IllegalStateException("Ups en Registry no se puede hacer eso!");
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
	public RegistryAddress getDefaultAddress() throws ManagerBeanException {
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), getId());
		criteria.addEqualExpression(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.MAIN);
		Iterator<ITransferObject> iter = rAddressBean.getList(criteria).iterator();
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
	public RegistryMedia getWeb() throws ManagerBeanException{
		return getRegistryMedia(MediaType.WEB);
	}

	@Transient 
	private RegistryMedia getRegistryMedia(MediaType type) throws ManagerBeanException{
		IManagerBean rMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID), getId());
		criteria.addEqualExpression(rMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE), type);
		Iterator<ITransferObject> iter = rMediaBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryMedia)iter.next();
		}
		return null;
	}

	@Transient
	public RegistryPayMethod getPayMethod() throws ManagerBeanException {
		IManagerBean rPayMethodBean = BeanManager.getManagerBean(RegistryPayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rPayMethodBean.getFieldName(IEntityAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), getId());
		Iterator<ITransferObject> iter = rPayMethodBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryPayMethod)iter.next(); 
		}
		return null;
	}
	
	@Transient 
	public String getPhones() throws ManagerBeanException{
		IManagerBean rMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID), getId());
		criteria.addEqualExpression(rMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE), MediaType.FIXED_PHONE);
		Iterator<ITransferObject> iter = rMediaBean.getList(criteria).iterator();
		String phones = "";
		while(iter.hasNext()){
			RegistryMedia media = (RegistryMedia)iter.next();
			phones += media.getValue()+", ";
		}
		return (phones=="")?"":phones.substring(0, phones.length()-2);
	}
	
}
