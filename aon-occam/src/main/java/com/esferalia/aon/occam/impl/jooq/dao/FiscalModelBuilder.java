package com.esferalia.aon.occam.impl.jooq.dao;

import java.util.Date;

import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class FiscalModelBuilder<FM extends FiscalModel> {
	
	public static interface Template {
		Integer getId();
		Integer getDomain();
		Integer getYear();
		Period getPeriod();
		Administration getAdministration();
		FiscalStatus getStatus();
		boolean isConfidential();
		boolean isComplementary();
		boolean isReplacement();
		boolean isWithoutActivity();
		FiscalModelType getModel();
		String getNumber();
		String getReplacedNumber();
		String  getComments();
		Finance getFinance();
		String  getDocument();
		String  getSurname();
		String  getName();
		String  getStreetInitial();
		String  getStreetName();
		String  getStreetNumber();
		String  getStreetStair();
		String  getStreetFloor();
		String  getStreetDoor();
		String  getPhone();
		String  getTown();
		String  getProvince();
		String getZip();
		String getAdmonAeat();
		String getContactPerson();
		String getContactPhone();
		String getContactCellular();
		String getContactEmail();
		String getCreationUser();
		Date getCreationDate();
		String getModificationUser();
		Date getModificationDate();
	}

	private FM model;
	
	public FiscalModelBuilder(FM model) {
		this.model = model;
	}
	
	public FiscalModelBuilder<FM> setId(Integer id) {
		model.setId(id);
		return this;
	}
	private FiscalModelBuilder<FM> setDomain(Integer domain) {
		model.setDomain(domain);
		return this;
	}
	
	private FiscalModelBuilder<FM> setYear(Integer year) {
		model.setYear(year);
		return this;
	}

	private FiscalModelBuilder<FM> setPeriod(Period period) {
		model.setPeriod(period);
		return this;
	}
	
	private FiscalModelBuilder<FM> setAdministration(Administration administration) {
		model.setAdministration(administration);
		return this;
	}
	
	private FiscalModelBuilder<FM> setStatus(FiscalStatus status) {
		model.setStatus(status);
		return this;
	}
	private FiscalModelBuilder<FM> setConfidential(boolean confidential) {
		model.setConfidential(confidential);
		return this;
	}
	private FiscalModelBuilder<FM> setComplementary(boolean complementary) {
		model.setComplementary(complementary);
		return this;
	}
	private FiscalModelBuilder<FM> setReplacement(boolean replacement) {
		model.setReplacement(replacement);
		return this;
	}
	private FiscalModelBuilder<FM> setWithoutActivity(boolean withoutActivity) {
		model.setWithoutActivity(withoutActivity);
		return this;
	}

	private FiscalModelBuilder<FM> setModel(FiscalModelType modelType) {
		model.setModel(modelType);
		return this;
	}

	private FiscalModelBuilder<FM> setNumber(String number) {
		model.setNumber(number);
		return this;
	}

	private FiscalModelBuilder<FM> setReplacedNumber(String replacedNumber) {
		model.setReplacedNumber(replacedNumber);
		return this;
	}

	private FiscalModelBuilder<FM> setComments(String comments) {
		model.setComments(comments);
		return this;
	}

	private FiscalModelBuilder<FM> setFinance(Finance finance) {
		model.setFinance(finance);
		return this;
	}

	private FiscalModelBuilder<FM> setDocument(String document) {
		model.setDocument(document);
		return this;
	}
	private FiscalModelBuilder<FM> setSurname(String surname) {
		model.setSurname(surname);
		return this;
	}

	private FiscalModelBuilder<FM> setName(String name) {
		model.setName(name);
		return this;
	}

	private FiscalModelBuilder<FM> setStreetInitial(String streetInitial) {
		model.setStreetInitial(streetInitial);
		return this;
	}

	private FiscalModelBuilder<FM> setStreetName(String streetName) {
		model.setStreetName(streetName);
		return this;
	}

	private FiscalModelBuilder<FM> setStreetNumber(String streetNumber) {
		model.setStreetNumber(streetNumber);
		return this;
	}
	
	private FiscalModelBuilder<FM> setStreetStair(String streetStair) {
		model.setStreetStair(streetStair);
		return this;
	}

	private FiscalModelBuilder<FM> setStreetFloor(String streetFloor) {
		model.setStreetFloor(streetFloor);
		return this;
	}
	
	private FiscalModelBuilder<FM> setStreetDoor(String streetDoor) {
		model.setStreetDoor(streetDoor);
		return this;
	}

	private FiscalModelBuilder<FM> setPhone(String phone) {
		model.setPhone(phone);
		return this;
	}

	private FiscalModelBuilder<FM> setTown(String town) {
		model.setTown(town);
		return this;
	}

	private FiscalModelBuilder<FM> setProvince(String province) {
		model.setProvince(province);
		return this;
	}

	private FiscalModelBuilder<FM> setZip(String zip) {
		model.setZip(zip);
		return this;
	}

	private FiscalModelBuilder<FM> setAdmonAeat(String admonAeat) {
		model.setAdmonAeat(admonAeat);
		return this;
	}

	private FiscalModelBuilder<FM> setContactPerson(String contactPerson) {
		model.setContactPerson(contactPerson);
		return this;
	}

	private FiscalModelBuilder<FM> setContactPhone(String contactPhone) {
		model.setContactPhone(contactPhone);
		return this;
	}
	
	private FiscalModelBuilder<FM> setContactCellular(String contactCellular) {
		model.setContactCellular(contactCellular);
		return this;
	}

	private FiscalModelBuilder<FM> setContactEmail(String contactEmail) {
		model.setContactEmail(contactEmail);
		return this;
	}


	private FiscalModelBuilder<FM> setCreationUser(String creationUser) {
		model.setCreationUser(creationUser);
		return this;
	}
	
	private FiscalModelBuilder<FM> setCreationDate(Date creationDate) {
		model.setCreationDate(creationDate);
		return this;
	}
	private FiscalModelBuilder<FM> setModificationUser(String modificationUser) {
		model.setModificationUser(modificationUser);
		return this;
	}
	
	private FiscalModelBuilder<FM> setModificationDate(Date modificationDate) {
		model.setModificationDate(modificationDate);
		return this;
	}
	
	public FM create(Template template) {
		setId(template.getId())
		.setDomain(template.getDomain());
		setYear(template.getYear());
		setPeriod(template.getPeriod());
		setAdministration(template.getAdministration());
		setStatus(template.getStatus());
		setConfidential(template.isConfidential());
		setComplementary(template.isComplementary());
		setReplacement(template.isReplacement());
		setWithoutActivity(template.isWithoutActivity());
		setModel(template.getModel());
		setNumber(template.getNumber());
		setReplacedNumber(template.getReplacedNumber());
		setComments(template.getComments());
		setFinance(template.getFinance());
		setDocument(template.getDocument());
		setSurname(template.getSurname());
		setName(template.getName());
		setStreetInitial(template.getStreetInitial());
		setStreetName(template.getStreetName());
		setStreetNumber(template.getStreetNumber());
		setStreetStair(template.getStreetStair());
		setStreetFloor(template.getStreetFloor());
		setStreetDoor(template.getStreetDoor());
		setPhone(template.getPhone());
		setTown(template.getTown());
		setProvince(template.getProvince());
		setZip(template.getZip());
		setAdmonAeat(template.getAdmonAeat());
		setContactPerson(template.getContactPerson());
		setContactPhone(template.getContactPhone());
		setContactCellular(template.getContactCellular());
		setContactEmail(template.getContactEmail());
		setCreationUser(template.getCreationUser());
		setCreationDate(template.getCreationDate());
		setModificationUser(template.getModificationUser());
		setModificationDate(template.getModificationDate());
		return model;
	}




}
