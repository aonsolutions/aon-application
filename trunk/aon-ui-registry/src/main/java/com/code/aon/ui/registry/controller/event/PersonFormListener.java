package com.code.aon.ui.registry.controller.event;

import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class PersonFormListener extends RegistryPayMethodFormListener {
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			Person person = (Person)event.getController().getTo();

			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteriaAddress = new Criteria();
			criteriaAddress.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), person.getId());
			criteriaAddress.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.MAIN);
			Iterator<?> addressIter = rAddressBean.getList(criteriaAddress).iterator();
			if (addressIter.hasNext()) {
				setMainAddress((RegistryAddress)addressIter.next());
			} else {
				setMainAddress(new RegistryAddress());
				getMainAddress().setRegistry(person.getRegistry());
				getMainAddress().setGeozone(new GeoZone());
				getMainAddress().setAddressType(AddressType.MAIN);
				getMainAddress().setStreetType(StreetType.CL);
			}

			RegistryMedia phone = new RegistryMedia();
			phone.setRegistry(person.getRegistry());
			phone.setMediaType(MediaType.FIXED_PHONE);
			RegistryMedia cellular = new RegistryMedia();
			cellular.setRegistry(person.getRegistry());
			cellular.setMediaType(MediaType.CELLULAR);
			RegistryMedia fax = new RegistryMedia();
			fax.setRegistry(person.getRegistry());
			fax.setMediaType(MediaType.FAX);
			RegistryMedia email = new RegistryMedia();
			email.setRegistry(person.getRegistry());
			email.setMediaType(MediaType.EMAIL);
			RegistryMedia web = new RegistryMedia();
			web.setRegistry(person.getRegistry());
			web.setMediaType(MediaType.WEB);

			IManagerBean rMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
			Criteria criteriaMedia = new Criteria();
			criteriaMedia.addEqualExpression(rMediaBean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID), person.getId());
			Iterator<?> mediaIter = rMediaBean.getList(criteriaMedia).iterator();
			while (mediaIter.hasNext()) {
				RegistryMedia rmedia = (RegistryMedia)mediaIter.next();
				switch (rmedia.getMediaType()) {
				case FIXED_PHONE:
					phone = rmedia;
					break;
				case CELLULAR:
					cellular = rmedia;
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
			setCellular(cellular);
			setFax(fax);
			setEmail(email);
			setWeb(web);

			resetPayMethod();

			IManagerBean rPayMethodBean = BeanManager.getManagerBean(RegistryPayMethod.class);
			Criteria criteriaPayMethod = new Criteria();
			criteriaPayMethod.addEqualExpression(rPayMethodBean.getFieldName(IRegistryAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), person.getId());
			Iterator<?> payMethodIter = rPayMethodBean.getList(criteriaPayMethod).iterator();
			if (payMethodIter.hasNext()) {
				RegistryPayMethod rPayMethod = (RegistryPayMethod)payMethodIter.next();
				setRegistryPayMethod(rPayMethod);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}
