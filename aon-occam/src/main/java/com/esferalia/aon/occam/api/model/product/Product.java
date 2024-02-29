package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.TaxType;

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
	boolean perishable;
	Integer daysToExpire;
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
		if(domain == null) 
			domain = new Domain();
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
		if(brand == null) 
			brand = new Brand();
		return brand;
	}
	
	public Product setBrand(Brand brand) {
		this.brand = brand;
		return this;
	}

	public ProductCategory getCategory() {
		if(category == null) 
			category = new ProductCategory();
		return category;
	}

	public Product setCategory(ProductCategory category) {
		this.category = category;
		return this;
	}
	
	public boolean isActive() {
		return ProductStatus.ACTIVE.equals(getStatus());
	}
	
	public Product setActive(boolean active) {
		setStatus(active ? ProductStatus.ACTIVE : ProductStatus.DISCONTINUED);
		return this;
	}

	public ProductStatus getStatus() {
		if(status == null) 
			status = ProductStatus.ACTIVE;
		return status;
	}

	public Product setStatus(ProductStatus status) {
		this.status = status;
		return this;
	}
	
	public boolean isAuxiliary() {
		return getType().isAuxiliary();
	}

	public ProductType getType() {
		if(type == null) 
			type = ProductType.COMMERCIAL_PRODUCT;
		return type;
	}

	public Product setType(ProductType type) {
		this.type = type;
		return this;
	}

	public ProductKind getKind() {
		if(kind == null) 
			kind = ProductKind.SALE_PURCHASE;
		return kind;
	}

	public Product setKind(ProductKind kind) {
		this.kind = kind;
		return this;
	}

	public Tax getVat() {
		if(vat == null) 
			vat = new Tax().setType(TaxType.VAT);
		return vat;
	}

	public Product setVat(Tax vat) {
		this.vat = vat;
		return this;
	}

	public Tax getRetention() {
		if(retention == null)
			retention = new Tax().setType(TaxType.RETENTION);
		return retention;
	}

	public Product setRetention(Tax retention) {
		this.retention = retention;
		return this;
	}

	public Boolean isInventoriable() {
		return getInventoriable();
	}
	
	public Boolean getInventoriable() {
		if(inventoriable == null) 
			inventoriable = false; 
		return inventoriable;
	}

	public Product setInventoriable(Boolean inventoriable) {
		this.inventoriable = inventoriable;
		return this;
	}

	public Boolean isSerializable() { 
		return getSerializable();
	}
	
	public Boolean getSerializable() {
		if(serializable == null) 
			serializable = false; 
		return serializable;
	}

	public Product setSerializable(Boolean serializable) {
		this.serializable = serializable;
		return this;
	}

	public Boolean isLotable() {
		return getLotable();
	}
	
	public Boolean getLotable() {
		if(lotable == null) 
			lotable = false;
		return lotable;
	}

	public Product setLotable(Boolean lotable) {
		this.lotable = lotable;
		return this;
	}

	public Boolean isManufactured() {
		return getManufactured();
	}
	
	public Boolean getManufactured() {
		if(manufactured == null) 
			manufactured = false;
		return manufactured;
	}

	public Product setManufactured(Boolean manufactured) {
		this.manufactured = manufactured;
		return this;
	}

	public Boolean isComposition() {
		return getComposition();
	}
	
	public Boolean getComposition() {
		if(composition == null) 
			composition = false;
		return composition;
	}

	public Product setComposition(Boolean composition) {
		this.composition = composition;
		return this;
	}


	public Boolean isCompositionPrice() {
		return getCompositionPrice();
	}
	
	public Boolean getCompositionPrice() {
		if(compositionPrice == null) 
			compositionPrice = false;
		return compositionPrice;
	}

	public Product setCompositionPrice(Boolean compositionPrice) {
		this.compositionPrice = compositionPrice;
		return this;
	}

	public Boolean isPackaged() {
		return getPackaged();
	}
	
	public Boolean getPackaged() {
		if(packaged == null)
			packaged = false;
		return packaged;
	}

	public Product setPackaged(Boolean packaged) {
		this.packaged = packaged;
		return this;
	}

	public boolean isPerishable() {
		return perishable;
	}
	
	public Product setPerishable(boolean perishable) {
		this.perishable = perishable;
		return this;
	}
	
	public Integer getDaysToExpire() {
		return daysToExpire;
	}
	
	public Product setDaysToExpire(Integer daysToExpire) {
		this.daysToExpire = daysToExpire;
		return this;
	}
	
	public Account getSalesAccount() {
		if(salesAccount == null)
			salesAccount = new Account();
		return salesAccount;
	}

	public Product setSalesAccount(Account salesAccount) {
		this.salesAccount = salesAccount;
		return this;
	}

	public Account getPurchaseAccount() {
		if(purchaseAccount == null)
			purchaseAccount = new Account();
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
