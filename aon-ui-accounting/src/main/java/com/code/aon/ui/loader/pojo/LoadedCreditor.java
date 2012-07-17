package com.code.aon.ui.loader.pojo;

import com.code.aon.config.enumeration.InvoiceTransactionType;



public class LoadedCreditor extends LoadedRegistry {

	public String cuenta;
	public Integer transaccion;
	public Integer retencion;
	
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
	public InvoiceTransactionType getInvoiceTransactionType() {
		return InvoiceTransactionType.values()[getTransaccion()]; 
	}
	public boolean isWithholding() {
		return (getRetencion() == 1);
	}
	
}
