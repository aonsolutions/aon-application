package com.code.aon.ui.registry.controller.event;

import java.util.Iterator;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.PersonController;

public class PersonFormListener extends RegistryFormListener {
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			PersonController c = (PersonController) event.getController();
			Person person = (Person) c.getTo();

			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), person.getId());
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.MAIN);
			Iterator<?> iter = rAddressBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				setMainAddress((RegistryAddress) iter.next());
			} else {
				setMainAddress(new RegistryAddress());
				getMainAddress().setRegistry(person.getRegistry());
				getMainAddress().setGeozone(new GeoZone());
				getMainAddress().setAddressType(AddressType.MAIN);
				getMainAddress().setStreetType(StreetType.CL);
			}

			Criteria criteriaMedia = new Criteria();
			IManagerBean beanMedia = BeanManager.getManagerBean(RegistryMedia.class);
			String registryIdFieldName = beanMedia.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID);
			criteriaMedia.addEqualExpression(registryIdFieldName, person.getId());
			List<?> mediaList = beanMedia.getList(criteriaMedia);
			
			RegistryMedia phone = new RegistryMedia();
			phone.setRegistry(person.getRegistry());
			phone.setMediaType(MediaType.FIXED_PHONE);
			RegistryMedia fax = new RegistryMedia();
			fax.setRegistry(person.getRegistry());
			fax.setMediaType(MediaType.FAX);
			RegistryMedia email = new RegistryMedia();
			email.setRegistry(person.getRegistry());
			email.setMediaType(MediaType.EMAIL);
			RegistryMedia web = new RegistryMedia();
			web.setRegistry(person.getRegistry());
			web.setMediaType(MediaType.WEB);

			Iterator<?> mediaIter = mediaList.iterator();
			while (mediaIter.hasNext()) {
				RegistryMedia rmedia = (RegistryMedia) mediaIter.next();
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
			setPhone(phone);
			setFax(fax);
			setEmail(email);
			setWeb(web);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}
