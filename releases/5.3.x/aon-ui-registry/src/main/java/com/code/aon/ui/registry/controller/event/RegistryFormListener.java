package com.code.aon.ui.registry.controller.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.util.CompanyUtil;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class RegistryFormListener extends ControllerAdapter {
	
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
	
	private boolean isEmpty( RegistryAddress address ) {
		return StringUtils.isEmpty(address.getAddress()) && StringUtils.isEmpty(address.getCity()) &&
			StringUtils.isEmpty(address.getZip());
	}
	
	private boolean isEmpty( RegistryMedia media ) {
		return StringUtils.isEmpty(media.getValue());
	}
	
	private void updateRegistryMedia(Registry registry, RegistryMedia media) throws ManagerBeanException {
		IManagerBean registryMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
		if (media != null) {
			if (!isEmpty(media)) {
				media.setRegistry(registry);
				registryMediaBean.insertOrUpdate(media);
			} else if (media.getId() != null) {
				registryMediaBean.remove(media);
			}
		}
	}
	
	private void updateRegistryAddress( Registry registry, RegistryAddress address ) throws ManagerBeanException {
		IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		if (address != null) {
			if (!isEmpty(address) ) {
				address.setRegistry(registry);
				registryAddressBean.insertOrUpdate(address);
			} else if ( address.getId() != null ) {
				registryAddressBean.remove(address);
			}
		}
	}	
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		setMainAddress( new RegistryAddress() );
		getMainAddress().setAddressType( AddressType.MAIN );
		try {
			getMainAddress().setGeozone( CompanyUtil.getCompanyGeoZone() );
		} catch( ManagerBeanException e ) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
		setPhone( new RegistryMedia() );
		initRegistryMedia(phone, MediaType.FIXED_PHONE);
		setFax( new RegistryMedia() );
		initRegistryMedia(fax, MediaType.FAX);
		setEmail( new RegistryMedia() );
		initRegistryMedia(email, MediaType.EMAIL);
		setWeb( new RegistryMedia() );
		initRegistryMedia(web, MediaType.WEB);
	}
	
	protected void updateRegistryLines( Registry registry ) throws ManagerBeanException {
		updateRegistryAddress(registry, getMainAddress());
		updateRegistryMedia(registry, phone);
		updateRegistryMedia(registry, fax);
		updateRegistryMedia(registry, email);
		updateRegistryMedia(registry, web);
	}
	
	private Registry getRegistry( ControllerEvent event ) {
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