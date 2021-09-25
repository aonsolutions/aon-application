package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.registry.Registry;

public class Company extends Registry implements Serializable {

	private static final long serialVersionUID = -4970548127101817530L;

    private boolean active;
	private boolean surcharge;
	private boolean withholding;
	private boolean vatAccrualPayment;
	private boolean eInvoice;
	
	public Company copy(Registry registry) {
		return super.copy( registry, this);
	}

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

}
