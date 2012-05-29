package com.code.aon.ui.accounting.invoice;

import java.util.Date;

import com.code.aon.common.enumeration.Country;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.registry.enumeration.DocumentType;

public class LoadedInvoice {

	public Integer id;
	public String serie;
	public Integer numero;
	public String referencia;
	public String cuenta;
	public String documento;
	public Integer tipoDocumento;
	public String paisDocumento;
	public String razonSocial;
	public Date fechaFactura;
	public Date fechaIva;
	public Integer tipo;
	public Integer inversion;
	public Integer transaccion;
	public String comentario;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getCuenta() {
		return cuenta;
	}
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public Integer getTipoDocumento() {
		return tipoDocumento;
	}
	public DocumentType getDocumentType() {
		return DocumentType.values()[getTipoDocumento()];
	}
	public void setTipoDocumento(Integer tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public String getPaisDocumento() {
		return paisDocumento;
	}
	public Country getDocumentCountry() {
		return Country.valueOf( getPaisDocumento());
	}
	public void setPaisDocumento(String paisDocumento) {
		this.paisDocumento = paisDocumento;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public Date getFechaFactura() {
		return fechaFactura;
	}
	public void setFechaFactura(Date fechaFactura) {
		this.fechaFactura = fechaFactura;
	}
	public Date getFechaIva() {
		return fechaIva;
	}
	public void setFechaIva(Date fechaIva) {
		this.fechaIva = fechaIva;
	}
	public Integer getTipo() {
		return tipo;
	}
	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}
	public Integer getInversion() {
		return inversion;
	}
	public boolean isInvestment() {
		return inversion==1;
	}
	public void setInversion(Integer inversion) {
		this.inversion = inversion;
	}
	public Integer getTransaccion() {
		return transaccion;
	}
	public InvoiceTransactionType getInvoiceTransactionType() {
		return InvoiceTransactionType.values()[getTransaccion()]; 
	}
	public void setTransaccion(Integer transaccion) {
		this.transaccion = transaccion;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
}
