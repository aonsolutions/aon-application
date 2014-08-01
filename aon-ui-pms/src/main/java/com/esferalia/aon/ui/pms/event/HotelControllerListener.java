package com.esferalia.aon.ui.pms.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.customer.Customer;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;

public class HotelControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Hotel hotel = (Hotel)event.getController().getTo();
		hotel.setActive(true);

		WorkPlace workPlace = new WorkPlace();
		try {
			CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			if (companyCollections.getCurrentUserEnterprisesCount() == 1) {
				workPlace.setEnterprise((Enterprise)companyCollections.getCurrentUserEnterprises().get(0).getValue());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		workPlace.setAddress(new RegistryAddress());
		workPlace.getAddress().setId(0);
		workPlace.getAddress().setStreetType(StreetType.CL);
		workPlace.setCustomer(new Customer());
		workPlace.getCustomer().setRegistry(new Registry());
		hotel.setWorkPlace(workPlace);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Hotel hotel = (Hotel)event.getController().getTo();
		try {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			RegistryAddress rAddress = hotel.getWorkPlace().getAddress();
			rAddress.setId(null);
			rAddress.setRegistry(hotel.getWorkPlace().getEnterprise().getRegistry());
			rAddress = (RegistryAddress)rAddressBean.insert(hotel.getWorkPlace().getAddress());

			IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
			WorkPlace workPlace = hotel.getWorkPlace();
			workPlace.setAddress(rAddress);
			workPlace.setScope(hotel.getScope());
			workPlace.setActive(hotel.isActive());
			workPlace = (WorkPlace)workPlaceBean.insert(hotel.getWorkPlace());

			hotel.setWorkPlace(workPlace);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Hotel hotel = (Hotel)event.getController().getTo();
		try {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			RegistryAddress rAddress = hotel.getWorkPlace().getAddress();
			rAddress = (RegistryAddress)rAddressBean.update(hotel.getWorkPlace().getAddress());

			IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
			WorkPlace workPlace = hotel.getWorkPlace();
			workPlace.setAddress(rAddress);
			workPlace.setScope(hotel.getScope());
			workPlace.setActive(hotel.isActive());
			workPlace = (WorkPlace)workPlaceBean.update(hotel.getWorkPlace());

			hotel.setWorkPlace(workPlace);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)	throws ControllerListenerException {
		Hotel hotel = (Hotel)event.getController().getTo();
		try {
			IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
			workPlaceBean.remove(hotel.getWorkPlace());

			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			rAddressBean.remove(hotel.getWorkPlace().getAddress());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}