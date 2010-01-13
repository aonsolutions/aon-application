package com.code.aon.ui.registry.controller.event;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class RegistryLookupListener extends ControllerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegistryLookupListener.class);
	
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
	
	private void initRegistryMedia( RegistryMedia media, MediaType type ) {
		media.setMediaType( type );
		media.setAdministrative(true);
		media.setCommercial(true);
		media.setTechnical(true);
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		this.mainAddress = new RegistryAddress();
		this.mainAddress.setAddressType( AddressType.MAIN );
		this.mainAddress.setGeozone( new GeoZone() );
		this.phone = new RegistryMedia();
		initRegistryMedia(phone, MediaType.FIXED_PHONE);
		this.fax = new RegistryMedia();
		initRegistryMedia(fax, MediaType.FAX);
		this.email = new RegistryMedia();
		initRegistryMedia(email, MediaType.EMAIL);
		this.web = new RegistryMedia();
		initRegistryMedia(web, MediaType.WEB);
	}

	private boolean isEmpty( RegistryAddress address ) {
		return StringUtils.isEmpty(address.getAddress()) && StringUtils.isEmpty(address.getCity()) &&
			StringUtils.isEmpty(address.getZip());
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
	
	private void updateRegistryAddress( Registry registry, RegistryAddress address ) throws ManagerBeanException {
		IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		if (! isEmpty(address) ) {
			address.setRegistry(registry);
			registryAddressBean.insertOrUpdate(address);
		} else if ( address.getId() != null ) {
			registryAddressBean.remove(address);
		}
	}	

	public ITransferObject onAccept( LinesController controller, ITransferObject to ) {
		controller.initModel();
		controller.onReset(null);
		ITransferObject newTo = controller.getTo();
		try {
			BeanUtils.copyProperties(newTo, to);
			IManagerBean bean = controller.getManagerBean();
			bean.initializePOJO(newTo);
			bean.setId(newTo, bean.getId(to));
			controller.onAccept(null);
			return newTo;
		} catch (Throwable th) {
			LOGGER.error( "onAccept: " + to, th );
		}
		return null;
	}

	private void updateRegistryAddress( LinesController controller, RegistryAddress address ) {
		if (! isEmpty(address) ) {
			onAccept(controller, address);
		}
	}	

	private void updateRegistryMedia( LinesController controller, RegistryMedia media ) {
		if (! isEmpty(media) ) {
			onAccept(controller, media);
		}		
	}
		
	private void updateRegistryLines( Registry registry ) throws ManagerBeanException {
		updateRegistryAddress(registry, mainAddress);
		updateRegistryMedia(registry, phone);
		updateRegistryMedia(registry, fax);
		updateRegistryMedia(registry, email);
		updateRegistryMedia(registry, web);
	}

	public void updateRegistryAddress( LinesController registryAddress ) throws ManagerBeanException {
		updateRegistryAddress(registryAddress, mainAddress);
	}	
	
	public void updateRegistryMedia( LinesController registryMedia ) throws ManagerBeanException {
		updateRegistryMedia(registryMedia, phone);
		updateRegistryMedia(registryMedia, fax);
		updateRegistryMedia(registryMedia, email);
		updateRegistryMedia(registryMedia, web);
	}	
	
	protected Registry getRegistry( ControllerEvent event ) {
		IController controller = event.getController();
		return ((IRegistry) controller.getTo()).getRegistry();
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		try {
			updateRegistryLines( getRegistry(event) );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		try {
			updateRegistryLines( getRegistry(event) );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

}