package com.code.aon.ui.loader.pojo;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.enumeration.Country;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.registry.enumeration.DocumentType;

public class LoadedInvoice implements ILoadedPojo, ILoadedDocumentHolder{

	private Integer id;
	private String serie;
	private Integer numero;
	private String referencia;
	private Integer idTitular;
	private String cuenta;
	private String documento;
	private Integer tipoDocumento;
	private String paisDocumento;
	private String razonSocial;
	private Date fechaFactura;
	private Date fechaIva;
	private Integer tipo;
	private Integer inversion;
	private Integer transaccion;
	private Integer criterioCaja;
	private String comentario;
	private Double baseImponible;
	private Double totalCuotaIVA;
	private Double totalCuotaIRPF;
	private Double totalFactura;

	private String tipoVia;
	private String direccion;
	private String numeroDir;
	private String direccion2;
	private String cp;
	private String ciudad;
	private String provincia;
	private String nombreProvincia;

	private boolean fromLoadedInvoiceAccount = false;
	
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
	public InvoiceType getInvoiceType() {
		return InvoiceType.values()[getTipo()]; 
	}
	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}
	public Integer getInversion() {
		return inversion;
	}
	public boolean isInvestment() {
		return inversion != null && inversion==1;
	}
	public void setInversion(Integer inversion) {
		this.inversion = inversion;
	}
	public Integer getTransaccion() {
		return transaccion;
	}
	public InvoiceTransactionType getInvoiceTransactionType() {
		return getTransaccion() != null
				?InvoiceTransactionType.values()[getTransaccion()]
				:InvoiceTransactionType.NATIONAL; 
	}
	public void setTransaccion(Integer transaccion) {
		this.transaccion = transaccion;
	}
	public Integer getCriterioCaja() {
		return criterioCaja;
	}
	public boolean isVatAccrualPayment() {
		return (getCriterioCaja()==null?false:(getCriterioCaja() == 1));
	}
	public void setCriterioCaja(Integer criterioCaja) {
		this.criterioCaja = criterioCaja;
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
	
	public String getTipoVia() {
		return tipoVia;
	}

	public void setTipoVia(String tipoVia) {
		this.tipoVia = tipoVia;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getNumeroDir() {
		return numeroDir;
	}

	public void setNumeroDir(String numeroDir) {
		this.numeroDir = numeroDir;
	}

	public String getDireccion2() {
		return direccion2;
	}

	public void setDireccion2(String direccion2) {
		this.direccion2 = direccion2;
	}

	public String getCp() {
		return cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getNombreProvincia() {
		return nombreProvincia;
	}

	public void setNombreProvincia(String nombreProvincia) {
		this.nombreProvincia = nombreProvincia;
	}

	public boolean isFromLoadedInvoiceAccount() {
		return fromLoadedInvoiceAccount;
	}
	public void setFromLoadedInvoiceAccount(boolean fromLoadedInvoiceAccount) {
		this.fromLoadedInvoiceAccount = fromLoadedInvoiceAccount;
	}

	public LoadedCustomer getLoadedCustomer() {
		LoadedCustomer loadedCustomer = new LoadedCustomer();
		fillLoadedRegistry(loadedCustomer);
		loadedCustomer.setCuenta(getCuenta());
		if (getTransaccion() != null) {
			loadedCustomer.setTransaccion(getTransaccion());
		}
		return loadedCustomer;
	}
	public LoadedSupplier getLoadedSupplier() {
		LoadedSupplier loadedSupplier = new LoadedSupplier();
		fillLoadedRegistry(loadedSupplier);
		loadedSupplier.setCuenta(getCuenta());
		if (getTransaccion() != null) {
			loadedSupplier.setTransaccion(getTransaccion());
		}
		if (getCriterioCaja() != null) {
			loadedSupplier.setCriterioCaja(getCriterioCaja());
		}
		return loadedSupplier;
	}
	public LoadedCreditor getLoadedCreditor() {
		LoadedCreditor loadedCreditor = new LoadedCreditor();
		fillLoadedRegistry(loadedCreditor);
		loadedCreditor.setCuenta(getCuenta());
		if (getTransaccion() != null) {
			loadedCreditor.setTransaccion(getTransaccion());
		}
		if (getCriterioCaja() != null) {
			loadedCreditor.setCriterioCaja(getCriterioCaja());
		}
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
			accountEntry.setTipoAsiento(AccountEntryType.SALES_INVOICE.ordinal());	
		} else if (type == InvoiceType.PURCHASE) {
			accountEntry.setTipoAsiento(AccountEntryType.PURCHASE_INVOICE.ordinal());
		} else if (type == InvoiceType.EXPENSES) {
			accountEntry.setTipoAsiento(AccountEntryType.EXPENSE_INVOICE.ordinal());
		} else if (type == InvoiceType.UNDEDUCTIBLE) {
			accountEntry.setTipoAsiento(AccountEntryType.EXPENSE_INVOICE.ordinal());
		} else {
			accountEntry.setTipoAsiento(AccountEntryType.MANUAL.ordinal());
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
