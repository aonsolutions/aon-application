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
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDomain() {
		return domain;
	}
	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getBrand() {
		return brand;
	}
	public void setBrand(Integer brand) {
		this.brand = brand;
	}
	public Integer getCategory() {
		return category;
	}
	public void setCategory(Integer category) {
		this.category = category;
	}
	public Byte getInventoriable() {
		return inventoriable;
	}
	public Boolean isInventoriable(){
		return inventoriable==1;
	}
	public void setInventoriable(Byte inventoriable) {
		this.inventoriable = inventoriable;
	}
	public void setInventoriable(Boolean inventoriable){
		this.inventoriable = inventoriable ? (byte) 1 : (byte) 0;
	}
	public Byte getSerializable() {
		return serializable;
	}
	public Boolean isSerializable(){
		return serializable == 1;
	}
	public void setSerializable(Byte serializable) {
		this.serializable = serializable;
	}
	public void setSerializable(Boolean serializable){
		this.serializable = serializable ? (byte) 1 : (byte) 0;
	}
	public Byte getLotable() {
		return lotable;
	}
	public Boolean isLotable(){
		return lotable == 1;
	}
	public void setLotable(Byte lotable) {
		this.lotable = lotable;
	}
	public void setLotable(Boolean lotable){
		this.lotable = lotable ? (byte) 1 : (byte) 0;
	}
	public Byte getStatus() {
		return status;
	}
	public void setStatus(Byte status) {
		this.status = status;
	}
	public Integer getVat() {
		return vat;
	}
	public void setVat(Integer vat) {
		this.vat = vat;
	}
	public Integer getRetention() {
		return retention;
	}
	public void setRetention(Integer retention) {
		this.retention = retention;
	}
	public Byte getType() {
		return type;
	}
	public void setType(Byte type) {
		this.type = type;
	}
	public Byte getManufactured() {
		return manufactured;
	}
	public void setManufactured(Byte manufactured) {
		this.manufactured = manufactured;
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
	public void setComposition(Byte composition) {
		this.composition = composition;
	}
	public void setComposition(Boolean composition){
		this.composition = composition ? (byte) 1 : (byte) 0;
	}
	public Byte getCompositionPrice() {
		return compositionPrice;
	}
	public Boolean isCompositionPrice(){
		return compositionPrice == 1;
	}
	public void setCompositionPrice(Byte compositionPrice) {
		this.compositionPrice = compositionPrice;
	}
	public void setCompositionPrice(Boolean compositionPrice){
		this.compositionPrice = compositionPrice ? (byte) 1 : (byte) 0;
	}
	public Integer getSalesAccount() {
		return salesAccount;
	}
	public void setSalesAccount(Integer salesAccount) {
		this.salesAccount = salesAccount;
	}
	public Integer getPurchaseAccount() {
		return purchaseAccount;
	}
	public void setPurchaseAccount(Integer purchaseAccount) {
		this.purchaseAccount = purchaseAccount;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public void setCreationUser(String creationUser) {
		this.creationUser = creationUser;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public void setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Byte getKind() {
		return kind;
	}
	public void setKind(Byte kind) {
		this.kind = kind;
	}
	
	
}
