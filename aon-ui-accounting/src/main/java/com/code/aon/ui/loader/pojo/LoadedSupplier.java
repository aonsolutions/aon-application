package com.code.aon.ui.loader.pojo;

import com.code.aon.config.enumeration.InvoiceTransactionType;



public class LoadedSupplier extends LoadedRegistry {

	public String cuenta;
	public Integer transaccion;
	public Integer retencion;
	public Integer criterioCaja;
	public Integer regimenAgrario;
	
	public String getCuenta() {
		return cuenta;
	}
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	public Integer getTransaccion() {
		return transaccion;
	}
	public void setTransaccion(Integer transaccion) {
		this.transaccion = transaccion;
	}
	public Integer getRetencion() {
		return retencion;
	}
	public void setRetencion(Integer retencion) {
		this.retencion = retencion;
	}
	public Integer getCriterioCaja() {
		return criterioCaja;
	}
	public void setCriterioCaja(Integer criterioCaja) {
		this.criterioCaja = criterioCaja;
	}
	public Integer getRegimenAgrario() {
		return regimenAgrario;
	}
	public void setRegimenAgrario(Integer regimenAgrario) {
		this.regimenAgrario = regimenAgrario;
	}
	public InvoiceTransactionType getInvoiceTransactionType() {
		if (getTransaccion() == null) {
			return InvoiceTransactionType.NATIONAL; 
		}
		return InvoiceTransactionType.values()[getTransaccion()]; 
	}
	public boolean isWithholding() {
		return (getRetencion()==null?false:(getRetencion() == 1));
	}
	public boolean isVatAccrualPayment() {
		return (getCriterioCaja()==null?false:(getCriterioCaja() == 1));
	}
	public boolean isWithholdingFarmer() {
		return (getRegimenAgrario()==null?false:(getRegimenAgrario() == 1));
	}
	
}
