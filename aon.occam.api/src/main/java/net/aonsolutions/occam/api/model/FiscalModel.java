package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.type.Administration;
import net.aonsolutions.occam.api.model.type.FiscalModelDeclarationType;
import net.aonsolutions.occam.api.model.type.FiscalModelType;
import net.aonsolutions.occam.api.model.type.FiscalStatus;
import net.aonsolutions.occam.api.model.type.Period;

public class FiscalModel implements HasAudit, Serializable {

	public interface IFiscalModelKey extends Serializable {
		
		String getValue();
		int getBox();
		String getBoxFormatted();
		
	}
	
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
	private Double result;
	private Integer accountEntry;
	private Double declarationResult;
	private FiscalModelDeclarationType declarationResultType;
	
	private boolean generateFromYearStart;
	private boolean previousInvoicesAvailable;
	private boolean previousSalariesAvailable;
	private boolean generateFromYearStartAvailable;
	private boolean complementaryDeclarationAvailable;
	private boolean replacementDeclarationAvailable;

	private String creationUser;
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;
	
	private LinkedList<String> messages;
	private LinkedHashMap<String,FiscalModelDetail> map;
	
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
	
	public int getDomain() {
		return domain;
	}
	public FiscalModel setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getDomainName() {
		return domainName;
	}
	public FiscalModel setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public int getYear() {
		return year;
	}
	public FiscalModel setYear(int year) {
		this.year = year;
		return this;
	}
	
