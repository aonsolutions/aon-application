package net.aonsolutions.occam.api.model;

import java.io.Serializable;

import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;

public class Company extends Registry implements Serializable {

	private static final long serialVersionUID = -4970548127101817530L;

    private boolean active;
	private boolean surcharge;
	private boolean withholding;
	private boolean vatAccrualPayment;
	private boolean eInvoice;
	

	public boolean isSurcharge() {
		return surcharge;
	}
	public Company setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
		return this;
	}

	public boolean isWithholding() {
		return withholding;
	}
	public Company setWithholding(boolean withholding) {
		this.withholding = withholding;
		return this;
	}

	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public Company setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}
	
	public boolean isActive() {
		return active;
	}
	public Company setActive(boolean active) {
		this.active = active;
		return this;
	}

	public boolean iseInvoice() {
		return eInvoice;
	}
	public Company seteInvoice(boolean eInvoice) {
		this.eInvoice = eInvoice;
		return this;
	}

	
	@Override
	public Company setId(Integer id) {
		super.setId(id);
		return this;
	}
	@Override
	public Company setDomain(Integer domain) {
		super.setDomain(domain);
		return this;
	}
	@Override
	public Company setDocument(String document) {
		super.setDocument(document);
		return this;
	}
	@Override
	public Company setDocumentType(DocumentType documentType) {
		super.setDocumentType(documentType);
		return this;
	}
	@Override
	public Company setDocumentCountry(Country documentCountry) {
		super.setDocumentCountry(documentCountry);
		return this;
	}
	@Override
	public Company setName(String name) {
		super.setName(name);
		return this;
	}
	@Override
	public Company setAlias(String alias) {
		super.setAlias(alias);
		return this;
	}
	@Override
	public Company setLegalPerson(boolean legalPerson) {
		super.setLegalPerson(legalPerson);
		return this;
	}
	@Override
	public Company setNationality(Country nationality) {
		super.setNationality(nationality);
		return this;
	}
	@Override
	public Company setConfidential(boolean confidential) {
		super.setConfidential(confidential);
		return this;
	}
	
	
}
