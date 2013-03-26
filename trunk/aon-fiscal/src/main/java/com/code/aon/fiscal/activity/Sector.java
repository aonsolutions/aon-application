package com.code.aon.fiscal.activity;

public class Sector {

	private Integer id;
	private Integer period;
	private Integer admon;
	private String sector;
	private String description;
	private boolean modAgr;
	private boolean modIva;
	private boolean irpf;
	private boolean iva;
	private boolean irpfAgr;
	private boolean ivaAgr;
	private boolean ivaOp;
	private double cuotamin;
	private double maxPerson;
	private double maxImport;
	private double vatPercent;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}
	public Integer getAdmon() {
		return admon;
	}
	public void setAdmon(Integer admon) {
		this.admon = admon;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isModAgr() {
		return modAgr;
	}
	public void setModAgr(boolean modAgr) {
		this.modAgr = modAgr;
	}
	public boolean isIrpf() {
		return irpf;
	}
	public void setIrpf(boolean irpf) {
		this.irpf = irpf;
	}
	public boolean isModIva() {
		return modIva;
	}
	public void setModIva(boolean modIva) {
		this.modIva = modIva;
	}
	public boolean isIva() {
		return iva;
	}
	public void setIva(boolean iva) {
		this.iva = iva;
	}
	public boolean isIvaAgr() {
		return ivaAgr;
	}
	public void setIvaAgr(boolean ivaAgr) {
		this.ivaAgr = ivaAgr;
	}
	public boolean isIrpfAgr() {
		return irpfAgr;
	}
	public void setIrpfAgr(boolean irpfAgr) {
		this.irpfAgr = irpfAgr;
	}
	public boolean isIvaOp() {
		return ivaOp;
	}
	public void setIvaOp(boolean ivaOp) {
		this.ivaOp = ivaOp;
	}
	public double getCuotamin() {
		return cuotamin;
	}
	public void setCuotamin(double cuotamin) {
		this.cuotamin = cuotamin;
	}
	public double getMaxPerson() {
		return maxPerson;
	}
	public void setMaxPerson(double maxPerson) {
		this.maxPerson = maxPerson;
	}
	
	public double getMaxImport() {
		return maxImport;
	}
	public void setMaxImport(double maxImport) {
		this.maxImport = maxImport;
	}
	public double getVatPercent() {
		return vatPercent;
	}
	public void setVatPercent(double vatPercent) {
		this.vatPercent = vatPercent;
	}
}
