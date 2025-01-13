package net.aonsolutions.occam.api.model;

import java.sql.Timestamp;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.metadata.ProductMetadata;
import net.aonsolutions.occam.api.model.type.ProductKind;
import net.aonsolutions.occam.api.model.type.ProductStatus;
import net.aonsolutions.occam.api.model.type.ProductType;

public class Product extends AonEntity<ProductMetadata> implements HasAudit {

	private static final long serialVersionUID = -4677724896967244753L;

	private Integer id;
	private Integer domain;
	private String name;
	private String code;
	private Brand brand;
	private ProductCategory category;
	private ProductStatus status;
	private ProductType type;
	private ProductKind kind;
	private Tax vat;
	private Tax retention;
	private boolean inventoriable;
	private boolean serializable;
	private boolean lotable;
	private boolean manufactured;
	private boolean composition;
	private boolean compositionPrice;
	private boolean packaged;
	private boolean perishable;
	private Integer daysToExpire;
	private Account salesAccount;
	private Account purchaseAccount;

	private String creationUser;
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public Product markAsClean() {
		super.markAsClean();
		return this; 
	}
	
	@Override
	public Product setSelected( boolean selected) {
		super.setSelected(selected);
		return this; 
	}
	
	@Override
	public Product setDeleted( boolean selected) {
		super.setDeleted(selected);
		return this; 
	}

	public Integer getId() {
		return id;
	}
	public Product setId(Integer id) {
		checkIfDirty( this.id,id, ProductMetadata.ID);
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Product setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, ProductMetadata.DOMAIN);
		this.domain = domain;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public Product setName(String name) {
		checkIfDirty( this.name,name, ProductMetadata.NAME);
		this.name = name;
		return this;
	}
	
	public String getCode() {
		return code;
	}
	public Product setCode(String code) {
		checkIfDirty( this.code,code, ProductMetadata.CODE);
		this.code = code;
		return this;
	}
	
	public Optional<Brand> getBrand() {
		return Optional.ofNullable(brand);
	}
	public Product setBrand(Brand brand) {
		checkIfDirty( this.brand,brand, ProductMetadata.BRAND);
		this.brand = brand;
		return this;
	}

	public Optional<ProductCategory> getCategory() {
		return Optional.ofNullable(category);
	}
	public Product setCategory(ProductCategory category) {
		checkIfDirty( this.category,category, ProductMetadata.CATEGORY);
		this.category = category;
		return this;
	}
	
	public boolean isActive() {
		return getStatus() == ProductStatus.ACTIVE;
	}
	public Product setActive(boolean active) {
		setStatus(active ? ProductStatus.ACTIVE : ProductStatus.DISCONTINUED);
		return this;
	}
	public ProductStatus getStatus() {
		return status;
	}
	public Product setStatus(ProductStatus status) {
		checkIfDirty( this.status,status, ProductMetadata.STATUS);
		this.status = status;
		return this;
	}

	public ProductType getType() {
		return type;
	}
	public Product setType(ProductType type) {
		checkIfDirty( this.type,type, ProductMetadata.TYPE);
		this.type = type;
		return this;
	}

	public ProductKind getKind() {
		return kind;
	}
	public Product setKind(ProductKind kind) {
		checkIfDirty( this.kind,kind, ProductMetadata.KIND);
		this.kind = kind;
		return this;
	}
	
	public Optional<Tax> getVat() {
		return Optional.ofNullable(vat);
	}
	public Product setVat(Tax vat) {
		checkIfDirty( this.vat,vat, ProductMetadata.VAT);
		this.vat = vat;
		return this;
	}

	public Optional<Tax> getRetention() {
		return Optional.ofNullable(retention);
	}
	public Product setRetention(Tax retention) {
		checkIfDirty( this.retention,retention, ProductMetadata.RETENTION);
		this.retention = retention;
		return this;
	}

	public boolean isInventoriable() {
		return inventoriable;
	}
	public Product setInventoriable(boolean inventoriable) {
		checkIfDirty( this.inventoriable,inventoriable, ProductMetadata.INVENTORIABLE);
		this.inventoriable = inventoriable;
		return this;
	}

	public boolean isSerializable() { 
		return serializable;
	}
	public Product setSerializable(boolean serializable) {
		checkIfDirty( this.serializable,serializable, ProductMetadata.SERIALIZABLE);
		this.serializable = serializable;
		return this;
	}

	public boolean isLotable() {
		return lotable;
	}
	public Product setLotable(boolean lotable) {
		checkIfDirty( this.lotable,lotable, ProductMetadata.LOTABLE);
		this.lotable = lotable;
		return this;
	}

	public boolean isManufactured() {
		return manufactured;
	}
	public Product setManufactured(boolean manufactured) {
		checkIfDirty( this.manufactured,manufactured, ProductMetadata.MANUFACTURED);
		this.manufactured = manufactured;
		return this;
	}

	public boolean isComposition() {
		return this.composition;
	}
	public Product setComposition(boolean composition) {
		checkIfDirty( this.composition ,composition , ProductMetadata.COMPOSITION );
		this.composition = composition;
		return this;
	}


	public boolean isCompositionPrice() {
		return this.compositionPrice;
	}
	public Product setCompositionPrice(boolean compositionPrice) {
		checkIfDirty( this.compositionPrice ,compositionPrice , ProductMetadata.COMPOSITION_PRICE );
		this.compositionPrice = compositionPrice;
		return this;
	}

	public boolean isPackaged() {
		return this.packaged;
	}
	public Product setPackaged(boolean packaged) {
		checkIfDirty( this.packaged,packaged, ProductMetadata.PACKAGED);
		this.packaged = packaged;
		return this;
	}

	public boolean isPerishable() {
		return perishable;
	}
	public Product setPerishable(boolean perishable) {
		checkIfDirty( this.perishable,perishable, ProductMetadata.PERISHABLE);
		this.perishable = perishable;
		return this;
	}
	
	public Integer getDaysToExpire() {
		return daysToExpire;
	}
	public Product setDaysToExpire(Integer daysToExpire) {
		checkIfDirty( this.daysToExpire,daysToExpire, ProductMetadata.DAYS_TO_EXPIRE);
		this.daysToExpire = daysToExpire;
		return this;
	}
	
	public Optional<Account> getSalesAccount() {
		return Optional.ofNullable(salesAccount);
	}
	public Product setSalesAccount(Account salesAccount) {
		checkIfDirty( this.salesAccount,salesAccount, ProductMetadata.SALES_ACCOUNT);
		this.salesAccount = salesAccount;
		return this;
	}

	public Optional<Account> getPurchaseAccount() {
		return Optional.ofNullable(purchaseAccount);
	}
	public Product setPurchaseAccount(Account purchaseAccount) {
		checkIfDirty( this.purchaseAccount,purchaseAccount, ProductMetadata.PURCHASE_ACCOUNT);
		this.purchaseAccount = purchaseAccount;
		return this;
	}

	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Product setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	@Override
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public Product setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Product setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	@Override
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public Product setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Product) {
			return AonObjectUtils.equals( this.getUuid(),((Product) obj).getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + AonObjectUtils.requireNonNullElse(getUuid(), 0).hashCode();
	}
}
