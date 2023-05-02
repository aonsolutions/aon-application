package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.ProductType;

@Deprecated
public class OldProduct implements Serializable{

	private static final long serialVersionUID = -4677724896967244753L;

	Integer id;
	Integer domain;
	String name;
	String code;
	Integer brand;
	String brandName;
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
	
	boolean modify = false;
	
	public Integer getId() {
		return id;
	}
	public OldProduct setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public OldProduct setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public String getName() {
		return name;
	}
	public OldProduct setName(String name) {
		this.name = name;
		return this;
	}
	public String getCode() {
		return code;
	}
	public OldProduct setCode(String code) {
		this.code = code;
		return this;
	}
	public Integer getBrand() {
		return brand;
	}
	public OldProduct setBrand(Integer brand) {
		this.brand = brand;
		return this;
	}
	public Integer getCategory() {
		return category;
	}
	public OldProduct setCategory(Integer category) {
		this.category = category;
		return this;
	}
	public Byte getInventoriable() {
		if(inventoriable == null) {
			inventoriable = 0;
		}
		return inventoriable;
	}
	public Boolean isInventoriable(){
		return inventoriable==1;
	}
	public OldProduct setInventoriable(Byte inventoriable) {
		this.inventoriable = inventoriable;
		return this;
	}
	public OldProduct setInventoriable(Boolean inventoriable){
		this.inventoriable = inventoriable ? (byte) 1 : (byte) 0;
		return this;
	}
	public Byte getSerializable() {
		return serializable;
	}
	public Boolean isSerializable(){
		return serializable == 1;
	}
	public OldProduct setSerializable(Byte serializable) {
		this.serializable = serializable;
		return this;
	}
	public OldProduct setSerializable(Boolean serializable){
		this.serializable = serializable ? (byte) 1 : (byte) 0;
		return this;
	}
	public Byte getLotable() {
		return lotable;
	}
	public Boolean isLotable(){
		return lotable == 1;
	}
	public OldProduct setLotable(Byte lotable) {
		this.lotable = lotable;
		return this;
	}
	public OldProduct setLotable(Boolean lotable){
		this.lotable = lotable ? (byte) 1 : (byte) 0;
		return this;
	}
	public Byte getStatus() {
		return status;
	}
	public OldProduct setStatus(Byte status) {
		this.status = status;
		return this;
	}
	public Integer getVat() {
		return vat;
	}
	public OldProduct setVat(Integer vat) {
		this.vat = vat;
		return this;
	}
	public Integer getRetention() {
		return retention;
	}
	public OldProduct setRetention(Integer retention) {
		this.retention = retention;
		return this;
	}
	public Byte getType() {
		return type;
	}
	public OldProduct setType(Byte type) {
		this.type = type;
		return this;
	}
	public Byte getManufactured() {
		return manufactured;
	}
	public OldProduct setManufactured(Byte manufactured) {
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
	public OldProduct setComposition(Byte composition) {
		this.composition = composition;
		return this;
	}
	public OldProduct setComposition(Boolean composition){
		this.composition = composition ? (byte) 1 : (byte) 0;
		return this;
	}
	public Byte getCompositionPrice() {
		return compositionPrice;
	}
	public Boolean isCompositionPrice(){
		return compositionPrice == 1;
	}
	public OldProduct setCompositionPrice(Byte compositionPrice) {
		this.compositionPrice = compositionPrice;
		return this;
	}
	public OldProduct setCompositionPrice(Boolean compositionPrice){
		this.compositionPrice = compositionPrice ? (byte) 1 : (byte) 0;
		return this;
	}
	public Integer getSalesAccount() {
		return salesAccount;
	}
	public OldProduct setSalesAccount(Integer salesAccount) {
		this.salesAccount = salesAccount;
		return this;
	}
	public Integer getPurchaseAccount() {
		return purchaseAccount;
	}
	public OldProduct setPurchaseAccount(Integer purchaseAccount) {
		this.purchaseAccount = purchaseAccount;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public OldProduct setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public OldProduct setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public OldProduct setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public OldProduct setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Byte getKind() {
		return kind;
	}
	public OldProduct setKind(Byte kind) {
		this.kind = kind;
		return this;
	}
	
	public Boolean getPackaged() {
		return packaged;
	}
	
	public Byte getPackagedValue(){
		return packaged ? (byte) 1 : 0;
	}

	public OldProduct setPackaged(Boolean packaged) {
		this.packaged = packaged;
		return this;
	}
	
	public String getBrandName(){
		return brandName;
	}
	
	public OldProduct setBrandName(String brandName){
		this.brandName = brandName;
		return this;
	}
	
	public boolean isModify() {
		return modify;
	}
	
	public OldProduct setModify(boolean modify) {
		this.modify = modify;
		return this;
	}
	
	public Product toNewProduct() {
		return new Product()
			.setId(getId())
			.setBrand(new Brand().setId(getBrand()).setName(getBrandName()))
			.setCategory(new ProductCategory().setId(getCategory()))
			.setCode(getCode())
			.setComposition(isComposition())
			.setCompositionPrice(isCompositionPrice())
			.setDomain(new Domain().setId(getDomain()))
			.setInventoriable(isInventoriable())
			.setKind(ProductKind.safeValueOf(getKind()))
			.setLotable(isLotable())
			.setManufactured(isManufactured())
			.setName(getName())
			.setPackaged(getPackaged())
			.setPurchaseAccount(new Account().setId(getPurchaseAccount()))
			.setRetention(new Tax().setId(getRetention()))
			.setSalesAccount(new Account().setId(getSalesAccount()))
			.setSerializable(isSerializable())
			.setStatus(ProductStatus.safeValueOf(getStatus()))
			.setType(ProductType.safeValueOf(getType()))
			.setVat(new Tax().setId(getVat()))
			.setCreationDate(getCreationDate())
			.setCreationUser(getCreationUser())
			.setModificationDate(getModificationDate())
			.setModificationUser(getModificationUser());
	}
	
}
