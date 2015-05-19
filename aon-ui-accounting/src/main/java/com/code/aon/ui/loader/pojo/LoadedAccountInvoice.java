package com.code.aon.ui.loader.pojo;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.registry.enumeration.DocumentType;

/**
 * @author ecastellano
 *
 */
public class LoadedAccountInvoice implements ILoadedPojo, ILoadedDocumentHolder{

	private Integer id;
	private String serie;
	private Integer numero;
	private String referencia;
	private String cuenta;
	private Integer idTitular;
	private String documento;
	private Integer tipoDocumento;
	private String paisDocumento;
	private String razonSocial;
	private Date fechaFactura;
	private Date fechaIva;
	private Integer tipo;
	private Integer inversion;
	private Integer transaccion;
	private String comentario;
	private Double baseImponible1;
	private Double iva1;
	private Integer tipoDeduccionIva1;
	private Double re1;
	private Double cuotaIVA1;
	private Double cuotaRE1;
	private Double baseImponible2;
	private Double iva2;
	private Integer tipoDeduccionIva2;
	private Double re2;
	private Double cuotaIVA2;
	private Double cuotaRE2;
	private Double baseImponible3;
	private Double iva3;
	private Integer tipoDeduccionIva3;
	private Double re3;
	private Double cuotaIVA3;
	private Double cuotaRE3;
	private Double irpf;
	private Integer tipoIrpf;
	private Double cuotaIRPF;
	private Double totalFactura;
//	private String articulo;
	private String concepto;
	private String cuentaExplotacion;
	private String cuentaIva;
	private String cuentaIrpf;
	private Date fechaVto;
	private String formaPago;
	private String cuentaBanco;
		
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

