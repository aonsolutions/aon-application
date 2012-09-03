package com.code.aon.ui.loader.pojo;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.enumeration.Country;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.registry.enumeration.DocumentType;

public class LoadedInvoice implements ILoadedPojo{

	public Integer id;
	public String serie;
	public Integer numero;
	public String referencia;
	public Integer idTitular;
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
	public Double baseImponible;
	public Double totalCuotaIVA;
	public Double totalCuotaIRPF;
	public Double totalFactura;
	
	
	@Override
	public String getIdentifier() {
		return id==null?null:id.toString();
	}

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
	public Integer getIdTitular() {
		return idTitular;
	}
	public void setIdTitular(Integer idTitular) {
		this.idTitular = idTitular;
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

	public Double getBaseImponible() {
		return baseImponible;
	}

	public void setBaseImponible(Double baseImponible) {
		this.baseImponible = baseImponible;
	}

	public Double getTotalCuotaIVA() {
		return totalCuotaIVA;
	}

	public void setTotalCuotaIVA(Double totalCuotaIVA) {
		this.totalCuotaIVA = totalCuotaIVA;
	}

	public Double getTotalCuotaIRPF() {
		return totalCuotaIRPF;
	}

	public void setTotalCuotaIRPF(Double totalCuotaIRPF) {
		this.totalCuotaIRPF = totalCuotaIRPF;
	}

	public Double getTotalFactura() {
		return totalFactura;
	}

	public void setTotalFactura(Double totalFactura) {
		this.totalFactura = totalFactura;
	}
	
	public LoadedCustomer getLoadedCustomer() {
		LoadedCustomer loadedCustomer = new LoadedCustomer();
		fillLoadedRegistry(loadedCustomer);
		loadedCustomer.setCuenta(getCuenta());
		return loadedCustomer;
	}
	public LoadedSupplier getLoadedSupplier() {
		LoadedSupplier loadedSupplier = new LoadedSupplier();
		fillLoadedRegistry(loadedSupplier);
		loadedSupplier.setCuenta(getCuenta());
		return loadedSupplier;
	}
	public LoadedCreditor getLoadedCreditor() {
		LoadedCreditor loadedCreditor = new LoadedCreditor();
		fillLoadedRegistry(loadedCreditor);
		loadedCreditor.setCuenta(getCuenta());
		return loadedCreditor;
	}
	private void fillLoadedRegistry(LoadedRegistry loadedRegistry) {
		loadedRegistry.setDocumento(getDocumento());
		loadedRegistry.setPaisDocumento(getPaisDocumento());
		loadedRegistry.setTipoDocumento(getTipoDocumento());
		loadedRegistry.setRazonSocial(getRazonSocial());
		loadedRegistry.setId(getIdTitular());
	}
	
	public LoadedAccountEntry getLoadedAccountEntry() {
		LoadedAccountEntry accountEntry = new LoadedAccountEntry();
		accountEntry.setFecha(getFechaFactura());
		InvoiceType type = InvoiceType.values()[getTipo()];
		if (type == InvoiceType.SALES) {
			accountEntry.setEntryType(AccountEntryType.SALES_INVOICE);	
		} else if (type == InvoiceType.PURCHASE) {
			accountEntry.setEntryType(AccountEntryType.PURCHASE_INVOICE);
		} else if (type == InvoiceType.EXPENSES) {
			accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);
		} else if (type == InvoiceType.UNDEDUCTIBLE) {
			accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);
		} else {
			accountEntry.setEntryType(AccountEntryType.MANUAL);
		}
		return accountEntry;  
	}

	// Primer Apunte (Cliente, Proveedor o Acreedor)
	public LoadedAccountEntryDetail getLoadedAccountEntryDetail() {
		LoadedAccountEntryDetail detail =  new LoadedAccountEntryDetail(); 
		detail.setCuenta(getCuenta());
		detail.setDescripcionCuenta(getRazonSocial());
		detail.setDocumento(getReferencia());
		InvoiceType type = InvoiceType.values()[getTipo()];
		String prefix = (type == InvoiceType.SALES) ? "N/Fra" : "S/Fra";
		if (getTotalFactura() < 0) {
			prefix += " " + "ABONO";
		}
		prefix += ": ";
		detail.setConcepto( StringUtils.abbreviate(prefix + getReferencia(), 32) );
		if (type == InvoiceType.SALES) {
			detail.setDebe(getTotalFactura());
		} else {
			detail.setHaber(getTotalFactura());
		}
		return detail;
	}
	
	
	
}
