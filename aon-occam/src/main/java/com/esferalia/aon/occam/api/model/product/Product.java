package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable{

	private static final long serialVersionUID = -4677724896967244753L;

	Integer id;
	Integer domain;
	String name;
	String code;
	Integer brand;
	Integer category;
	Byte inventoriable;
	Byte serializable = 0;
	Byte lotable = 0;
	Byte status = 0;
	Integer vat;
	Integer retention;
	Byte type;
	Byte manufactured = 0;
	Byte composition = 0;
	Byte compositionPrice = 0;
	Boolean packaged;
	Integer salesAccount;
	Integer purchaseAccount;
	String creationUser;
	Date creationDate;
	String modificationUser;
	Date modificationDate;
	Byte kind;
	
	public Integer getId() {
		return id;
	}
	public Product setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Product setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public String getName() {
		return name;
	}
	public Product setName(String name) {
		this.name = name;
		return this;
	}
	public String getCode() {
		return code;
	}
	public Product setCode(String code) {
		this.code = code;
		return this;
	}
	public Integer getBrand() {
		return brand;
	}
	public Product setBrand(Integer brand) {
		this.brand = brand;
		return this;
	}
	public Integer getCategory() {
		return category;
	}
	public Product setCategory(Integer category) {
		this.category = category;
		return this;
	}
	public Byte getInventoriable() {
		return inventoriable;
	}
	public Boolean isInventoriable(){
		return inventoriable==1;
	}
	public Product setInventoriable(Byte inventoriable) {
		this.inventoriable = inventoriable;
		return this;
	}
	public Product setInventoriable(Boolean inventoriable){
		this.inventoriable = inventoriable ? (byte) 1 : (byte) 0;
		return this;
	}
	public Byte getSerializable() {
		return serializable;
	}
	public Boolean isSerializable(){
		return serializable == 1;
	}
	public Product setSerializable(Byte serializable) {
		this.serializable = serializable;
		return this;
	}
	public Product setSerializable(Boolean serializable){
		this.serializable = serializable ? (byte) 1 : (byte) 0;
		return this;
	}
	public Byte getLotable() {
		return lotable;
	}
	public Boolean isLotable(){
		return lotable == 1;
	}
	public Product setLotable(Byte lotable) {
		this.lotable = lotable;
		return this;
	}
	public Product setLotable(Boolean lotable){
		this.lotable = lotable ? (byte) 1 : (byte) 0;
		return this;
	}
	public Byte getStatus() {
		return status;
	}
	public Product setStatus(Byte status) {
		this.status = status;
		return this;
	}
	public Integer getVat() {
		return vat;
	}
	public Product setVat(Integer vat) {
		this.vat = vat;
		return this;
	}
	public Integer getRetention() {
		return retention;
	}
	public Product setRetention(Integer retention) {
		this.retention = retention;
		return this;
	}
	public Byte getType() {
		return type;
	}
	public Product setType(Byte type) {
		this.type = type;
		return this;
	}
	public Byte getManufactured() {
		return manufactured;
	}
	public Product setManufactured(Byte manufactured) {
		this.manufactured = manufactured;
		return this;
	}
	public Boolean isManufactured(){
		return manufactured == 1;
	}
	public Byte getComposition() {
		return composition;
	}
	public Boolean isComposition(){
		return composition == 1;
	}
	public Product setComposition(Byte composition) {
		this.composition = composition;
		return this;
	}
	public Product setComposition(Boolean composition){
		this.composition = composition ? (byte) 1 : (byte) 0;
		return this;
	}
	public Byte getCompositionPrice() {
		return compositionPrice;
	}
	public Boolean isCompositionPrice(){
		return compositionPrice == 1;
	}
	public Product setCompositionPrice(Byte compositionPrice) {
		this.compositionPrice = compositionPrice;
		return this;
	}
	public Product setCompositionPrice(Boolean compositionPrice){
		this.compositionPrice = compositionPrice ? (byte) 1 : (byte) 0;
		return this;
	}
	public Integer getSalesAccount() {
		return salesAccount;
	}
	public Product setSalesAccount(Integer salesAccount) {
		this.salesAccount = salesAccount;
		return this;
	}
	public Integer getPurchaseAccount() {
		return purchaseAccount;
	}
	public Product setPurchaseAccount(Integer purchaseAccount) {
		this.purchaseAccount = purchaseAccount;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public Product setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public Product setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public Product setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public Product setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Byte getKind() {
		return kind;
	}
	public Product setKind(Byte kind) {
		this.kind = kind;
		return this;
	}
	
	public Boolean getPackaged() {
		return packaged;
	}

	public Product setPackaged(Boolean packaged) {
		this.packaged = packaged;
		return this;
	}
	
	
}