	public String getCuenta() {
		return cuenta;
	}
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public Integer getIdTitular() {
		return idTitular;
	}
	public void setIdTitular(Integer idTitular) {
		this.idTitular = idTitular;
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
	public void setTipoDocumento(Integer tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getPaisDocumento() {
		return paisDocumento;
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
	public void setInversion(Integer inversion) {
		this.inversion = inversion;
	}

	public Integer getTransaccion() {
		return transaccion;
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

	public Double getBaseImponible1() {
		return baseImponible1;
	}
	public void setBaseImponible1(Double baseImponible1) {
		this.baseImponible1 = baseImponible1;
	}

	public Double getIva1() {
		return iva1;
	}
	public void setIva1(Double iva1) {
		this.iva1 = iva1;
	}

	public Integer getTipoDeduccionIva1() {
		return tipoDeduccionIva1;
	}
	public void setTipoDeduccionIva1(Integer tipoDeduccionIva1) {
		this.tipoDeduccionIva1 = tipoDeduccionIva1;
	}

	public Double getRe1() {
		return re1;
	}
	public void setRe1(Double re1) {
		this.re1 = re1;
	}

	public Double getCuotaIVA1() {
		return cuotaIVA1;
	}
	public void setCuotaIVA1(Double cuotaIVA1) {
		this.cuotaIVA1 = cuotaIVA1;
	}

	public Double getCuotaRE1() {
		return cuotaRE1;
	}
	public void setCuotaRE1(Double cuotaRE1) {
		this.cuotaRE1 = cuotaRE1;
	}

	public Double getBaseImponible2() {
		return baseImponible2;
	}
	public void setBaseImponible2(Double baseImponible2) {
		this.baseImponible2 = baseImponible2;
	}

	public Double getIva2() {
		return iva2;
	}
	public void setIva2(Double iva2) {
		this.iva2 = iva2;
	}

	public Integer getTipoDeduccionIva2() {
		return tipoDeduccionIva2;
	}
	public void setTipoDeduccionIva2(Integer tipoDeduccionIva2) {
		this.tipoDeduccionIva2 = tipoDeduccionIva2;
	}

	public Double getRe2() {
		return re2;
	}
	public void setRe2(Double re2) {
		this.re2 = re2;
	}

	public Double getCuotaIVA2() {
		return cuotaIVA2;
	}
	public void setCuotaIVA2(Double cuotaIVA2) {
		this.cuotaIVA2 = cuotaIVA2;
	}

	public Double getCuotaRE2() {
		return cuotaRE2;
	}
	public void setCuotaRE2(Double cuotaRE2) {
		this.cuotaRE2 = cuotaRE2;
	}

	public Double getBaseImponible3() {
		return baseImponible3;
	}
	public void setBaseImponible3(Double baseImponible3) {
		this.baseImponible3 = baseImponible3;
	}

	public Double getIva3() {
		return iva3;
	}
	public void setIva3(Double iva3) {
		this.iva3 = iva3;
	}

	public Integer getTipoDeduccionIva3() {
		return tipoDeduccionIva3;
	}
	public void setTipoDeduccionIva3(Integer tipoDeduccionIva3) {
		this.tipoDeduccionIva3 = tipoDeduccionIva3;
	}

	public Double getRe3() {
		return re3;
	}
	public void setRe3(Double re3) {
		this.re3 = re3;
	}

	public Double getCuotaIVA3() {
		return cuotaIVA3;
	}
	public void setCuotaIVA3(Double cuotaIVA3) {
		this.cuotaIVA3 = cuotaIVA3;
	}

	public Double getCuotaRE3() {
		return cuotaRE3;
	}
	public void setCuotaRE3(Double cuotaRE3) {
		this.cuotaRE3 = cuotaRE3;
	}

	public Double getIrpf() {
		return irpf;
	}
	public void setIrpf(Double irpf) {
		this.irpf = irpf;
	}

	public Integer getTipoIrpf() {
		return tipoIrpf;
	}
	public void setTipoIrpf(Integer tipoIrpf) {
		this.tipoIrpf = tipoIrpf;
	}

	public Double getCuotaIRPF() {
		return cuotaIRPF;
	}
	public void setCuotaIRPF(Double cuotaIRPF) {
		this.cuotaIRPF = cuotaIRPF;
	}

	public Double getTotalFactura() {
		return totalFactura;
	}
	public void setTotalFactura(Double totalFactura) {
		this.totalFactura = totalFactura;
	}

	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

//	public String getArticulo() {
//		return articulo;
//	}
//	public void setArticulo(String articulo) {
//		this.articulo = articulo;
//	}

	public String getCuentaExplotacion() {
		return cuentaExplotacion;
	}
	public void setCuentaExplotacion(String cuentaExplotacion) {
		this.cuentaExplotacion = cuentaExplotacion;
	}

	public String getCuentaIva() {
		return cuentaIva;
	}
	public void setCuentaIva(String cuentaIva) {
		this.cuentaIva = cuentaIva;
	}

	public String getCuentaIrpf() {
		return cuentaIrpf;
	}
	public void setCuentaIrpf(String cuentaIrpf) {
		this.cuentaIrpf = cuentaIrpf;
	}

	public Date getFechaVto() {
		return fechaVto;
	}
	public void setFechaVto(Date fechaVto) {
		this.fechaVto = fechaVto;
	}

	public String getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}

	public String getCuentaBanco() {
		return cuentaBanco;
	}
	public void setCuentaBanco(String cuentaBanco) {
		this.cuentaBanco = cuentaBanco;
	}

	public DocumentType getDocumentType() {
		return DocumentType.values()[getTipoDocumento()];
	}

	public Country getDocumentCountry() {
		return Country.valueOf( getPaisDocumento());
	}
	
	public boolean isInvestment() {
		return inversion != null && inversion==1;
	}
	
	public InvoiceTransactionType getInvoiceTransactionType() {
		if (getTransaccion() == null) {
			return InvoiceTransactionType.NATIONAL;
		}
		return InvoiceTransactionType.values()[getTransaccion()]; 
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

	public double getTotalBaseImponible() {
		return CommonUtil.round( 
				  (getBaseImponible1()==null?0:getBaseImponible1())
				+ (getBaseImponible2()==null?0:getBaseImponible2())
				+ (getBaseImponible3()==null?0:getBaseImponible3())
					, 3);
	}

	public double getTotalCuotaIVA() {
		return CommonUtil.round( 
				  (getCuotaIVA1()==null?0:getCuotaIVA1()) 
				+ (getCuotaRE1()==null?0:getCuotaRE1()) 
				+ (getCuotaIVA2()==null?0:getCuotaIVA2()) 
				+ (getCuotaRE2()==null?0:getCuotaRE2()) 
				+ (getCuotaIVA3()==null?0:getCuotaIVA3())
				+ (getCuotaRE3()==null?0:getCuotaRE3())
					, 3);
	}

	public LoadedInvoice getLoadedInvoice() {
		LoadedInvoice invoice = new LoadedInvoice();
		invoice.setId(getId());
		invoice.setSerie(getSerie());
		invoice.setNumero(getNumero());
		invoice.setReferencia(getReferencia());
		invoice.setIdTitular(getIdTitular());
		invoice.setCuenta(getCuenta());
		invoice.setDocumento(getDocumento());
		invoice.setTipoDocumento(getTipoDocumento());
		invoice.setPaisDocumento(getPaisDocumento());
		invoice.setRazonSocial(getRazonSocial());
		invoice.setFechaFactura(getFechaFactura());
		invoice.setFechaIva(getFechaIva());
		invoice.setTipo(getTipo());
		invoice.setInversion(getInversion());
		invoice.setTransaccion(getTransaccion());
		invoice.setComentario(getComentario());
		invoice.setBaseImponible(getTotalBaseImponible());
		invoice.setTotalCuotaIVA(getTotalCuotaIVA());
		invoice.setTotalCuotaIRPF(getCuotaIRPF());
		invoice.setTotalFactura(getTotalFactura());
		invoice.setFromLoadedInvoiceAccount(true);
//		invoice.setFromLoadedInvoiceAccountWithProduct(StringUtils.isNotBlank(getArticulo()));
		return invoice;
	}

	public List<LoadedInvoiceDetail> getLoadedInvoiceDetails() {
		List<LoadedInvoiceDetail> details = new LinkedList<LoadedInvoiceDetail>();
		LoadedInvoiceDetail detail = new LoadedInvoiceDetail();
		detail.setFactura(getId());
		detail.setLinea(1);
//		detail.setArticulo(getArticulo());
		detail.setConcepto(getConcepto());
		detail.setCantidad(1.0);
		detail.setPrecio(getBaseImponible1());
		detail.setBaseImponible(getBaseImponible1());
		detail.setPorcentajeIva(getIva1());
		detail.setCuotaIva(getCuotaIVA1());
		detail.setRe(getRe1());
		detail.setCuotaRe(getCuotaRE1());
		detail.setPorcentajeIrpf(getIrpf());
		detail.setCuotaIrpf(getCuotaIRPF());
		detail.setTipoDeduccionIva(getTipoDeduccionIva1());
		detail.setTipoIrpf(getTipoIrpf());
		detail.setCuenta(getCuentaExplotacion());
		detail.setCuentaIva(getCuentaIva());
		detail.setCuentaIrpf(getCuentaIrpf());
		details.add(detail);
		
		if (getBaseImponible2() != null) {
			detail = new LoadedInvoiceDetail();
			detail.setFactura(getId());
			detail.setLinea(2);
//			detail.setArticulo(getArticulo());
			detail.setConcepto(getConcepto());
			detail.setCantidad(1.0);
			detail.setPrecio(getBaseImponible2());
			detail.setBaseImponible(getBaseImponible2());
			detail.setPorcentajeIva(getIva2());
			detail.setCuotaIva(getCuotaIVA2());
			detail.setRe(getRe2());
			detail.setCuotaRe(getCuotaRE2());
			detail.setTipoDeduccionIva(getTipoDeduccionIva2());
			detail.setCuenta(getCuentaExplotacion());
			detail.setCuentaIva(getCuentaIva());
			detail.setCuentaIrpf(getCuentaIrpf());
			details.add(detail);
		}
		if (getBaseImponible3() != null) {
			detail = new LoadedInvoiceDetail();
			detail.setFactura(getId());
			detail.setLinea(3);
//			detail.setArticulo(getArticulo());
			detail.setConcepto(getConcepto());
			detail.setCantidad(1.0);
			detail.setPrecio(getBaseImponible3());
			detail.setBaseImponible(getBaseImponible3());
			detail.setPorcentajeIva(getIva3());
			detail.setCuotaIva(getCuotaIVA3());
			detail.setRe(getRe3());
			detail.setCuotaRe(getCuotaRE3());
			detail.setTipoDeduccionIva(getTipoDeduccionIva3());
			detail.setCuenta(getCuentaExplotacion());
			detail.setCuentaIva(getCuentaIva());
			detail.setCuentaIrpf(getCuentaIrpf());
			details.add(detail);
		}
		return details;
	}

	public LoadedFinance getLoadedFinance() {
		LoadedFinance finance = new LoadedFinance();
		finance.setId(1);
		finance.setFactura(getId());
		finance.setIdTitular(getIdTitular());
		finance.setCuenta(getCuenta());
		finance.setDocumento(getDocumento());
		finance.setTipoDocumento(getTipoDocumento());
		finance.setPaisDocumento(getPaisDocumento());
		finance.setRazonSocial(getRazonSocial());
		finance.setConcepto(getConcepto());
		finance.setFechaVto(getFechaFactura());
		finance.setPago(getInvoiceType()==InvoiceType.SALES?0:1);
		finance.setFormaPago(getFormaPago());
		finance.setCuentaBanco(getCuentaBanco());
		finance.setImporte(getTotalFactura());
		return finance;
	}
	
}
