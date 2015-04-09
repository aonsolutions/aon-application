package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.HashMap;

import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class FiscalModel implements Serializable {

	private static final long serialVersionUID = -6772628350162797968L;
	
	private Integer id;
	private Integer domain;
	private Integer year;
	private Integer finance;
	private FiscalModelType model;
	private Period period;
	private Administration administration;
	private boolean finished;
	private boolean  confidential;
	private boolean complementary;
	private boolean replacement;
	private boolean withoutActivity;
	private Integer number;
	private Integer replacedNumber;
	private String comments;
	private String document;
	private String surname;
	private String name;
	private String streetInitial;
	private String streetName;
	private String streetNumber;
	private String streetStair;
	private String streetFloor;
	private String streetDoor;
	private String phone;
	private String town;
	private String province;
	private String zip;
	private String admonAeat;
	private String contactPerson;
	private String contactPhone;
	private String contactCellular;
	private String contactEmail;
	
	HashMap<String,FiscalModelDetail> map;
	
	public Integer getId() {
		return id;
	}
	public FiscalModel setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public FiscalModel setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getYear() {
		return year;
	}
	public FiscalModel setYear(Integer year) {
		this.year = year;
		return this;
	}
	public Integer getFinance() {
		return finance;
	}
	public FiscalModel setFinance(Integer finance) {
		this.finance = finance;
		return this;
	}
	public FiscalModelType getModel() {
		return model;
	}
	public FiscalModel setModel(FiscalModelType model) {
		this.model = model;
		return this;
	}
	public Period getPeriod() {
		return period;
	}
	public FiscalModel setPeriod(Period period) {
		this.period = period;
		return this;
	}
	public Administration getAdministration() {
		return administration;
	}
	public FiscalModel setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}
	public boolean isFinished() {
		return finished;
	}
	public FiscalModel setFinished(boolean finished) {
		this.finished = finished;
		return this;
	}
	public boolean isConfidential() {
		return confidential;
	}
	public FiscalModel setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}
	public boolean isComplementary() {
		return complementary;
	}
	public FiscalModel setComplementary(boolean complementary) {
		this.complementary = complementary;
		return this;
	}
	public boolean isReplacement() {
		return replacement;
	}
	public FiscalModel setReplacement(boolean replacement) {
		this.replacement = replacement;
		return this;
	}
	public boolean isWithoutActivity() {
		return withoutActivity;
	}
	public FiscalModel setWithoutActivity(boolean withoutActivity) {
		this.withoutActivity = withoutActivity;
		return this;
	}
	public Integer getNumber() {
		return number;
	}
	public FiscalModel setNumber(Integer number) {
		this.number = number;
		return this;
	}
	public Integer getReplacedNumber() {
		return replacedNumber;
	}
	public FiscalModel setReplacedNumber(Integer replacedNumber) {
		this.replacedNumber = replacedNumber;
		return this;
	}
	public String getComments() {
		return comments;
	}
	public FiscalModel setComments(String comments) {
		this.comments = comments;
		return this;
	}
	public String getDocument() {
		return document;
	}
	public FiscalModel setDocument(String document) {
		this.document = document;
		return this;
	}
	public String getSurname() {
		return surname;
	}
	public FiscalModel setSurname(String surname) {
		this.surname = surname;
		return this;
	}
	public String getName() {
		return name;
	}
	public FiscalModel setName(String name) {
		this.name = name;
		return this;
	}
	public String getStreetInitial() {
		return streetInitial;
	}
	public FiscalModel setStreetInitial(String streetInitial) {
		this.streetInitial = streetInitial;
		return this;
	}
	public String getStreetName() {
		return streetName;
	}
	public FiscalModel setStreetName(String streetName) {
		this.streetName = streetName;
		return this;
	}
	public String getStreetNumber() {
		return streetNumber;
	}
	public FiscalModel setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
		return this;
	}
	public String getStreetStair() {
		return streetStair;
	}
	public FiscalModel setStreetStair(String streetStair) {
		this.streetStair = streetStair;
		return this;
	}
	public String getStreetFloor() {
		return streetFloor;
	}
	public FiscalModel setStreetFloor(String streetFloor) {
		this.streetFloor = streetFloor;
		return this;
	}
	public String getStreetDoor() {
		return streetDoor;
	}
	public FiscalModel setStreetDoor(String streetDoor) {
		this.streetDoor = streetDoor;
		return this;
	}
	public String getPhone() {
		return phone;
	}
	public FiscalModel setPhone(String phone) {
		this.phone = phone;
		return this;
	}
	public String getTown() {
		return town;
	}
	public FiscalModel setTown(String town) {
		this.town = town;
		return this;
	}
	public String getProvince() {
		return province;
	}
	public FiscalModel setProvince(String province) {
		this.province = province;
		return this;
	}
	public String getZip() {
		return zip;
	}
	public FiscalModel setZip(String zip) {
		this.zip = zip;
		return this;
	}
	public String getAdmonAeat() {
		return admonAeat;
	}
	public FiscalModel setAdmonAeat(String admonAeat) {
		this.admonAeat = admonAeat;
		return this;
	}
	public String getContactPerson() {
		return contactPerson;
	}
	public FiscalModel setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
		return this;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public FiscalModel setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
		return this;
	}
	public String getContactCellular() {
		return contactCellular;
	}
	public FiscalModel setContactCellular(String contactCellular) {
		this.contactCellular = contactCellular;
		return this;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public FiscalModel setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
		return this;
	}
	
	public HashMap<String, FiscalModelDetail> getMap() {
		if (map == null) {
			map = new HashMap<String, FiscalModelDetail>();
		}
		return map;
	}
	public FiscalModel setMap(HashMap<String, FiscalModelDetail> map) {
		this.map = map;
		return this;
	}
	public void put(FiscalModelDetail detail) {
		getMap().put(detail.getType(), detail);
	}

	public String getDescription(IFiscalModelKey key) {
		return getDescription(key.getValue());
	}

	public String getDescription(String key) {
		return ensureDetail(key).getDescription();
	}

	public void putDescription(IFiscalModelKey key, String description) {
		putDescription(key.getValue(), description);
	}

	public void putDescription(String key, String description) {
		FiscalModelDetail detail = ensureDetail(key);
		detail.setDescription(description);
	}

	public double getAmount(IFiscalModelKey key) {
		return getAmount(key.getValue());
	}

	public double getAmount(String key) {
		return ensureDetail(key).getAmount();
	}

	public void putAmount(IFiscalModelKey key, double amount) {
		putAmount(key.getValue(), amount);
	}

	public void putAmount(String key, double amount) {
		FiscalModelDetail detail = ensureDetail(key);
		detail.setAmount(amount);
	}

	public FiscalModelDetail ensureDetail(IFiscalModelKey key) {
		return ensureDetail(key.getValue());
	}

	public FiscalModelDetail ensureDetail(String key) {
		if (!getMap().containsKey(key)) {
			FiscalModelDetail detail = new FiscalModelDetail();
			detail.setType(key);
			getMap().put(key, detail);
		}
		return getMap().get(key);
	}
}
