package com.esferalia.aon.ui.pms.event;

import static com.code.aon.ui.common.ICommonMessages.REGISTRY_DOCUMENT_INCORRECT_ERROR;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.geozone.GeoZone;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.ui.pms.controller.ProjectReservationController;
import com.esferalia.aon.ui.pms.controller.ProjectReservationGuestController;

public class ProjectReservationGuestControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationGuestController controller = (ProjectReservationGuestController)event.getController();
		ProjectReservationGuest to = (ProjectReservationGuest)controller.getTo();
		to.setProjectReservation((ProjectReservation)controller.getMasterController().getTo());
		to.setDocumentCountry(Country.ES);
		to.setCountry(null);
		controller.setPhonePrefix("+");
		controller.setPhoneNumber(null);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationGuestController controller = (ProjectReservationGuestController)event.getController();
		ProjectReservationGuest to = (ProjectReservationGuest)controller.getTo();
		controller.setPhonePrefix(controller.obtainPhonePrefix(to.getPhone()));
		controller.setPhoneNumber(controller.obtainPhoneNumber(to.getPhone()));
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationGuestController controller = (ProjectReservationGuestController)event.getController();
		ProjectReservationGuest to = (ProjectReservationGuest)controller.getTo();
		to.setGuestIndex(0);
		to.setPhone(controller.obtainFullPhone());
		if (StringUtils.isNotBlank(to.getDocument())) {
			savePerson(event);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationGuestController controller = (ProjectReservationGuestController)event.getController();
		ProjectReservationGuest to = (ProjectReservationGuest)controller.getTo();
		if (to.getGuestIndex() == 1) {
			ProjectReservationController masterController = (ProjectReservationController)controller.getMasterController();
			try {
				masterController.refresh(null);
			} catch (ManagerBeanException ex) {
				throw new ControllerListenerException(ex.getMessage());
			}
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationGuestController controller = (ProjectReservationGuestController)event.getController();
		ProjectReservationGuest to = (ProjectReservationGuest)controller.getTo();
		to.setPhone(controller.obtainFullPhone());
		if (StringUtils.isNotBlank(to.getDocument())) {
			savePerson(event);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationGuestController controller = (ProjectReservationGuestController)event.getController();
		ProjectReservationGuest to = (ProjectReservationGuest)controller.getTo();
		if (to.getGuestIndex() == 1) {
			ProjectReservationController masterController = (ProjectReservationController)controller.getMasterController();
			try {
				masterController.refresh(null);
			} catch (ManagerBeanException ex) {
				throw new ControllerListenerException(ex.getMessage());
			}
		}
	}

	private void savePerson(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationGuestController controller = (ProjectReservationGuestController)event.getController();
		ProjectReservationGuest reservationGuest = (ProjectReservationGuest)controller.getTo();
		if (!reservationGuest.isDocumentValidable() || reservationGuest.isValidDocument()) {
			try {
				if (reservationGuest.getPerson() == null || reservationGuest.getPerson().getId() == null) {
					Person person = controller.obtainPerson(reservationGuest);
					if (person == null) {
						insertPerson(reservationGuest);
					} else {
						updatePerson(reservationGuest);
					}
				} else {
					updatePerson(reservationGuest);
				}
			} catch (ManagerBeanException ex) {
				throw new ControllerListenerException(ex.getMessage());
			}
		} else {
			throw new ControllerListenerException(AonUtil.getMessage(REGISTRY_DOCUMENT_INCORRECT_ERROR));
		}
	}

	private void insertPerson(ProjectReservationGuest reservationGuest) throws ManagerBeanException {
		Person person = new Person();
		person.setRegistry(new Registry());
		person.getRegistry().setDocumentType(reservationGuest.getDocumentType());
		person.getRegistry().setDocumentCountry(reservationGuest.getDocumentCountry());
		person.getRegistry().setDocument(reservationGuest.getDocument());
		person.setName(reservationGuest.getName());
		person.setFirstSurname(reservationGuest.getSurname());
		person.setSecondSurname(reservationGuest.getSurname2());
		person.setBirthDate(reservationGuest.getBirthDate());
		person = (Person)BeanManager.getManagerBean(Person.class).insert(person);

		reservationGuest.setPerson(person);
		
		if (StringUtils.isNotBlank(reservationGuest.getEmail())) {
			RegistryMedia email = new RegistryMedia();
			email.setRegistry(person.getRegistry());
			email.setMediaType(MediaType.EMAIL);
			email.setValue(reservationGuest.getEmail());
			BeanManager.getManagerBean(RegistryMedia.class).insert(email);
		}

		if (StringUtils.isNotBlank(reservationGuest.getPhone())) {
			RegistryMedia phone = new RegistryMedia();
			phone.setRegistry(person.getRegistry());
			phone.setMediaType(MediaType.CELLULAR);
			phone.setValue(reservationGuest.getPhone());
			BeanManager.getManagerBean(RegistryMedia.class).insert(phone);
		}

		if (StringUtils.isNotBlank(reservationGuest.getAddress()) && StringUtils.isNotBlank(reservationGuest.getCity())) {
			RegistryAddress address = new RegistryAddress();
			address.setRegistry(person.getRegistry());
			address.setAddressType(AddressType.MAIN);
			address.setAddress(reservationGuest.getAddress());
			address.setNumber(reservationGuest.getNumber());
			address.setAddress2(reservationGuest.getAddress2());
			address.setZip(reservationGuest.getZip());
			address.setCity(reservationGuest.getCity());
			address.setGeozone(obtainGeoZone(reservationGuest.getProvince()));
			address.setAddress3((address.getGeozone()==null) ? reservationGuest.getProvince() : null);
			BeanManager.getManagerBean(RegistryAddress.class).insert(address);
		}
	}

	private void updatePerson(ProjectReservationGuest reservationGuest) throws ManagerBeanException {
		Person person = reservationGuest.getPerson();
		person.setName(reservationGuest.getName());
		person.setFirstSurname(reservationGuest.getSurname());
		person.setSecondSurname(reservationGuest.getSurname2());
		person.setBirthDate(reservationGuest.getBirthDate());
		person = (Person)BeanManager.getManagerBean(Person.class).update(person);

		reservationGuest.setPerson(person);

		if (StringUtils.isNotBlank(reservationGuest.getEmail())) {
			RegistryMedia email = person.getRegistry().getEmail();
			if (email == null) {
				email = new RegistryMedia();
				email.setRegistry(person.getRegistry());
				email.setMediaType(MediaType.EMAIL);
				email.setValue(reservationGuest.getEmail());
				BeanManager.getManagerBean(RegistryMedia.class).insert(email);
			} else if (!email.getValue().equals(reservationGuest.getEmail())) {
				email.setValue(reservationGuest.getEmail());
				BeanManager.getManagerBean(RegistryMedia.class).update(email);
			}
		}

		if (StringUtils.isNotBlank(reservationGuest.getPhone())) {
			RegistryMedia phone = person.getRegistry().getCellular();
			if (phone == null) {
				phone = new RegistryMedia();
				phone.setRegistry(person.getRegistry());
				phone.setMediaType(MediaType.CELLULAR);
				phone.setValue(reservationGuest.getPhone());
				BeanManager.getManagerBean(RegistryMedia.class).insert(phone);
			} else if (!phone.getValue().equals(reservationGuest.getPhone())) {
				phone.setValue(reservationGuest.getPhone());
				BeanManager.getManagerBean(RegistryMedia.class).update(phone);
			}
		}

		if (StringUtils.isNotBlank(reservationGuest.getAddress()) && StringUtils.isNotBlank(reservationGuest.getCity())) {
			RegistryAddress address = person.getRegistry().getDefaultAddress();
			if (address == null) {
				address = new RegistryAddress();
				address.setRegistry(person.getRegistry());
				address.setAddressType(AddressType.MAIN);
				address.setAddress(reservationGuest.getAddress());
				address.setNumber(reservationGuest.getNumber());
				address.setAddress2(reservationGuest.getAddress2());
				address.setZip(reservationGuest.getZip());
				address.setCity(reservationGuest.getCity());
				address.setGeozone(obtainGeoZone(reservationGuest.getProvince()));
				address.setAddress3((address.getGeozone()==null) ? reservationGuest.getProvince() : null);
				BeanManager.getManagerBean(RegistryAddress.class).insert(address);
			} else {
				address.setAddress(reservationGuest.getAddress());
				address.setNumber(reservationGuest.getNumber());
				address.setAddress2(reservationGuest.getAddress2());
				address.setZip(reservationGuest.getZip());
				address.setCity(reservationGuest.getCity());
				address.setGeozone(obtainGeoZone(reservationGuest.getProvince()));
				address.setAddress3((address.getGeozone()==null) ? reservationGuest.getProvince() : null);
				BeanManager.getManagerBean(RegistryAddress.class).update(address);
			}
		}
	}

	private GeoZone obtainGeoZone(String geoZoneName) throws ManagerBeanException {
		IManagerBean geoZoneBean = BeanManager.getManagerBean(GeoZone.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(geoZoneBean.getFieldName(IEntityAlias.GEO_ZONE_NAME), geoZoneName);
		criteria.addEqualExpression(geoZoneBean.getFieldName(IEntityAlias.GEO_ZONE_SYSTEM), Boolean.TRUE);
		for (ITransferObject ito : geoZoneBean.getList(criteria)) {
			return (GeoZone)ito;
		}
		return null;
	}

}