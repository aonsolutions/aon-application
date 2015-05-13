package com.esferalia.aon.ui.pms.controller;

import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservationGuest;

public class ProjectReservationGuestController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void onDocumentTypeChanged(ValueChangeEvent event) throws ManagerBeanException {
		ProjectReservationGuest reservationGuest = (ProjectReservationGuest)getTo();
		reservationGuest.setDocumentType((DocumentType)event.getNewValue());
		obtainPersonData(reservationGuest);
	}

	public void onDocumentCountryChanged(ValueChangeEvent event) throws ManagerBeanException {
		ProjectReservationGuest reservationGuest = (ProjectReservationGuest)getTo();
		reservationGuest.setDocumentCountry((Country)event.getNewValue());
		obtainPersonData(reservationGuest);
	}

	public void onDocumentChanged(ValueChangeEvent event) throws ManagerBeanException {
		ProjectReservationGuest reservationGuest = (ProjectReservationGuest)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservationGuest.setDocument(event.getNewValue().toString());
		} else {
			reservationGuest.setDocument(null);
		}
		obtainPersonData(reservationGuest);
	}

	private void obtainPersonData(ProjectReservationGuest reservationGuest) throws ManagerBeanException {
		Person person = null;
		if (StringUtils.isNotBlank(reservationGuest.getDocument()) && (!reservationGuest.isDocumentValidable() || reservationGuest.isValidDocument())) {
			IManagerBean personBean = BeanManager.getManagerBean(Person.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(personBean.getFieldName(IEntityAlias.PERSON_REGISTRY_DOCUMENT_TYPE), reservationGuest.getDocumentType());
			criteria.addEqualExpression(personBean.getFieldName(IEntityAlias.PERSON_REGISTRY_DOCUMENT_COUNTRY), reservationGuest.getDocumentCountry());
			criteria.addEqualExpression(personBean.getFieldName(IEntityAlias.PERSON_REGISTRY_DOCUMENT), reservationGuest.getDocument());
			criteria.addOrder(personBean.getFieldName(IEntityAlias.PERSON_REGISTRY_ID), false);
			List<ITransferObject> personList = personBean.getList(criteria);
			if (personList.size() > 0) {
				person = (Person)personList.get(0);
			}
		}

		if (person != null) {
			RegistryMedia email = person.getRegistry().getEmail();
			RegistryMedia phone = person.getRegistry().getCellular();
			RegistryAddress address = person.getRegistry().getDefaultAddress();
			String country = (address!=null && address.getGeozone()!= null) ? address.getGeozone().getGeoZoneCountry().getCode() : null;

			reservationGuest.setName(person.getName());
			reservationGuest.setSurname(person.getFirstSurname());
			reservationGuest.setSurname2(person.getSecondSurname());
			reservationGuest.setBirthDate(person.getBirthDate());
			reservationGuest.setEmail((email!=null) ? email.getValue() : null);
			reservationGuest.setPhone((phone!=null) ? phone.getValue() : null);
			reservationGuest.setAddress((address!=null) ? address.getAddress() : null);
			reservationGuest.setNumber((address!=null) ? address.getNumber() : null);
			reservationGuest.setAddress2((address!=null) ? address.getAddress2() : null);
			reservationGuest.setZip((address!=null) ? address.getZip() : null);
			reservationGuest.setCity((address!=null) ? address.getCity() : null);
			reservationGuest.setProvince((address!=null) ? StringUtils.substring(address.getAddress3(), 0, 64) : null);
			reservationGuest.setCountry((country!=null) ? country : reservationGuest.getDocumentCountry().getValue());
			reservationGuest.setPerson(person);
		} else {
			reservationGuest.setPerson(null);
		}
	}

}