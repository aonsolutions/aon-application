package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonObjectUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountingRegistry implements Serializable {
	private static final long serialVersionUID = -5523495170211215075L;

	private AccountingRegistryType type;
	private boolean dirty;
	
	private Integer id;
	private int domain;

	private String document;
	private Country documentCountry;
	private DocumentType documentType;
	private Country nationality;
	private String name;
	private String alias;
	private int scope;

	private InvoiceTransactionType transaction;
	private boolean surcharge;
	private boolean withholding;
	private boolean withholdingFarmer;
	private boolean vatAccrualPayment;

	private Integer accountId;
	private String accountCode;
	private String accountDescription;
	
	
	private Integer addressId;
	private StreetType addressStreetType;
	private String address;
	private String addressNumber;
	private String addressTown;
	private String addressZIP;
	private Integer geozone;
	
	private String phone;
	private String phoneComments;
	private String cellular;
	private String cellularComments;
	private String fax;
	private String faxComments;
	private String email;
	private String web;
	
	public Integer getId() {
		return id;
	}

	public AccountingRegistry setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public AccountingRegistry setAccountId(Integer accountId) {
		dirty = dirty || AonObjectUtils.notEqual(this.accountId,accountId);
		this.accountId = accountId;
		return this;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public AccountingRegistry setAccountCode(String accountCode) {
		dirty = dirty || AonObjectUtils.notEqual(this.accountCode,accountCode);
		this.accountCode = accountCode;
		return this;
	}

	public String getAccountDescription() {
		return accountDescription;
	}

	public AccountingRegistry setAccountDescription(String accountDescription) {
		dirty = dirty || AonObjectUtils.notEqual(this.accountDescription,accountDescription);
		this.accountDescription = accountDescription;
		return this;
	}

	public String getAlias() {
		return alias;
	}
	public AccountingRegistry setAlias(String alias) {
		dirty = dirty || AonObjectUtils.notEqual(this.alias,alias);
		this.alias = alias;
		return this;
	}
	public String getDocument() {
		return document;
	}
	public AccountingRegistry setDocument(String document) {
		dirty = dirty || AonObjectUtils.notEqual(this.document,document);
		this.document = document;
		return this;
	}
	public Country getDocumentCountry() {
		return documentCountry;
	}
	public AccountingRegistry setDocumentCountry(Country documentCountry) {
		dirty = dirty || AonObjectUtils.notEqual(this.documentCountry,documentCountry);
		this.documentCountry = documentCountry;
		return this;
	}
	public DocumentType getDocumentType() {
		return documentType;
	}
	public AccountingRegistry setDocumentType(DocumentType documentType) {
		dirty = dirty || AonObjectUtils.notEqual(this.documentType,documentType);
		this.documentType = documentType;
		return this;
	}
	public Country getNationality() {
		return nationality;
	}
	public AccountingRegistry setNationality(Country nationality) {
		dirty = dirty || AonObjectUtils.notEqual(this.nationality,nationality);
		this.nationality = nationality;
		return this;
	}
	public String getName() {
		return name;
	}
	public AccountingRegistry setName(String name) {
		dirty = dirty || AonObjectUtils.notEqual(this.name,name);
		this.name = name;
		return this;
	}

	public AccountingRegistryType getType() {
		return type;
	}
	
	public AccountingRegistry setType(AccountingRegistryType type) {
		dirty = dirty || AonObjectUtils.notEqual(this.type,type);
		this.type = type;
		return this;
	}
	
	public int getScope() {
		return scope;
	}

	public AccountingRegistry setScope(int scope) {
		dirty = dirty || AonObjectUtils.notEqual(this.scope,scope);
		this.scope = scope;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public AccountingRegistry setDomain(int domain) {
		dirty = dirty || AonObjectUtils.notEqual(this.domain,domain);
		this.domain = domain;
		return this;
	}

	public boolean isSurcharge() {
		return surcharge;
	}

	public AccountingRegistry setSurcharge(boolean surcharge) {
		dirty = dirty || AonObjectUtils.notEqual(this.surcharge,surcharge);
		this.surcharge = surcharge;
		return this;
	}

	public boolean isWithholding() {
		return withholding;
	}

	public AccountingRegistry setWithholding(boolean withholding) {
		dirty = dirty || AonObjectUtils.notEqual(this.withholding,withholding);
		this.withholding = withholding;
		return this;
	}

	public boolean isWithholdingFarmer() {
		return withholdingFarmer;
	}

	public AccountingRegistry setWithholdingFarmer(boolean withholdingFarmer) {
		dirty = dirty || AonObjectUtils.notEqual(this.withholdingFarmer,withholdingFarmer);
		this.withholdingFarmer = withholdingFarmer;
		return this;
	}

	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}

	public AccountingRegistry setVatAccrualPayment(boolean vatAccrualPayment) {
		dirty = dirty || AonObjectUtils.notEqual(this.vatAccrualPayment,vatAccrualPayment);
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}

	public InvoiceTransactionType getTransaction() {
		return transaction;
	}

	public AccountingRegistry setTransaction(InvoiceTransactionType transaction) {
		dirty = dirty || AonObjectUtils.notEqual(this.transaction,transaction);
		this.transaction = transaction;
		return this;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public AccountingRegistry setAddressId(Integer addressId) {
		dirty = dirty || AonObjectUtils.notEqual(this.addressId,addressId);
		this.addressId = addressId;
		return this;
	}

	public StreetType getAddressStreetType() {
		return addressStreetType;
	}

	public AccountingRegistry setAddressStreetType(StreetType addressStreetType) {
		dirty = dirty || AonObjectUtils.notEqual(this.addressStreetType,addressStreetType);
		this.addressStreetType = addressStreetType;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public AccountingRegistry setAddress(String address) {
		dirty = dirty || AonObjectUtils.notEqual(this.address,address);
		this.address = address;
		return this;
	}

	public String getAddressNumber() {
		return addressNumber;
	}

	public AccountingRegistry setAddressNumber(String addressNumber) {
		dirty = dirty || AonObjectUtils.notEqual(this.addressNumber,addressNumber);
		this.addressNumber = addressNumber;
		return this;
	}

	public String getAddressTown() {
		return addressTown;
	}

	public AccountingRegistry setAddressTown(String addressTown) {
		dirty = dirty || AonObjectUtils.notEqual(this.addressTown,addressTown);
		this.addressTown = addressTown;
		return this;
	}

	public String getAddressZIP() {
		return addressZIP;
	}

	public AccountingRegistry setAddressZIP(String addressZIP) {
		dirty = dirty || AonObjectUtils.notEqual(this.addressZIP,addressZIP);
		this.addressZIP = addressZIP;
		return this;
	}
	
	public Integer getGeozone() {
		return geozone;
	}
	public AccountingRegistry setGeozone(Integer geozone) {
		dirty = dirty || AonObjectUtils.notEqual(this.geozone,geozone);
		this.geozone = geozone;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public AccountingRegistry setPhone(String phone) {
		dirty = dirty || AonObjectUtils.notEqual(this.phone,phone);
		this.phone = phone;
		return this;
	}
	public String getPhoneComments() {
		return phoneComments;
	}
	public AccountingRegistry setPhoneComments(String phoneComments) {
		dirty = dirty || AonObjectUtils.notEqual(this.phoneComments,phoneComments);
		this.phoneComments = phoneComments;
		return this;
	}
	public String getCellular() {
		return cellular;
	}

	public AccountingRegistry setCellular(String cellular) {
		dirty = dirty || AonObjectUtils.notEqual(this.cellular,cellular);
		this.cellular = cellular;
		return this;
	}
	public String getCellularComments() {
		return cellularComments;
	}
	public AccountingRegistry setCellularComments(String cellularComments) {
		dirty = dirty || AonObjectUtils.notEqual(this.cellularComments,cellularComments);
		this.cellularComments = phoneComments;
		return this;
	}

	public String getFax() {
		return fax;
	}

	public AccountingRegistry setFax(String fax) {
		dirty = dirty || AonObjectUtils.notEqual(this.fax,fax);
		this.fax = fax;
		return this;
	}

	public String getFaxComments() {
		return faxComments;
	}
	public AccountingRegistry setFaxComments(String faxComments) {
		dirty = dirty || AonObjectUtils.notEqual(this.faxComments,faxComments);
		this.faxComments = faxComments;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public AccountingRegistry setEmail(String email) {
		dirty = dirty || AonObjectUtils.notEqual(this.email,email);
		this.email = email;
		return this;
	}

	public String getWeb() {
		return web;
	}

	public AccountingRegistry setWeb(String web) {
		dirty = dirty || AonObjectUtils.notEqual(this.web,web);
		this.web = web;
		return this;
	}

	public static String getFullDescription(AccountingRegistry accRegistry) {
		return AonStringUtils.OPEN_BRACKET
				+ AonStringUtils.defaultIfEmpty(accRegistry.getAccountCode(),"NO CTA CTB")
				+ AonStringUtils.CLOSE_BRACKET
				+ AonStringUtils.SPACE
				+ AonStringUtils.defaultIfEmpty((accRegistry.getDocumentType() != null
						?accRegistry.getDocumentType().getDescription()
						:null)
					, AonStringUtils.repeat(AonStringUtils.QUESTION, 3))
				+ AonStringUtils.HYPHEN
				+ AonStringUtils.defaultIfEmpty((accRegistry.getDocumentCountry()!=null
						?accRegistry.getDocumentCountry().getIso2()
						:null)
					, AonStringUtils.repeat(AonStringUtils.QUESTION, 2)) 
				+ AonStringUtils.SLASH
				+ AonStringUtils.defaultIfEmpty(accRegistry.getDocument()
					, AonStringUtils.repeat(AonStringUtils.QUESTION, 9))
				+ AonStringUtils.SPACE
				+ AonStringUtils.HYPHEN
				+ AonStringUtils.SPACE
				+ accRegistry.getName()
				+ AonStringUtils.SPACE
				+ (AonStringUtils.isNotBlank(accRegistry.getAlias())
					?(AonStringUtils.SPACE + AonStringUtils.OPEN_PARENTHESIS + accRegistry.getAlias() + AonStringUtils.CLOSE_PARENTHESIS)
					:AonStringUtils.EMPTY)
				;
	}
	
	public boolean isDirty() {
		return dirty;
	}

	public AccountingRegistry cleanDirty() {
		dirty = false;
		return this;
	}

}
