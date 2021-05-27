package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Mod347Key;
import com.esferalia.aon.occam.api.model.type.Province;

public class Mod347Declared implements Serializable {

	private static final long serialVersionUID = -1702092858696283405L;
	
	private Integer id;	
	private int domain;
	private int mod347;
	
	private String document;
	private String representativeDocument;
	private Integer registry;  // La tabla ya contenía el campo registry, no se usa
	private String name;
	private Province province;  
	private Country country;
	private Mod347Key type; // Clave operacion
	private double amount;
	private boolean insuranceOperation;
	private boolean businessPremiseRental;
	private double cashAmount;
	private double assetAmount;
	private Integer cashYear;
	private double firstQuarterAmount;
	private double assetFirstQuarterAmount;	
	private double secondQuarterAmount;
	private double assetSecondQuarterAmount;	
	private double thirdQuarterAmount;
	private double assetThirdQuarterAmount;
	private double fourthQuarterAmount;
	private double assetFourthQuarterAmount;
	private String operatorNif;	
	private boolean vatAccrual;	
	private boolean isp;
	private boolean depositRegime;	
	private double vatAccrualAmount;

	private boolean dirty;
	private boolean deleted;
	private int tempId;	

	public Integer getId() {
		return id;
	}

	public Mod347Declared setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Mod347Declared setName(String name) {
		this.name = name;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Mod347Declared setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public int getMod347() {
		return mod347;
	}

	public Mod347Declared setMod347(int mod347) {
		this.mod347 = mod347;
		return this;
	}

	public Mod347Key getType() {
		return type;
	}

	public Mod347Declared setType(Mod347Key type) {
		this.type = type;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public Mod347Declared setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getRepresentativeDocument() {
		return representativeDocument;
	}

	public Mod347Declared setRepresentativeDocument(String representativeDocument) {
		this.representativeDocument = representativeDocument;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}

	public Mod347Declared setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public Province getProvince() {
		return province;
	}

	public Mod347Declared setProvince(Province province) {
		this.province = province;
		return this;
	}

	public Country getCountry() {
		return country;
	}

	public Mod347Declared setCountry(Country country) {
		this.country = country;
		return this;
	}

	public double getAmount() {
		return amount;
	}

	public Mod347Declared setAmount(double amount) {
		this.amount = amount;
		return this;
	}

	public double getFirstQuarterAmount() {
		return firstQuarterAmount;
	}

	public Mod347Declared setFirstQuarterAmount(double firstQuarterAmount) {
		this.firstQuarterAmount = firstQuarterAmount;
		return this;
	}

	public double getSecondQuarterAmount() {
		return secondQuarterAmount;
	}

	public Mod347Declared setSecondQuarterAmount(double secondQuarterAmount) {
		this.secondQuarterAmount = secondQuarterAmount;
		return this;
	}

	public double getThirdQuarterAmount() {
		return thirdQuarterAmount;
	}

	public Mod347Declared setThirdQuarterAmount(double thirdQuarterAmount) {
		this.thirdQuarterAmount = thirdQuarterAmount;
		return this;
	}

	public double getFourthQuarterAmount() {
		return fourthQuarterAmount;
	}

	public Mod347Declared setFourthQuarterAmount(double fourthQuarterAmount) {
		this.fourthQuarterAmount = fourthQuarterAmount;
		return this;
	}

	public double getAssetAmount() {
		return assetAmount;
	}

	public Mod347Declared setAssetAmount(double assetAmount) {
		this.assetAmount = assetAmount;
		return this;
	}

	public double getAssetFirstQuarterAmount() {
		return assetFirstQuarterAmount;
	}

	public Mod347Declared setAssetFirstQuarterAmount(double assetFirstQuarterAmount) {
		this.assetFirstQuarterAmount = assetFirstQuarterAmount;
		return this;
	}

	public double getAssetSecondQuarterAmount() {
		return assetSecondQuarterAmount;
	}

	public Mod347Declared setAssetSecondQuarterAmount(double assetSecondQuarterAmount) {
		this.assetSecondQuarterAmount = assetSecondQuarterAmount;
		return this;
	}

	public double getAssetThirdQuarterAmount() {
		return assetThirdQuarterAmount;
	}

	public Mod347Declared setAssetThirdQuarterAmount(double assetThirdQuarterAmount) {
		this.assetThirdQuarterAmount = assetThirdQuarterAmount;
		return this;
	}

	public double getAssetFourthQuarterAmount() {
		return assetFourthQuarterAmount;
	}

	public Mod347Declared setAssetFourthQuarterAmount(double assetFourthQuarterAmount) {
		this.assetFourthQuarterAmount = assetFourthQuarterAmount;
		return this;
	}

	public double getCashAmount() {
		return cashAmount;
	}

	public Mod347Declared setCashAmount(double cashAmount) {
		this.cashAmount = cashAmount;
		return this;
	}

	public Integer getCashYear() {
		return cashYear;
	}

	public Mod347Declared setCashYear(Integer cashYear) {
		this.cashYear = cashYear;
		return this;
	}

	public boolean isInsuranceOperation() {
		return insuranceOperation;
	}

	public Mod347Declared setInsuranceOperation(boolean insuranceOperation) {
		this.insuranceOperation = insuranceOperation;
		return this;
	}

	public boolean isBusinessPremiseRental() {
		return businessPremiseRental;
	}

	public Mod347Declared setBusinessPremiseRental(boolean businessPremiseRental) {
		this.businessPremiseRental = businessPremiseRental;
		return this;
	}

	public String getOperatorNif() {
		return operatorNif;
	}

	public Mod347Declared setOperatorNif(String operatorNif) {
		this.operatorNif = operatorNif;
		return this;
	}

	public boolean isVatAccrual() {
		return vatAccrual;
	}

	public Mod347Declared setVatAccrual(boolean vatAccrual) {
		this.vatAccrual = vatAccrual;
		return this;
	}

	public boolean isIsp() {
		return isp;
	}

	public Mod347Declared setIsp(boolean isp) {
		this.isp = isp;
		return this;
	}

	public boolean isDepositRegime() {
		return depositRegime;
	}

	public Mod347Declared setDepositRegime(boolean depositRegime) {
		this.depositRegime = depositRegime;
		return this;
	}

	public double getVatAccrualAmount() {
		return vatAccrualAmount;
	}

	public Mod347Declared setVatAccrualAmount(double vatAccrualAmount) {
		this.vatAccrualAmount = vatAccrualAmount;
		return this;
	}

	public boolean isDirty() {
		return dirty;
	}

	public Mod347Declared setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Mod347Declared setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	public int getTempId() {
		return tempId;
	}

	public Mod347Declared setTempId(int tempId) {
		this.tempId = tempId;
		return this;
	}
	
}
