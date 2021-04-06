package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.ProductType;

public class Product implements Serializable{

	private static final long serialVersionUID = -4677724896967244753L;

	Integer id;
	Domain domain;
	String name;
	String code;
	Brand brand;
	ProductCategory category;
	ProductStatus status;
	ProductType type;
	ProductKind kind;
	Tax vat;
	Tax retention;
	Boolean inventoriable;
	Boolean serializable;
	Boolean lotable;
	Boolean manufactured;
	Boolean composition;
	Boolean compositionPrice;
	Boolean packaged;
	Account salesAccount;
	Account purchaseAccount;

	String creationUser;
	Date creationDate;
	String modificationUser;
	Date modificationDate;
	
	public Integer getId() {
		return id;
	}
	
	public Product setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Domain getDomain() {
		return domain;
	}
	
	public Product setDomain(Domain domain) {
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
	
	public Brand getBrand() {
		return brand;
	}
	
	public Product setBrand(Brand brand) {
		this.brand = brand;
		return this;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public Product setCategory(ProductCategory category) {
		this.category = category;
		return this;
	}
	
	public Boolean isActive() {
		return ProductStatus.ACTIVE.equals(getStatus());
	}

	public ProductStatus getStatus() {
		return status;
	}

	public Product setStatus(ProductStatus status) {
		this.status = status;
		return this;
	}

	public ProductType getType() {
		return type;
	}

	public Product setType(ProductType type) {
		this.type = type;
		return this;
	}

	public ProductKind getKind() {
		return kind;
	}

	public Product setKind(ProductKind kind) {
		this.kind = kind;
		return this;
	}

	public Tax getVat() {
		return vat;
	}

	public Product setVat(Tax vat) {
		this.vat = vat;
		return this;
	}

	public Tax getRetention() {
		return retention;
	}

	public Product setRetention(Tax retention) {
		this.retention = retention;
		return this;
	}

	public Boolean isInventoriable() {
		return inventoriable;
	}
	
	public Boolean getInventoriable() {
		return inventoriable;
	}

	public Product setInventoriable(Boolean inventoriable) {
		this.inventoriable = inventoriable;
		return this;
	}

	public Boolean isSerializable() {
		return inventoriable;
	}
	
	public Boolean getSerializable() {
		return serializable;
	}

	public Product setSerializable(Boolean serializable) {
		this.serializable = serializable;
		return this;
	}

	public Boolean isLotable() {
		return lotable;
	}
	
	public Boolean getLotable() {
		return lotable;
	}

	public Product setLotable(Boolean lotable) {
		this.lotable = lotable;
		return this;
	}

	public Boolean isManufactured() {
		return manufactured;
	}
	
	public Boolean getManufactured() {
		return manufactured;
	}

	public Product setManufactured(Boolean manufactured) {
		this.manufactured = manufactured;
		return this;
	}

	public Boolean isComposition() {
		return composition;
	}
	
	public Boolean getComposition() {
		return composition;
	}

	public Product setComposition(Boolean composition) {
		this.composition = composition;
		return this;
	}

	public Boolean isCompositionPrice() {
		return compositionPrice;
	}
	
	public Boolean getCompositionPrice() {
		return compositionPrice;
	}

	public Product setCompositionPrice(Boolean compositionPrice) {
		this.compositionPrice = compositionPrice;
		return this;
	}

	public Boolean isPackaged() {
		return packaged;
	}
	
	public Boolean getPackaged() {
		return packaged;
	}

	public Product setPackaged(Boolean packaged) {
		this.packaged = packaged;
		return this;
	}

	public Account getSalesAccount() {
		return salesAccount;
	}

	public Product setSalesAccount(Account salesAccount) {
		this.salesAccount = salesAccount;
		return this;
	}

	public Account getPurchaseAccount() {
		return purchaseAccount;
	}

	public Product setPurchaseAccount(Account purchaseAccount) {
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
}
