package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Advertising;
import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.TargetStatus;

public class Target extends Registry implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer registry;
	private Tariff tariff;
	private Advertising advertising;
	private Boolean surcharge;
	private Boolean withholding;
	private InvoiceTransactionType transaction;
	private TargetStatus status;
	private Scope scope;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;

	
	public Target() {
		setAdvertising(Advertising.ALLOWED);
		setSurcharge(false);
		setWithholding(false);
		setTransaction(InvoiceTransactionType.NATIONAL);
		setStatus(TargetStatus.ACTIVE);
	}
	
	public Target copy(Registry registry) {
		return super.copy( registry, this);
	}

	public Integer getRegistry() {
		return registry;
	}
	
	public Target setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	
	public Target setId(Integer id) {
		super.setId(id);
		return this;
	}

	public Tariff getTariff() {
		if(tariff == null) 
			tariff = new Tariff();
		return tariff;
	}
	
	public Target setTariff(Tariff tariff) {
		this.tariff = tariff;
		return this;
	}
	
	public Advertising getAdvertising() {
		return advertising;
	}
	
	public Target setAdvertising(Advertising advertising) {
		this.advertising = advertising;
		return this;
	}
	
	public Boolean isSurcharge() {
		return surcharge;
	}
	
	public Boolean getSurcharge() {
		return surcharge;
	}
	
	public Target setSurcharge(Boolean surcharge) {
		this.surcharge = surcharge;
		return this;
	}
	
	public Boolean isWithholding() {
		return withholding;
	}
	
	public Boolean getWithholding() {
		return withholding;
	}
	
	public Target setWithholding(Boolean withholding) {
		this.withholding = withholding;
		return this;
	}
	
	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	
	public Target setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
		return this;
	}
	
	public TargetStatus getStatus() {
		return status;
	}
	
	public Target setStatus(TargetStatus status) {
		this.status = status;
		return this;
	}
	
	public Scope getScope() {
		return scope;
	}
	
	public Target setScope(Scope scope) {
		this.scope = scope;
		return this;
	}
	
	public String getCreationUser() {
		return creationUser;
	}
	
	public Target setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public Target setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	public String getModificationUser() {
		return modificationUser;
	}
	
	public Target setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
	public Date getModificationDate() {
		return modificationDate;
	}
	
	public Target setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
}
