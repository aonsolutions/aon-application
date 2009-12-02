package com.code.aon.ui.commercial.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class TargetLookupListener extends ControllerAdapter implements ICommercialConstants {
	
	private RegistryAddress mainAddress;
	
	private RegistryMedia phone;
	
	private RegistryMedia fax;
	
	private RegistryMedia email;
	
	private RegistryMedia web;	
	
	public RegistryAddress getMainAddress() {
		return mainAddress;
	}

	public void setMainAddress(RegistryAddress mainAddress) {
		this.mainAddress = mainAddress;
	}

	public RegistryMedia getPhone() {
		return phone;
	}

	public void setPhone(RegistryMedia phone) {
		this.phone = phone;
	}

	public RegistryMedia getFax() {
		return fax;
	}

	public void setFax(RegistryMedia fax) {
		this.fax = fax;
	}

	public RegistryMedia getEmail() {
		return email;
	}

	public void setEmail(RegistryMedia email) {
		this.email = email;
	}

	public RegistryMedia getWeb() {
		return web;
	}

	public void setWeb(RegistryMedia web) {
		this.web = web;
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		this.mainAddress = new RegistryAddress();
		this.mainAddress.setAddressType( AddressType.MAIN );
		this.mainAddress.setGeozone( new GeoZone() );
		this.phone = new RegistryMedia();
		this.phone.setMediaType(MediaType.FIXED_PHONE);
		this.fax = new RegistryMedia();
		this.fax.setMediaType(MediaType.FAX);
		this.email = new RegistryMedia();
		this.email.setMediaType(MediaType.EMAIL);
		this.web = new RegistryMedia();
		this.web.setMediaType(MediaType.WEB);
	}

	private boolean isEmpty( RegistryAddress address ) {
		return StringUtils.isEmpty(address.getAddress()) && StringUtils.isEmpty(address.getAddress2()) &&
			StringUtils.isEmpty(address.getAddress3());
	}
	
	private boolean isEmpty( RegistryMedia media ) {
		return StringUtils.isEmpty(media.getValue());
	}
	
	private void updateRegistryMedia( Registry registry, RegistryMedia media ) throws ManagerBeanException {
		IManagerBean registryMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
		if (! isEmpty(media) ) {
			media.setRegistry( registry );
			registryMediaBean.insertOrUpdate( media );
		} else if ( media.getId() != null ) {
			registryMediaBean.remove(media);
		}		
	}
	
	private void updateRegistryLines( Registry registry ) throws ManagerBeanException {
		IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		if (! isEmpty(mainAddress) ) {
			mainAddress.setRegistry(registry);
			registryAddressBean.insertOrUpdate(mainAddress);
		} else if ( mainAddress.getId() != null ) {
			registryAddressBean.remove(mainAddress);
		}
		updateRegistryMedia(registry, phone);
		updateRegistryMedia(registry, fax);
		updateRegistryMedia(registry, email);
		updateRegistryMedia(registry, web);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		IController controller = event.getController();
		Registry registry = ((Target) controller.getTo()).getRegistry();
		try {
			updateRegistryLines( registry );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		IController controller = event.getController();
		Registry registry = ((Target) controller.getTo()).getRegistry();
		try {
			updateRegistryLines( registry );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

}