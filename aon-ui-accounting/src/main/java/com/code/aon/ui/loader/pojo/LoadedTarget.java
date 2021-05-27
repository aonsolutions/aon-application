package com.code.aon.ui.loader.pojo;

import com.code.aon.config.enumeration.InvoiceTransactionType;


public class LoadedTarget extends LoadedRegistry{

	public Integer re;
	public Integer transaccion;
	public Integer retencion;
	public String segmento;
	
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
	public InvoiceTransactionType getInvoiceTransactionType() {
		if (getTransaccion() == null) {
			return InvoiceTransactionType.NATIONAL; 
		}
		return InvoiceTransactionType.values()[getTransaccion()]; 
	}
	public boolean isSurcharge() {
		return (getRe()==null?false:(getRe() == 1));
	}
	public boolean isWithholding() {
		return (getRetencion()==null?false:(getRetencion() == 1));
	}
	public String getSegmento() {
		return segmento;
	}
	public void setSegmento(String segmento) {
		this.segmento = segmento;
	}
	
}
