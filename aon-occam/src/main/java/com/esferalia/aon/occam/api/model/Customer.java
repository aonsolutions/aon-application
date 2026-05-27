package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.registry.CustomerFiscalStatus;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;

public class Customer extends Registry implements Serializable, HasAudit, IScopable<Customer>, IAccountId<Customer> {

	private static final long serialVersionUID = -1763053163676341615L;
	
	private Integer tariff;
	private boolean surcharge;
	private boolean withholding;
	private InvoiceTransactionType transaction;
	private RegistryStatus status;
	private CustomerFiscalStatus fiscalStatus;
	private Scope scope;
	private boolean eInvoice;
	private Integer invoicingGroup;
	private boolean projectGrouped;
	private boolean deliveryGrouped;
	private boolean deliveryValuated;
	private boolean isRelationship;
	private Integer account;
	
	private boolean isBillable;
	
	private Date creationDate;
	private String creationUser;
	private Date modificationDate;
	private String modificationUser;

	
	public Customer() {
		transaction = InvoiceTransactionType.NATIONAL;
		projectGrouped = true;
		deliveryGrouped = true;
		deliveryValuated = true;
		isRelationship = false;
		isBillable = true;
	}
	
	public Customer copy(Registry registry) {
		return super.copy( registry, this);
	}

	public Customer setId(Integer id) {
		super.setId(id);
		return this;
	}

	public Integer getTariff() {
		return tariff;
	}
	public Customer setTariff(Integer tariff) {
		this.tariff = tariff;
		return this;
	}

	public boolean isSurcharge() {
		return surcharge;
	}
	public Customer setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
		return this;
	}

	public boolean isWithholding() {
		return withholding;
	}
	public Customer setWithholding(boolean withholding) {
		this.withholding = withholding;
		return this;
	}

	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	public Customer setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
		return this;
	}
	
	public RegistryStatus getStatus() {
		return status;
	}
	public Customer setStatus(RegistryStatus status) {
		this.status = status;
		return this;
	}
	
	public CustomerFiscalStatus getFiscalStatus() {
		return fiscalStatus;
	}
	
	public boolean isRegistered() {
		return fiscalStatus != null && fiscalStatus != CustomerFiscalStatus.REGISTERED;
	}
	
	public boolean isNotRegistered() {
		return fiscalStatus != null && fiscalStatus != CustomerFiscalStatus.NOT_REGISTERED;
	}
	
	public boolean isNotIdentified() {
		return fiscalStatus != null && fiscalStatus != CustomerFiscalStatus.NOT_IDENTIFIED;
	}
	
	public Customer setFiscalStatus(CustomerFiscalStatus fiscalStatus) {
		this.fiscalStatus = fiscalStatus;
		return this;
	}
	
	@Override 
	public Scope getScope() {
		if(scope == null) {
			scope = new Scope();
		}
		return scope;
	}
	@Override 
	public Customer setScope(Scope scope) {
		this.scope = scope;
		return this;
	}

	public boolean isEInvoice() {
		return eInvoice;
	}
	public Customer setEInvoice(boolean eInvoice) {
		this.eInvoice = eInvoice;
		return this;
	}

	public Integer getInvoicingGroup() {
		return invoicingGroup;
	}
	public Customer setInvoicingGroup(Integer invoicingGroup) {
		this.invoicingGroup = invoicingGroup;
		return this;
	}
	
	public boolean isProjectGrouped() {
		return projectGrouped;
	}
	public Customer setProjectGrouped(boolean projectGrouped) {
		this.projectGrouped = projectGrouped;
		return this;
	}
	
	public boolean isDeliveryGrouped() {
		return deliveryGrouped;
	}
	public Customer setDeliveryGrouped(boolean deliveryGrouped) {
		this.deliveryGrouped = deliveryGrouped;
		return this;
	}
	
	public boolean isDeliveryValuated() {
		return deliveryValuated;
	}
	
	public Customer setRelationship(boolean isRelationship) {
		this.isRelationship = isRelationship;
		return this;
	}
	
	public boolean isRelationship() {
		return isRelationship;
	}
	
	public Customer setBillable(boolean isBillable) {
		this.isBillable = isBillable;
		return this;
	}
	
	public boolean isBillable() {
		return isBillable;
	}
	
	public Customer setDeliveryValuated(boolean deliveryValuated) {
		this.deliveryValuated = deliveryValuated;
		return this;
	}
	@Override
	public Integer getAccount() {
		return account;
	}
	@Override
	public Customer setAccount(Integer account) {
		this.account = account;
		return this;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	public Customer setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	public String getCreationUser() {
		return creationUser;
	}
	public Customer setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}
	public Customer setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public String getModificationUser() {
		return modificationUser;
	}
	public Customer setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
}
