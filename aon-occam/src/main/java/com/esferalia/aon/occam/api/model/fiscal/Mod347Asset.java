package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class Mod347Asset implements Serializable {

	private static final long serialVersionUID = 8440410953071171471L;

	private Integer id;	
	private int domain;
	private int mod347;
	
	private String document;
	private String representativeDocument;
	private Integer registry;  // FALTA - La tabla ya contenía el campo registry, pero se usará ??
	private String name;
	private double amount;
	private String assetLocation;  // FALTA - Así o enumerado con situación del inmueble ??
	private String cadasdralReference;
	private String assetStreetType;
	private String assetStreet;
	private String assetStreetNumberType;
	private String assetStreetNumber;
	private String assetStreetNumberSuffix;
	private String assetStreetBlock;
	private String assetStreetHall;
	private String assetStreetStair;
	private String assetStreetFloor;
	private String assetStreetDoor;
	private String assetStreetComplement;
	private String assetStreetCity;
	private String assetStreetTown;
	private String assetStreetTownCode;
	private String assetStreetProvince;
	private String assetStreetZip;

	private boolean dirty;
	private boolean deleted;
	private int tempId;	

	public Integer getId() {
		return id;
	}

	public Mod347Asset setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Mod347Asset setName(String name) {
		this.name = name;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Mod347Asset setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public int getMod347() {
		return mod347;
	}

	public Mod347Asset setMod347(int mod347) {
		this.mod347 = mod347;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public Mod347Asset setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getRepresentativeDocument() {
		return representativeDocument;
	}

	public Mod347Asset setRepresentativeDocument(String representativeDocument) {
		this.representativeDocument = representativeDocument;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}

	public Mod347Asset setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public double getAmount() {
		return amount;
	}

	public Mod347Asset setAmount(double amount) {
		this.amount = amount;
		return this;
	}

	public String getAssetLocation() {
		return assetLocation;
	}

	public Mod347Asset setAssetLocation(String assetLocation) {
		this.assetLocation = assetLocation;
		return this;
	}

	public String getCadasdralReference() {
		return cadasdralReference;
	}

	public Mod347Asset setCadasdralReference(String cadasdralReference) {
		this.cadasdralReference = cadasdralReference;
		return this;
	}

	public String getAssetStreetType() {
		return assetStreetType;
	}

	public Mod347Asset setAssetStreetType(String assetStreetType) {
		this.assetStreetType = assetStreetType;
		return this;
	}

	public String getAssetStreet() {
		return assetStreet;
	}

	public Mod347Asset setAssetStreet(String assetStreet) {
		this.assetStreet = assetStreet;
		return this;
	}

	public String getAssetStreetNumberType() {
		return assetStreetNumberType;
	}

	public Mod347Asset setAssetStreetNumberType(String assetStreetNumberType) {
		this.assetStreetNumberType = assetStreetNumberType;
		return this;
	}

	public String getAssetStreetNumber() {
		return assetStreetNumber;
	}

	public Mod347Asset setAssetStreetNumber(String assetStreetNumber) {
		this.assetStreetNumber = assetStreetNumber;
		return this;
	}

	public String getAssetStreetNumberSuffix() {
		return assetStreetNumberSuffix;
	}

	public Mod347Asset setAssetStreetNumberSuffix(String assetStreetNumberSuffix) {
		this.assetStreetNumberSuffix = assetStreetNumberSuffix;
		return this;
	}

	public String getAssetStreetBlock() {
		return assetStreetBlock;
	}

	public Mod347Asset setAssetStreetBlock(String assetStreetBlock) {
		this.assetStreetBlock = assetStreetBlock;
		return this;
	}

	public String getAssetStreetHall() {
		return assetStreetHall;
	}

	public Mod347Asset setAssetStreetHall(String assetStreetHall) {
		this.assetStreetHall = assetStreetHall;
		return this;
	}

	public String getAssetStreetStair() {
		return assetStreetStair;
	}

	public Mod347Asset setAssetStreetStair(String assetStreetStair) {
		this.assetStreetStair = assetStreetStair;
		return this;
	}

	public String getAssetStreetFloor() {
		return assetStreetFloor;
	}

	public Mod347Asset setAssetStreetFloor(String assetStreetFloor) {
		this.assetStreetFloor = assetStreetFloor;
		return this;
	}

	public String getAssetStreetDoor() {
		return assetStreetDoor;
	}

	public Mod347Asset setAssetStreetDoor(String assetStreetDoor) {
		this.assetStreetDoor = assetStreetDoor;
		return this;
	}

	public String getAssetStreetComplement() {
		return assetStreetComplement;
	}

	public Mod347Asset setAssetStreetComplement(String assetStreetComplement) {
		this.assetStreetComplement = assetStreetComplement;
		return this;
	}

	public String getAssetStreetCity() {
		return assetStreetCity;
	}

	public Mod347Asset setAssetStreetCity(String assetStreetCity) {
		this.assetStreetCity = assetStreetCity;
		return this;
	}

	public String getAssetStreetTown() {
		return assetStreetTown;
	}

	public Mod347Asset setAssetStreetTown(String assetStreetTown) {
		this.assetStreetTown = assetStreetTown;
		return this;
	}

	public String getAssetStreetTownCode() {
		return assetStreetTownCode;
	}

	public Mod347Asset setAssetStreetTownCode(String assetStreetTownCode) {
		this.assetStreetTownCode = assetStreetTownCode;
		return this;
	}

	public String getAssetStreetProvince() {
		return assetStreetProvince;		
	}

	public Mod347Asset setAssetStreetProvince(String assetStreetProvince) {
		this.assetStreetProvince = assetStreetProvince;
		return this;
	}

	public String getAssetStreetZip() {
		return assetStreetZip;
	}

	public Mod347Asset setAssetStreetZip(String assetStreetZip) {
		this.assetStreetZip = assetStreetZip;
		return this;
	}

	public boolean isDirty() {
		return dirty;
	}

	public Mod347Asset setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Mod347Asset setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	public int getTempId() {
		return tempId;
	}

	public Mod347Asset setTempId(int tempId) {
		this.tempId = tempId;
		return this;
	}
	
}
