package com.esferalia.aon.ui.pms.controller;

import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

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
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservationGuest;

public class ProjectReservationGuestController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String phonePrefix;
	private String phoneNumber;

	public String getPhonePrefix() {
		return phonePrefix;
	}
	public void setPhonePrefix(String phonePrefix) {
		this.phonePrefix = phonePrefix;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void onDocumentTypeChanged(ValueChangeEvent event) throws ManagerBeanException {
		ProjectReservationGuest reservationGuest = (ProjectReservationGuest)getTo();
		reservationGuest.setDocumentType((DocumentType)event.getNewValue());
		obtainPersonData(reservationGuest);
	}

	public void onDocumentCountryChanged(ValueChangeEvent event) throws ManagerBeanException {
		ProjectReservationGuest reservationGuest = (ProjectReservationGuest)getTo();
		reservationGuest.setDocumentCountry((Country)event.getNewValue());
		obtainPersonData(reservationGuest);
		if (reservationGuest.getCountry() == null || reservationGuest.getCountry() == (Country)event.getOldValue()) {
			reservationGuest.setCountry(reservationGuest.getDocumentCountry());
		}
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
			person = obtainPerson(reservationGuest);
		}

		if (person != null) {
			RegistryMedia email = person.getRegistry().getEmail();
			RegistryMedia phone = person.getRegistry().getCellular();
			RegistryAddress address = person.getRegistry().getDefaultAddress();
			GeoZone geoZone = (address!=null && address.getGeozone()!=null && address.getGeozone().getId()!=null) ? address.getGeozone() : null;

			reservationGuest.setName(person.getName());
			reservationGuest.setSurname(person.getFirstSurname());
			reservationGuest.setSurname2(person.getSecondSurname());
			reservationGuest.setDocumentExpDate(obtainDocumentExpDate(person.getId()));
			reservationGuest.setBirthDate(person.getBirthDate());
			reservationGuest.setEmail((email!=null) ? email.getValue() : null);
			reservationGuest.setPhone((phone!=null) ? phone.getValue() : null);
			reservationGuest.setAddress((address!=null) ? address.getAddress() : null);
			reservationGuest.setNumber((address!=null) ? address.getNumber() : null);
			reservationGuest.setAddress2((address!=null) ? address.getAddress2() : null);
			reservationGuest.setZip((address!=null) ? address.getZip() : null);
			reservationGuest.setCity((address!=null) ? address.getCity() : null);
			reservationGuest.setProvince((address!=null) ? ((geoZone!=null) ? geoZone.getName() : StringUtils.substring(address.getAddress3(), 0, 64)) : null);
			reservationGuest.setCountry((geoZone!=null) ? Country.obtainCountry(address.getGeozone().getGeoZoneCountry().getCode()) : null);
			reservationGuest.setPerson(person);

			setPhonePrefix(obtainPhonePrefix((phone!=null) ? phone.getValue() : null));
			setPhoneNumber(obtainPhoneNumber((phone!=null) ? phone.getValue() : null));
		} else {
			reservationGuest.setPerson(null);
		}
	}

	public Person obtainPerson(ProjectReservationGuest reservationGuest) throws ManagerBeanException {
		IManagerBean personBean = BeanManager.getManagerBean(Person.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(personBean.getFieldName(IEntityAlias.PERSON_REGISTRY_DOCUMENT_TYPE), reservationGuest.getDocumentType());
		criteria.addEqualExpression(personBean.getFieldName(IEntityAlias.PERSON_REGISTRY_DOCUMENT_COUNTRY), reservationGuest.getDocumentCountry());
		criteria.addEqualExpression(personBean.getFieldName(IEntityAlias.PERSON_REGISTRY_DOCUMENT), reservationGuest.getDocument());
		criteria.addOrder(personBean.getFieldName(IEntityAlias.PERSON_REGISTRY_ID), false);
		List<ITransferObject> personList = personBean.getList(criteria);
		if (personList.size() > 0) {
			return (Person)personList.get(0);
		}
		return null;
	}

	public Date obtainDocumentExpDate(Integer personId) throws ManagerBeanException {
		IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PERSON_ID), personId);
		criteria.addNotNullExpression(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_DOCUMENT_EXP_DATE));
		criteria.addOrder(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_DOCUMENT_EXP_DATE), false);
		Projection prjExpDate = Projection.property(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_DOCUMENT_EXP_DATE));
		List<?> resultList = reservationGuestBean.getList(new ProjectionList(prjExpDate), criteria);
		if (resultList.size() > 0 && resultList.get(0) != null) {
			return (Date)resultList.get(0);
		}
		return null;
	}

	public String obtainPhonePrefix(String phone) {
		String prefix = "+";
		if (phone != null) {
			if (phone.contains("(") && phone.contains(")")) {
				prefix = StringUtils.substring(phone, phone.indexOf("(")+1, phone.indexOf(")"));
			}
		}
		return prefix;
	}

	public String obtainPhoneNumber(String phone) {
		String number = null;
		if (phone != null) {
			if (phone.contains("(") && phone.contains(")")) {
				number =  StringUtils.substring(phone, phone.indexOf(")")+1);
			} else {
				number = phone;
			}
		}
		return number;
	}

	public String obtainFullPhone() {
		String phone = "";
		if (getPhonePrefix() != null && getPhonePrefix().length() > 1) {
			phone = "(" + getPhonePrefix() + ")";
		}
		if (getPhoneNumber() != null && getPhoneNumber().length() > 0) {
			phone += getPhoneNumber();
		}
		return phone;
	}

}