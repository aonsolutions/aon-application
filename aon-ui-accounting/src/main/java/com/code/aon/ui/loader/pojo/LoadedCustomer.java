package com.code.aon.ui.loader.pojo;

import com.code.aon.config.enumeration.InvoiceTransactionType;


public class LoadedCustomer extends LoadedRegistry{

	public String cuenta;
	
	public Integer re;
	public Integer transaccion;
	public Integer retencion;
	public Integer facturarAlbaranesAgrupados;
	
	public String getCuenta() {
		return cuenta;
	}
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	public Integer getRe() {
		return re;
	}
	public void setRe(Integer re) {
		this.re = re;
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
	public Integer getFacturarAlbaranesAgrupados() {
		return facturarAlbaranesAgrupados;
	}
	public void setFacturarAlbaranesAgrupados(Integer facturarAlbaranesAgrupados) {
		this.facturarAlbaranesAgrupados = facturarAlbaranesAgrupados;
	}
	
	public InvoiceTransactionType getInvoiceTransactionType() {
		return InvoiceTransactionType.values()[getTransaccion()]; 
	}
	public boolean isDeliveryGrouped() {
		return (getFacturarAlbaranesAgrupados() == 1);
	}
	public boolean isSurcharge() {
		return (getRe() == 1);
	}
	public boolean isWithholding() {
		return (getRetencion() == 1);
	}
	
}