	public Optional<Finance> getFinance() {
		return Optional.ofNullable(finance);
	}
	public FiscalModel setFinance(Finance finance) {
		this.finance = finance;
		return this;
	}
	public Integer getFinanceId() {
		return finance==null?null:finance.getId();
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
	
	public FiscalStatus getStatus() {
		return this.status;
	}
	public FiscalModel setStatus(FiscalStatus status) {
		this.status = status;
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
	
	public String getFullName() {
		return (isEntity())
			?name
			:AonStringUtils.prependIfMissing(getName(),AonStringUtils.appendIfMissing(AonStringUtils.trimToNull(getSurname()), ", " ) ); 
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
	public FiscalModel setIban(String iban) {
		this.iban = iban;
		return this;
	}
	public String getFinanceBankAlias() {
		return getFinance()
			.map( Finance::getBankAlias)
			.orElse(null);
	}
	public String getFinanceIban() {
		return getFinance()
			.flatMap( Finance::getBankAccount)
			.map( BankAccount::getIban)
			.orElse(null);
	}
	public String getFinanceMaskedIban() {
		return getFinance()
			.flatMap( Finance::getBankAccount)
			.map( BankAccount::getMaskedIban)
			.orElse(null);
	}
	public String getFinanceCCC() {
		return getFinance()
			.flatMap( Finance::getBankAccount)
			.map( BankAccount::getPureCCC)
			.orElse(null);
	}
	
	public LinkedHashMap<String, FiscalModelDetail> getMap() {
		if (map == null) {
			map = new LinkedHashMap<>();
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
	
	public double getAccumulatedAmount(IFiscalModelKey key) {
		return getAccumulatedAmount(key.getValue());
	}
	public double getAccumulatedAmount(String key) {
		return ensureDetail(key).getAccumulatedAmount();
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
	public int getAmountAsInt(IFiscalModelKey key) {
		return (int) getAmount(key.getValue());
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
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public FiscalModel setCreationDate(Timestamp creationDate) {
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
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public FiscalModel setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public boolean isStrictToDeposit() {
		return (canBeSent() || isSent()) 
			&& (getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT);
	}
	
	public String getModelFullName() {
		return AonStringUtils.defaultIfBlank(FiscalModelUtils.getModelName(this),
				(getModel() != null?getModel().getName():"???") ) 
			+ " "
			+ getYear()
			+ " "
			+ (getPeriod() != null?getPeriod().getDescription() :"???")
			+ (isComplementary()?" (C)":"")
			+ (isReplacement()?" (S)":"")
			;
	}
	
	public Integer getAccountEntry() {
		return accountEntry;
	}
	public FiscalModel setAccountEntry(Integer accountEntry) {
		this.accountEntry = accountEntry;
		return this;
	}
	public boolean isRecorded() {
		return getAccountEntry() != null;
	}
	 
	public Double getDeclarationResult() {
		return declarationResult;
	}
	public FiscalModel setDeclarationResult(Double declarationResult) {
		this.declarationResult = declarationResult;
		return this;
	}
	
	public FiscalModelDeclarationType getDeclarationResultType() {
		return declarationResultType;
	}
	public FiscalModel setDeclarationResultType(FiscalModelDeclarationType declarationResultType) {
		this.declarationResultType = declarationResultType;
		return this;
	}
	
	public void setDefaultDeclarationType(){
		if (isAEAT()) {
			if (AonMathUtils.isGreatherThanZero(getDeclarationResult() )) {
				setDeclarationResultType(FiscalModelDeclarationType.DEPOSIT);
			} else {
				setDeclarationResultType(FiscalModelDeclarationType.NEGATIVE);
			}
		}
	}
	
	public boolean isGenerateFromYearStart() {
		return generateFromYearStart;
	}
	public FiscalModel setGenerateFromYearStart(boolean generateFromYearStart) {
		this.generateFromYearStart = generateFromYearStart;
		return this;
	}
	public boolean isPreviousInvoicesAvailable() {
		return previousInvoicesAvailable;
	}
	public FiscalModel setPreviousInvoicesAvailable(boolean previousInvoicesAvailable) {
		this.previousInvoicesAvailable = previousInvoicesAvailable;
		return this;
	}
	public boolean isPreviousSalariesAvailable() {
		return previousSalariesAvailable;
	}
	public FiscalModel setPreviousSalariesAvailable(boolean previousSalariesAvailable) {
		this.previousSalariesAvailable = previousSalariesAvailable;
		return this;
	}
	public boolean isGenerateFromYearStartAvailable() {
		return generateFromYearStartAvailable;
	}
	public FiscalModel setGenerateFromYearStartAvailable(boolean generateFromYearStartAvailable) {
		this.generateFromYearStartAvailable = generateFromYearStartAvailable;
		return this;
	}
	
	public boolean isComplementaryDeclarationAvailable() {
		return this.complementaryDeclarationAvailable;
	}
	public FiscalModel setComplementaryDeclarationAvailable(boolean complementaryDeclarationAvailable) {
		this.complementaryDeclarationAvailable = complementaryDeclarationAvailable;
		return this;
	}
	public boolean isReplacementDeclarationAvailable() {
		return this.replacementDeclarationAvailable;
	}
	public FiscalModel setReplacementDeclarationAvailable(boolean replacementDeclarationAvailable) {
		this.replacementDeclarationAvailable = replacementDeclarationAvailable;
		return this;
	}
	public LinkedList<String> getMessages() {
		if (messages == null) {
			messages = new LinkedList<>();
		}
		return messages;
	}
	public FiscalModel addMessage( String message ) {
		getMessages().add(message);
		return this;
	}
	public FiscalModel setMessages(LinkedList<String> messages) {
		this.messages = messages;
		return this;
	}

	public Double getResult() {
		return result;
	}
	public FiscalModel setResult(Double result) {
		this.result = result;
		return this;
	}
	
	public boolean isReplacedNumberAvailable() {
		return false;
	}
	public boolean isComplementaryNumberAvailable() {
		return false;
	}
	public boolean isToDeduceAvailable() {
		return false;
	}
	public boolean isNegativeAvailable(){
		return false;
	}
	public boolean isDiffCalculationAvailable() {
		return false;
	}
	public boolean isDiffCalculationDisabled() {
		return false;
	}
	public void setDiffCalculationDisabled(boolean diffCalculationDisabled) {
		// REDEFINE
	}
	
	public boolean canBeDeleted() {
		return getStatus() != FiscalStatus.FINISHED
			&& getStatus() != FiscalStatus.SENT
			&& getStatus() != FiscalStatus.CUSTOMER_CHECK
			&& getStatus() != FiscalStatus.CUSTOMER_ACCEPTED;
	}
	
	public String getNrc() {
    	return ensureDetail("NRC").getDescription();
	}
	public FiscalModel setNrc(String nrc) {
		ensureDetail("NRC").setDescription(nrc);
		return this;
	}
	
	// PARA LOS APLAZAMIENTOS
	public int getPlazos() {
    	return (int) ensureDetail("PLAZOS").getAmount();
	}
	public FiscalModel setPlazos(int p) {
		ensureDetail("PLAZOS").setAmount(p);
		return this;
	}
	
	// Lo pongo como String, para que sea más fácil su manejo, pues en fs_model_detail, realmente se graba como un string
	public String getFechaPlazo() {
		return ensureDetail("FECHAPLAZO").getDescription();		
	}
	public FiscalModel setFechaPlazo(String d) {
		ensureDetail("FECHAPLAZO").setDescription(d);
		return this;
	}	
	
	public boolean isFirstPeriod() {
		return getPeriod() != null && getPeriod().isFirstPeriod();
	}
	public boolean isLastPeriod() {
		return getPeriod() != null && getPeriod().isLastPeriod();
	}
	public boolean isQuarterPeriod() {
		return getPeriod() != null && getPeriod().isQuarterPeriod();
	}
	public boolean isMonthPeriod() {
		return getPeriod() != null && getPeriod().isMonthPeriod();
	}
	public boolean isAraba() {
		return (getAdministration() == Administration.ALAVA);
	}
	public boolean isBizkaia() {
		return (getAdministration() == Administration.BIZKAIA);
	}
	public boolean isGipuzkoa() {
		return (getAdministration() == Administration.GIPUZKOA);
	}
	public boolean isNavarra() {
		return (getAdministration() == Administration.NAVARRA);
	}
	public boolean isAEAT() {
		return (getAdministration() == Administration.COMMON_TERRITORY);
	}
	public boolean canBeSent() {
		return isFinished() || isCustomerAccepted(); 
	}
	
	// El modelo puede ser enviado al "Servicio de validación y prueba de impresión de la AEAT"
	// si "Puede ser Enviado" o "es Informativa y no está Presentado"
	public boolean canBeValidated() {
		return canBeSent() || (getModel().isInformative() && !isSent()) ; 
	}
	
	public boolean isNotEditable() {
		return !isEditable(); 
	}
	public boolean isEditable() {
		return isPending() || isCustomerRejected(); 
	}
	public boolean isPending() {
		return getStatus() == FiscalStatus.PENDING;
	}
	public boolean isFinished() {
		return getStatus() == FiscalStatus.FINISHED;
	}
	public boolean isSent() {
		return getStatus() == FiscalStatus.SENT;
	}
	public boolean isBlocked() {
		return getStatus() == FiscalStatus.BLOCKED;
	}
	public boolean isCustomerCheck() {
		return getStatus() == FiscalStatus.CUSTOMER_CHECK;
	}
	public boolean isCustomerAccepted() {
		return getStatus() == FiscalStatus.CUSTOMER_ACCEPTED;
	}
	public boolean isCustomerRejected() {
		return getStatus() == FiscalStatus.CUSTOMER_REJECTED;
	}
	

}

