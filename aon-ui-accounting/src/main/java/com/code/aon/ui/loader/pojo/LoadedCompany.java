package com.code.aon.ui.loader.pojo;

import java.util.Date;

public class LoadedCompany extends LoadedRegistry{

	public Integer codAdmon;
	public String nombreContacto;
	public String telefonoContacto;
	public Integer regimenFiscal;
	public Integer devMensual;
	public Integer recc;
	public Integer re;
	public Integer retencion;	
	public String tipoPres111;
	public String tipoPres115;
	public String tipoPres123;
	public String tipoPres130;
	public String tipoPres131;
	public String tipoPres180;
	public String tipoPres190;
	public String tipoPres303RG;
	public String tipoPres303RS;
	public String tipoPres349;
	public String tipoPres347;
	public String tipoPres390;
			
	public String codigoMunicipio;
	
	public Double porcentajeIva;
	public String cuentaVentas;
	public String cuentaCompras;
	public Date fechaLimite;
	
	public String ejercicioActual;
	public Date fechaIniActual;
	public Date fechaFinActual;
	public String ejercicioAnterior;
	public Date fechaIniAnterior;
	public Date fechaFinAnterior;
	
	public Integer getRegimenFiscal() {
		return regimenFiscal;
	}
	public String getRegimenFiscalStr() {
		return (regimenFiscal==null?"3":regimenFiscal.toString());  // Por defecto valor 3 (sociedad mercantil)
	}
	public void setRegimenFiscal(Integer regimenFiscal) {
		this.regimenFiscal = regimenFiscal;
	}
	public Integer getDevMensual() {
		return devMensual;
	}
	public String getDevMensualStr() {
		return (devMensual==null?"0":devMensual.toString());
	}
	public void setDevMensual(Integer devMensual) {
		this.devMensual = devMensual;
	}
	public String getTelefonoContacto() {
		return telefonoContacto;
	}
	public void setTelefonoContacto(String telefonoContacto) {
		this.telefonoContacto = telefonoContacto;
	}
	public String getNombreContacto() {
		return nombreContacto;
	}
	public void setNombreContacto(String nombreContacto) {
		this.nombreContacto = nombreContacto;
	}
	public Integer getCodAdmon() {
		return codAdmon;
	}
	public String getCodAdmonStr() {
		return (codAdmon==null?"":codAdmon.toString());
	}
	public void setCodAdmon(Integer codAdmon) {
		this.codAdmon = codAdmon;
	}
	public String getTipoPres111() {
		return tipoPres111;
	}
	public void setTipoPres111(String tipoPres111) {
		this.tipoPres111 = tipoPres111;
	}
	public String getTipoPres115() {
		return tipoPres115;
	}
	public void setTipoPres115(String tipoPres115) {
		this.tipoPres115 = tipoPres115;
	}
	public String getTipoPres123() {
		return tipoPres123;
	}
	public void setTipoPres123(String tipoPres123) {
		this.tipoPres123 = tipoPres123;
	}
	public String getTipoPres130() {
		return tipoPres130;
	}
	public void setTipoPres130(String tipoPres130) {
		this.tipoPres130 = tipoPres130;
	}
	public String getTipoPres131() {
		return tipoPres131;
	}
	public void setTipoPres131(String tipoPres131) {
		this.tipoPres131 = tipoPres131;
	}
	public String getTipoPres180() {
		return tipoPres180;
	}
	public void setTipoPres180(String tipoPres180) {
		this.tipoPres180 = tipoPres180;
	}
	public String getTipoPres190() {
		return tipoPres190;
	}
	public void setTipoPres190(String tipoPres190) {
		this.tipoPres190 = tipoPres190;
	}
	public String getTipoPres303RG() {
		return tipoPres303RG;
	}
	public void setTipoPres303RG(String tipoPres303RG) {
		this.tipoPres303RG = tipoPres303RG;
	}
	public String getTipoPres303RS() {
		return tipoPres303RS;
	}
	public void setTipoPres303RS(String tipoPres303RS) {
		this.tipoPres303RS = tipoPres303RS;
	}
	public String getTipoPres349() {
		return tipoPres349;
	}
	public void setTipoPres349(String tipoPres349) {
		this.tipoPres349 = tipoPres349;
	}
	public String getTipoPres347() {
		return tipoPres347;
	}
	public void setTipoPres347(String tipoPres347) {
		this.tipoPres347 = tipoPres347;
	}
	public String getTipoPres390() {
		return tipoPres390;
	}
	public void setTipoPres390(String tipoPres390) {
		this.tipoPres390 = tipoPres390;
	}
	public Integer getRecc() {
		return recc;
	}
	public void setRecc(Integer recc) {
		this.recc = recc;
	}
	public Integer getRe() {
		return re;
	}
	public void setRe(Integer re) {
		this.re = re;
	}
	public Integer getRetencion() {
		return retencion;
	}
	public void setRetencion(Integer retencion) {
		this.retencion = retencion;
	}
	public boolean isSurcharge() {
		return (getRe()==null?false:(getRe() == 1));
	}
	public boolean isWithholding() {
		return (getRetencion()==null?false:(getRetencion() == 1));
	}
	public boolean isVatAccrualPayment() {
		return (getRecc()==null?false:(getRecc() == 1));
	}
	public Double getPorcentajeIva() {
		return porcentajeIva;
	}	
	public void setPorcentajeIva(Double porcentajeIva) {
		this.porcentajeIva = porcentajeIva;
	}
	public String getCuentaVentas() {
		return cuentaVentas;
	}
	public void setCuentaVentas(String cuentaVentas) {
		this.cuentaVentas = cuentaVentas;
	}
	public String getCuentaCompras() {
		return cuentaCompras;
	}
	public void setCuentaCompras(String cuentaCompras) {
		this.cuentaCompras = cuentaCompras;
	}
	public Date getFechaLimite() {
		return fechaLimite;
	}	
	public void setFechaLimite(Date fechaLimite) {
		this.fechaLimite = fechaLimite;
	}
	public String getEjercicioActual() {
		return ejercicioActual;
	}
	public void setEjercicioActual(String ejercicioActual) {
		this.ejercicioActual = ejercicioActual;
	}
	public Date getFechaIniActual() {
		return fechaIniActual;
	}
	public void setFechaIniActual(Date fechaIniActual) {
		this.fechaIniActual = fechaIniActual;
	}
	public Date getFechaFinActual() {
		return fechaFinActual;
	}
	public void setFechaFinActual(Date fechaFinActual) {
		this.fechaFinActual = fechaFinActual;
	}
	public String getEjercicioAnterior() {
		return ejercicioAnterior;
	}
	public void setEjercicioAnterior(String ejercicioAnterior) {
		this.ejercicioAnterior = ejercicioAnterior;
	}
	public Date getFechaIniAnterior() {
		return fechaIniAnterior;
	}
	public void setFechaIniAnterior(Date fechaIniAnterior) {
		this.fechaIniAnterior = fechaIniAnterior;
	}
	public Date getFechaFinAnterior() {
		return fechaFinAnterior;
	}
	public void setFechaFinAnterior(Date fechaFinAnterior) {
		this.fechaFinAnterior = fechaFinAnterior;
	}
	public String getCodigoMunicipio() {
		return codigoMunicipio;
	}
	public void setCodigoMunicipio(String codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
	}
	
}
