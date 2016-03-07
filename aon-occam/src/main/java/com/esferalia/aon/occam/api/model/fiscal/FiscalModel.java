package com.esferalia.aon.occam.api.model.fiscal;

import static com.esferalia.aon.occam.api.model.type.Administration.ALAVA;
import static com.esferalia.aon.occam.api.model.type.Administration.BIZKAIA;
import static com.esferalia.aon.occam.api.model.type.Administration.COMMON_TERRITORY;
import static com.esferalia.aon.occam.api.model.type.Administration.GIPUZKOA;
import static com.esferalia.aon.occam.api.model.type.Administration.NAVARRA;

import java.util.Date;
import java.util.LinkedHashMap;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FiscalModel implements IFiscalModel, HasAudit {

	private static final long serialVersionUID = -6772628350162797968L;
	
	private Integer id;
	private int domain;
	private String domainName;
	private int year;
	private Finance finance;
	private FiscalModelType model;
	private Period period;
	private Administration administration;
	private FiscalStatus status;
	private boolean  confidential;
	private boolean complementary;
	private boolean replacement;
	private boolean withoutActivity;
	private String number;
	private String replacedNumber;
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
	private String iban;

	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	private LinkedHashMap<String,FiscalModelDetail> map;
	
	@Override
	public Integer getId() {
		return id;
	}
	public FiscalModel setId(Integer id) {
		this.id = id;
		return this;
	}
	public boolean isNew() {
		return id==null;
	}
	
	@Override
	public int getDomain() {
		return domain;
	}
	public FiscalModel setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	@Override
	public String getDomainName() {
		return domainName;
	}
	public FiscalModel setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	@Override
	public int getYear() {
		return year;
	}
	public FiscalModel setYear(int year) {
		this.year = year;
		return this;
	}
	public Finance getFinance() {
		return finance;
	}
	public FiscalModel setFinance(Finance finance) {
		this.finance = finance;
		return this;
	}
	@Override
	public FiscalModelType getModel() {
		return model;
	}
	public FiscalModel setModel(FiscalModelType model) {
		this.model = model;
		return this;
	}
	public String getModelName() {
		return getModel().getName(administration, period);
	}
	
	@Override
	public Period getPeriod() {
		return period;
	}
	public FiscalModel setPeriod(Period period) {
		this.period = period;
		return this;
	}
	@Override
	public Administration getAdministration() {
		return administration;
	}
	public FiscalModel setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}
	public boolean isFinished() {
		return getStatus() == FiscalStatus.FINISHED;
	}
	public boolean isNotFinished() {
		return getStatus() != FiscalStatus.FINISHED;
	}
	public FiscalModel setStatus(FiscalStatus status) {
		this.status = status;
		return this;
	}
	@Override
	public FiscalStatus getStatus() {
		return this.status;
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
	@Override
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
	public String getNumber() {
		return number;
	}
	public FiscalModel setNumber(String number) {
		this.number = number;
		return this;
	}
	public String getReplacedNumber() {
		return replacedNumber;
	}
	public FiscalModel setReplacedNumber(String replacedNumber) {
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
	@Override
	public String getDocument() {
		return document;
	}
	public FiscalModel setDocument(String document) {
		this.document = document;
		return this;
	}
	public boolean isEntity() {
		return AonDocumentUtil.isEntity(getDocument());
	}
	
	@Override
	public String getSurname() {
		return surname;
	}
	public FiscalModel setSurname(String surname) {
		this.surname = surname;
		return this;
	}
	@Override
	public String getName() {
		return name;
	}
	public FiscalModel setName(String name) {
		this.name = name;
		return this;
	}
	
	@Override
	public String getFullName() {
		return (isEntity())
			?name
			:AonStringUtils.prependIfMissing(getName(),AonStringUtils.appendIfMissing(
					AonStringUtils.trimToNull(getSurname()), " ," ) ); 
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
	public String getIban() {
		return iban;
	}
	public void setIban(String iban) {
		this.iban = iban;
	}
	public String getFinanceBankAlias() {
		return (getFinance() != null?getFinance().getBankAlias():null);
	}
	public String getFinanceIban() {
		return (getFinance() != null && getFinance().getBankAccount() != null)?getFinance().getBankAccount().getIban():null;
	}
	public String getFinanceMaskedIban() {
		return (getFinance() != null && getFinance().getBankAccount() != null)?getFinance().getBankAccount().getMaskedIban():null;
	}
	public String getFinanceCCC() {
		return (getFinance() != null && getFinance().getBankAccount() != null)?getFinance().getBankAccount().getPureCCC():null;
	}
	
	public LinkedHashMap<String, FiscalModelDetail> getMap() {
		if (map == null) {
			map = new LinkedHashMap<String, FiscalModelDetail>();
		}
		return map;
	}
	public FiscalModel setMap(LinkedHashMap<String, FiscalModelDetail> map) {
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
	
	public double getDeclaredAmount(IFiscalModelKey key) {
		return getDeclaredAmount(key.getValue());
	}
	public double getDeclaredAmount(String key) {
		return ensureDetail(key).getResultAmount();
	}
	public double getResultAmount(IFiscalModelKey key) {
		return getResultAmount(key.getValue());
	}
	public double getResultAmount(String key) {
		return ensureDetail(key).getResultAmount();
	}
	public double getAdjustAmount(IFiscalModelKey key) {
		return getAdjustAmount(key.getValue());
	}
	public double getAdjustAmount(String key) {
		return ensureDetail(key).getAdjustAmount();
	}
	public double getAmount(IFiscalModelKey key) {
		return getAmount(key.getValue());
	}
	public boolean getCheck(IFiscalModelKey key) {
		return getAmount(key.getValue()) == 1;
	}

	public double getAmount(String key) {
		return ensureDetail(key).getAmount();
	}

	public void putAmount(IFiscalModelKey key, double amount) {
		putAmount(key.getValue(), amount);
	}
	public void addAmount(IFiscalModelKey key, double amount) {
		FiscalModelDetail detail = ensureDetail(key);
		putAmount(key.getValue(), AonMathUtils.round(detail.getAmount() + amount));
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
	
	public static void map(FiscalModel from,FiscalModel to) {
		to.setId(from.getId());
		to.setAdministration(from.getAdministration());
		to.setModel(from.getModel());
		to.setYear(from.getYear());
		to.setPeriod(from.getPeriod());
		to.setReplacement(from.isReplacement());
		to.setDomain(from.getDomain());
		to.setFinance(from.getFinance());
		to.setStatus(from.getStatus());
		to.setConfidential(from.isConfidential());
		to.setComplementary(from.isComplementary());
		to.setWithoutActivity(from.isWithoutActivity());
		to.setNumber(from.getNumber());
		to.setReplacedNumber(from.getReplacedNumber());
		to.setComments(from.getComments());
		to.setDocument(from.getDocument());
		to.setSurname(from.getSurname());
		to.setName(from.getName());
		to.setStreetInitial(from.getStreetInitial());
		to.setStreetName(from.getStreetName());
		to.setStreetNumber(from.getStreetNumber());
		to.setStreetStair(from.getStreetStair());
		to.setStreetFloor(from.getStreetFloor());
		to.setStreetDoor(from.getStreetDoor());
		to.setPhone(from.getPhone());
		to.setTown(from.getTown());
		to.setProvince(from.getProvince());
		to.setZip(from.getZip());
		to.setAdmonAeat(from.getAdmonAeat());
		to.setContactPerson(from.getContactPerson());
		to.setContactPhone(from.getContactPhone());
		to.setContactCellular(from.getContactCellular());
		to.setContactEmail(from.getContactEmail());
		to.setIban(from.getIban());;
		to.setMap(from.getMap());
	}

	public boolean isAraba() {
		return (administration == ALAVA);
	}
	public boolean isBizkaia() {
		return (administration == BIZKAIA);
	}
	public boolean isGipuzkoa() {
		return (administration == GIPUZKOA);
	}
	public boolean isNavarra() {
		return (administration == NAVARRA);
	}
	public boolean isAEAT() {
		return (administration == COMMON_TERRITORY);
	}

	// ---------------------------------------------------------- AUDIT
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public FiscalModel setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	public FiscalModel setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public FiscalModel setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	public FiscalModel setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public FiscalModelDeclarationType getDeclarationType() {
		return FiscalModelDeclarationType.safeValueOf( getDescription( getDeclarationTypeKey() ));
	}
	public void setDeclarationType(FiscalModelDeclarationType type) {
		putDescription(getDeclarationTypeKey(),type == null? null : type.getValue());
	}
	public void setDeclarationType(String type) {
		setDeclarationType( FiscalModelDeclarationType.safeValueOf(type));
	}

	public IFiscalModelKey getDeclarationTypeKey() {
		return null;
	}
	public double getResult() {
		// REDEFINE
		return 0;
	}
	public boolean isReplacedNumberAvailable() {
		// REDEFINE
		return false;
	}
	public boolean isReplacementDeclarationAvailable() {
		// REDEFINE
		return false;
	}
	public boolean isComplementaryNumberAvailable() {
		// REDEFINE
		return false;
	}
	public boolean isComplementaryDeclarationAvailable() {
		// REDEFINE
		return false;
	}
	public boolean isToDeduceAvailable() {
		// REDEFINE
		return false;
	}
	public boolean isNegativeAvailable(){
		// REDEFINE
		return false;
	}
	
	public void setDefaultDeclarationType(){
		if (isAEAT()) {
			if (AonMathUtils.isGreatherThanZero(getResult() )) {
				setDeclarationType(FiscalModelDeclarationType.DEPOSIT);
			} else {
				setDeclarationType(FiscalModelDeclarationType.NEGATIVE);
			}
		}
	}
}
