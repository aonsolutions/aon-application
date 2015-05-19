package com.code.aon.ui.loader.pojo;

import java.util.Date;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.enumeration.AccountEntryType;

public class LoadedAccountEntryDetail implements ILoadedPojo{

	private Integer id;
	private Integer asiento;
	private Date fechaAsiento;
	private Integer tipoAsiento;
	private String cuenta;
	private String descripcionCuenta;
	private String concepto;
	private String documento;
	private Double debe;
	private Double haber;
	private String contrapartida;
	private String descripcionContrapartida;

	private AccountEntry entry;
	
	
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
	public Integer getAsiento() {
		return asiento;
	}
	public void setAsiento(Integer asiento) {
		this.asiento = asiento;
	}
	public Date getFechaAsiento() {
		return fechaAsiento;
	}
	public void setFechaAsiento(Date fechaAsiento) {
		this.fechaAsiento = fechaAsiento;
	}
	public Integer getTipoAsiento() {
		return tipoAsiento;
	}
	public AccountEntryType getEntryType() {
		return tipoAsiento==null?AccountEntryType.MANUAL:AccountEntryType.values()[tipoAsiento];
	}
	public void setTipoAsiento(Integer tipoAsiento) {
		this.tipoAsiento = tipoAsiento;
	}
	public String getCuenta() {
		return cuenta;
	}
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	public String getDescripcionCuenta() {
		return descripcionCuenta;
	}
	public void setDescripcionCuenta(String descripcionCuenta) {
		this.descripcionCuenta = descripcionCuenta;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public Double getDebe() {
		return debe;
	}
	public void setDebe(Double debe) {
		this.debe = debe;
	}
	public Double getHaber() {
		return haber;
	}
	public void setHaber(Double haber) {
		this.haber = haber;
	}
	public String getContrapartida() {
		return contrapartida;
	}
	public void setContrapartida(String contrapartida) {
		this.contrapartida = contrapartida;
	}
	public String getDescripcionContrapartida() {
		return descripcionContrapartida;
	}
	public void setDescripcionContrapartida(String descripcionContrapartida) {
		this.descripcionContrapartida = descripcionContrapartida;
	}
	
	public AccountEntry getEntry() {
		return entry;
	}
	public void setEntry(AccountEntry entry) {
		this.entry = entry;
	}

	public LoadedAccountEntry getLoadedAccountEntry() {
		LoadedAccountEntry loaded = new LoadedAccountEntry();
		loaded.setId(getAsiento());
		loaded.setFecha(getFechaAsiento());
		loaded.setTipoAsiento(getTipoAsiento());
		return loaded;
	}

	public boolean hasSaldo() {
		if (getDebe() != null && getDebe() != 0) return true; 
		if (getHaber() != null && getHaber() != 0) return true; 
		return false;
	}

}
