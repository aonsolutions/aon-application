package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.CustomerStatus;

@SuppressWarnings("serial")
public class Customer  extends Registry implements Serializable{
	Integer id; // registryId
	Integer account;
	Date creationDate;
	String creationUser;
	Byte deliveryGrouped;
	Byte deliveryValuated;
	Integer domain;
	Byte eInvoice;
	Integer invoicingGroup;
	Date modificationDate;
	String modificationUser;
	Byte projectGrouped;
	Registry registry;
	Integer scope;
	CustomerStatus status;
	Byte surcharge;
	Integer tariff;
	Byte transaction;
	Byte withholding;
	
	public Customer() {
		surcharge = (byte) 0;
		withholding = (byte) 0;
		transaction = (byte) 0;
		eInvoice = (byte) 0;
		projectGrouped = (byte) 1;
		deliveryGrouped = (byte) 1;
		deliveryValuated = (byte) 1;
	}
	
	public Integer getId() {
		return id;
	}
	public Customer setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getAccount() {
		return account;
	}
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
	public Byte getDeliveryGrouped() {
		return deliveryGrouped;
	}
	public Customer setDeliveryGrouped(Byte deliveryGrouped) {
		this.deliveryGrouped = deliveryGrouped;
		return this;
	}
	public Byte getDeliveryValuated() {
		return deliveryValuated;
	}
	public Customer setDeliveryValuated(Byte deliveryValuated) {
		this.deliveryValuated = deliveryValuated;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Customer setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Byte geteInvoice() {
		return eInvoice;
	}
	public Customer seteInvoice(Byte eInvoice) {
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
	public Byte getProjectGrouped() {
		return projectGrouped;
	}
	public Customer setProjectGrouped(Byte projectGrouped) {
		this.projectGrouped = projectGrouped;
		return this;
	}
	public Registry getRegistry() {
		return registry;
	}
	public Customer setRegistry(Registry registry) {
		this.registry = registry;
		return this;
	}
	public Integer getScope() {
		return scope;
	}
	public Customer setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	public CustomerStatus getStatus() {
		return status;
	}
	public Customer setStatus(CustomerStatus status) {
		this.status = status;
		return this;
	}
	public Byte getSurcharge() {
		return surcharge;
	}
	public Customer setSurcharge(Byte surcharge) {
		this.surcharge = surcharge;
		return this;
	}
	public Integer getTariff() {
		return tariff;
	}
	public Customer setTariff(Integer tariff) {
		this.tariff = tariff;
		return this;
	}
	public Byte getTransaction() {
		return transaction;
	}
	public Customer setTransaction(Byte transaction) {
		this.transaction = transaction;
		return this;
	}
	public Byte getWithholding() {
		return withholding;
	}
	public Customer setWithholding(Byte withholding) {
		this.withholding = withholding;
		return this;
	}
	
}
