package com.code.aon.ui.registry.controller.event;

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.company.util.CompanyUtil;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.IRegistryConstants;

public class RegistryFormListener extends ControllerAdapter {
	
	private RegistryAddress mainAddress;
	private RegistryMedia phone;
	private RegistryMedia cellular;
	private RegistryMedia fax;
	private RegistryMedia email;
	private RegistryMedia web;
	private CompanyUtil companyUtil;
	
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

	public RegistryMedia getCellular() {
		return cellular;
	}

	public void setCellular(RegistryMedia cellular) {
		this.cellular = cellular;
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

	public CompanyUtil getCompanyUtil() {
		if (companyUtil == null) {
			companyUtil = new CompanyUtil();
		}
		return companyUtil;
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		initDocument(getRegistry(event));

		setMainAddress(new RegistryAddress());
		getMainAddress().setAddressType(AddressType.MAIN);
		getMainAddress().setStreetType(StreetType.CL);
		try {
			getMainAddress().setGeozone(getCompanyUtil().getCompanyGeoZone());
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		setPhone(new RegistryMedia());
		initRegistryMedia(phone, MediaType.FIXED_PHONE);
		setCellular(new RegistryMedia());
		initRegistryMedia(cellular, MediaType.CELLULAR);
		setFax(new RegistryMedia());
		initRegistryMedia(fax, MediaType.FAX);
		setEmail(new RegistryMedia());
		initRegistryMedia(email, MediaType.EMAIL);
		setWeb(new RegistryMedia());
		initRegistryMedia(web, MediaType.WEB);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			updateRegistryLines(getRegistry(event));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			updateRegistryLines(getRegistry(event));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	public void initDocument(Registry registry) {
		registry.setType(RegistryType.LEGAL);
		registry.setNationality(Country.ES);
		registry.setDocumentCountry(Country.ES);
		registry.setDocumentType(DocumentType.CIF);		
	}	
	
	private Registry getRegistry(ControllerEvent event) {
		IController controller = event.getController();
		return ((IRegistry) controller.getTo()).getRegistry();
	}
	
	private void initRegistryMedia(RegistryMedia media, MediaType type) {
		media.setMediaType(type);
		media.setAdministrative(true);
		media.setCommercial(true);
		media.setTechnical(true);
	}
	
	protected void updateRegistryLines(Registry registry) throws ManagerBeanException {
		RegistryAddress address = updateRegistryAddress(registry, getMainAddress());
		updateRegistryMedia(registry, phone, address);
		updateRegistryMedia(registry, cellular, address);
		updateRegistryMedia(registry, fax, address);
		updateRegistryMedia(registry, email, address);
		updateRegistryMedia(registry, web, address);
	}
	
	private RegistryAddress updateRegistryAddress(Registry registry, RegistryAddress address) throws ManagerBeanException {
		IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		if (address != null) {
			if (!isEmpty(address)) {
				address.setRegistry(registry);
				return (RegistryAddress) registryAddressBean.insertOrUpdate(address);
			} else if (address.getId() != null) {
				registryAddressBean.remove(address);
			}
		}
		return null;
	}	
	
	private boolean isEmpty(RegistryAddress address) {
		return StringUtils.isEmpty(address.getAddress()) && StringUtils.isEmpty(address.getCity()) &&
			StringUtils.isEmpty(address.getZip());
	}
	
	private void updateRegistryMedia(Registry registry, RegistryMedia media, RegistryAddress address) throws ManagerBeanException {
		IManagerBean registryMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
		if (media != null) {
			if (!isEmpty(media)) {
				media.setRegistry(registry);
				media.setAddress(address);
				registryMediaBean.insertOrUpdate(media);
			} else if (media.getId() != null) {
				registryMediaBean.remove(media);
			}
		}
	}
	
	private boolean isEmpty(RegistryMedia media) {
		return StringUtils.isEmpty(media.getValue());
	}
	
	public List<SelectItem> getMunicipalities(){
		ResourceBundle bundle = ResourceBundle.getBundle(IRegistryConstants.MUNICIPALITIES_BUNDLE_NAME);
		List<SelectItem> municipalities = new LinkedList<SelectItem>();
		if(this.getMainAddress()!=null){
			RegistryAddress address = (RegistryAddress) this.getMainAddress();
			if(address!=null && address.getGeozone()!=null && StringUtils.isNotBlank(address.getGeozone().getCode())) {
				TreeSet<String> tree = new TreeSet<String>(bundle.keySet());
				for(String key: tree){
					if(key.startsWith(address.getGeozone().getCode())){
						String name = bundle.getString(key);
						SelectItem item = new SelectItem(key, name);
						municipalities.add(item);
					}
				}
			}
		}
		return municipalities;
	}
	
}