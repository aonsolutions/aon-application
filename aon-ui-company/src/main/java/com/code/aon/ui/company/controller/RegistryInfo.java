package com.code.aon.ui.company.controller;

import java.io.Serializable;
import java.util.Iterator;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.AddressType;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryInfo implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private RegistryAddress address;
	
	private RegistryMedia phone;
	
	private RegistryMedia fax;
	
	private RegistryMedia email;	
	
	private RegistryMedia web;
	
	public void init( Registry registry ) throws ManagerBeanException {
		reset();
		this.address = getMainAddress(registry);
		if ( this.address != null ) {
			initMedias(this.address);	
		} else {
    		this.address = new RegistryAddress();
    		this.address.setAddress("");
    		this.address.setAddressType(AddressType.MAIN);
    		this.address.setRegistry(registry);
		}
	}
	
	public void reset() {
		this.address = null;
		this.phone = null;
		this.fax = null;
		this.email = null;
		this.web = null;
	}

	public RegistryAddress getAddress() {
		return address;
	}

	public RegistryMedia getPhone() {
		return phone;
	}

	public RegistryMedia getFax() {
		return fax;
	}

	public RegistryMedia getEmail() {
		return email;
	}

	public RegistryMedia getWeb() {
		return web;
	}
	
	public boolean hasMedias() {
		return (phone != null) || (fax != null) || (email != null) || (web != null);
	}
	
	public static RegistryAddress getMainAddress( Registry registry ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), registry.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.MAIN);
		Iterator<ITransferObject> iter = bean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAddress) iter.next();
		}
		return null;
	}
	
	private void initMedias( RegistryAddress address ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_MEDIA_ADDRESS_ID), address.getId());
		for(ITransferObject to: bean.getList(criteria)){
			RegistryMedia rmedia = (RegistryMedia)to;
			switch (rmedia.getMediaType()) {
				case FIXED_PHONE:
					phone = rmedia;
					break;
				case FAX:
					fax = rmedia;
					break;
				case EMAIL:
					email = rmedia;
					break;
				case WEB:
					web = rmedia;
					break;
			}				
		}
	}
	
}
