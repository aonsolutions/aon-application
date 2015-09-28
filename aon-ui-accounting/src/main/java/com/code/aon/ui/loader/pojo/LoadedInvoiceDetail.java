package com.code.aon.ui.loader.pojo;

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;

public class LoadedInvoiceDetail implements ILoadedPojo{
	
	private Integer factura;
	private Integer linea;
	private String articulo;
	private String concepto;
	private Double cantidad;
	private Double precio;
	private String descuentos;
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
	
	@Override
	public String getIdentifier() {
		return (getFactura() + "-" + getLinea());
	}
	
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
	public String getDescuentos() {
		return descuentos;
	}
	public void setDescuentos(String descuentos) {
		this.descuentos = descuentos;
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
		return getTipoDeduccionIva()!=null
				?VatDeductionType.values()[getTipoDeduccionIva()]
				:VatDeductionType.WITH_RIGHT;
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

	public LoadedAccountEntryDetail getLoadedAccountEntryDetail(AccountEntryType type) {
		LoadedAccountEntryDetail laed = new LoadedAccountEntryDetail();
		laed.setCuenta(getCuenta());
		laed.setDescripcionCuenta(getConcepto());
		laed.setConcepto(getConcepto());
		if (type == AccountEntryType.SALES_INVOICE) {
			laed.setHaber(getBaseImponible());
		} else {
			laed.setDebe(getBaseImponible());
		}
		return laed;
	}

	public LoadedAccountEntryDetail getVATLoadedAccountEntryDetail(AccountEntryType type) {
		LoadedAccountEntryDetail laed = new LoadedAccountEntryDetail();
		laed.setConcepto(getConcepto());
		if (StringUtils.isNotEmpty( getCuentaIva()) ){
			laed.setCuenta( getCuentaIva() );
			if (type == AccountEntryType.SALES_INVOICE) {
				laed.setDescripcionCuenta( "Hacienda Pública, IVA repercutido." );
			} else {
				laed.setDescripcionCuenta( "Hacienda Pública, IVA soportado." );
			}
		} 
		double cuota = CommonUtil.round(
				(getCuotaIva()==null?0.0:getCuotaIva())
				+(getCuotaRe()==null?0.0:getCuotaRe())
				,2);
		if (type == AccountEntryType.SALES_INVOICE) {
			laed.setHaber(cuota);
		} else {
			laed.setDebe(cuota);
		}
		return laed;
	}
	
	public LoadedAccountEntryDetail getRetentionLoadedAccountEntryDetail(AccountEntryType type) {
		LoadedAccountEntryDetail laed = new LoadedAccountEntryDetail();
		laed.setConcepto(getConcepto());
		if (StringUtils.isNotEmpty( getCuentaIrpf()) ){
			laed.setCuenta( getCuentaIrpf() );
			laed.setDescripcionCuenta( "Hacienda Pública, retenciones y pagos a cuenta." );
		} 
		if (type == AccountEntryType.SALES_INVOICE) {
			laed.setDebe(getCuotaIrpf());
		} else {
			laed.setHaber(getCuotaIrpf());
		}
		return laed;
	}
	
}
