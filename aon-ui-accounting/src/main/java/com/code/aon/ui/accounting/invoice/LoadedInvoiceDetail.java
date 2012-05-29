package com.code.aon.ui.accounting.invoice;

import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;

public class LoadedInvoiceDetail {
	private Integer factura;
	private Integer linea;
	private String articulo;
	private String concepto;
	private Double cantidad;
	private Double precio;
	private Double baseImponible;
	private Double porcentajeIva;
	private Double cuotaIva;
	private Double re;
	private Double cuotaRe;
	private Double porcentajeIrpf;
	private Double cuotaIrpf;
	private Integer tipoDeduccionIva;
	private Integer tipoIrpf;
	private String cuenta;
	private String cuentaIva;
	private String cuentaIrpf;
	
	public Integer getFactura() {
		return factura;
	}
	public void setFactura(Integer factura) {
		this.factura = factura;
	}
	public Integer getLinea() {
		return linea;
	}
	public void setLinea(Integer linea) {
		this.linea = linea;
	}
	public String getArticulo() {
		return articulo;
	}
	public void setArticulo(String articulo) {
		this.articulo = articulo;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public Double getCantidad() {
		return cantidad;
	}
	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public Double getBaseImponible() {
		return baseImponible;
	}
	public void setBaseImponible(Double baseImponible) {
		this.baseImponible = baseImponible;
	}
	public Double getPorcentajeIva() {
		return porcentajeIva;
	}
	public void setPorcentajeIva(Double porcentajeIva) {
		this.porcentajeIva = porcentajeIva;
	}
	public Double getCuotaIva() {
		return cuotaIva;
	}
	public void setCuotaIva(Double cuotaIva) {
		this.cuotaIva = cuotaIva;
	}
	public Double getRe() {
		return re;
	}
	public void setRe(Double re) {
		this.re = re;
	}
	public Double getCuotaRe() {
		return cuotaRe;
	}
	public void setCuotaRe(Double cuotaRe) {
		this.cuotaRe = cuotaRe;
	}
	public Double getPorcentajeIrpf() {
		return porcentajeIrpf;
	}
	public void setPorcentajeIrpf(Double porcentajeIrpf) {
		this.porcentajeIrpf = porcentajeIrpf;
	}
	public Double getCuotaIrpf() {
		return cuotaIrpf;
	}
	public void setCuotaIrpf(Double cuotaIrpf) {
		this.cuotaIrpf = cuotaIrpf;
	}
	public Integer getTipoDeduccionIva() {
		return tipoDeduccionIva;
	}
	public VatDeductionType getVatDeductionType() {
		return VatDeductionType.values()[getTipoDeduccionIva()];
	}
	public void setTipoDeduccionIva(Integer tipoDeduccionIva) {
		this.tipoDeduccionIva = tipoDeduccionIva;
	}
	public Integer getTipoIrpf() {
		return tipoIrpf;
	}
	public WithholdingType getWithholdingType() {
		return WithholdingType.values()[getTipoIrpf()];
	}
	public void setTipoIrpf(Integer tipoIrpf) {
		this.tipoIrpf = tipoIrpf;
	}
	public String getCuenta() {
		return cuenta;
	}
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
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
}
