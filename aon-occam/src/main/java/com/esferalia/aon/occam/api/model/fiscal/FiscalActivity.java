
package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.ArrayList;

public class FiscalActivity implements Serializable {

	private static final long serialVersionUID = -8409745811046443519L;
	
	private Integer id;
	private Integer domain;
	private Integer year;
	private String epigraph;
	private String description;
	private boolean farmer;
	private double maxPerson;
	private double maxImport;
	private double vatPercent;
	
	private ArrayList<FiscalActivityInfo> info;
	private ArrayList<FiscalActivityModule> moduleIRPF;
	private ArrayList<FiscalActivityInfo> infoIRPF;
	private ArrayList<FiscalActivityModule> moduleIVA;
	private ArrayList<FiscalActivityInfo> infoIVA;
	
	public Integer getId() {
		return id;
	}
	public FiscalActivity setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public FiscalActivity setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getYear() {
		return year;
	}
	public FiscalActivity setYear(Integer year) {
		this.year = year;
		return this;
	}
	
	public String getEpigraph() {
		return epigraph;
	}
	public FiscalActivity setEpigraph(String epigraph) {
		this.epigraph = epigraph;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	public FiscalActivity setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public boolean isFarmer() {
		return farmer;
	}
	public FiscalActivity setFarmer(boolean farmer) {
		this.farmer = farmer;
		return this;
	}
	
	public double getMaxPerson() {
		return maxPerson;
	}
	public FiscalActivity setMaxPerson(double maxPerson) {
		this.maxPerson = maxPerson;
		return this;
	}
	
	public double getMaxImport() {
		return maxImport;
	}
	public FiscalActivity setMaxImport(double maxImport) {
		this.maxImport = maxImport;
		return this;
	}
	
	public double getVatPercent() {
		return vatPercent;
	}
	public FiscalActivity setVatPercent(double vatPercent) {
		this.vatPercent = vatPercent;
		return this;
	}
	
	public ArrayList<FiscalActivityInfo> getInfo() {
		return info;
	}
	public FiscalActivity setInfo(ArrayList<FiscalActivityInfo> info) {
		this.info = info;
		return this;
	}
	public FiscalActivity addInfo(FiscalActivityInfo fai) {
		if (this.info == null) {
			this.info = new ArrayList<FiscalActivityInfo>();
		}
		this.info.add(fai);
		return this;
	}

	public ArrayList<FiscalActivityModule> getModuleIRPF() {
		return moduleIRPF;
	}
	public FiscalActivity setModuleIRPF(ArrayList<FiscalActivityModule> moduleIRPF) {
		this.moduleIRPF = moduleIRPF;
		return this;
	}
	public FiscalActivity addModuleIRPF(FiscalActivityModule fam) {
		if (this.moduleIRPF == null) {
			this.moduleIRPF = new ArrayList<FiscalActivityModule>();
		}
		this.moduleIRPF.add(fam);
		return this;
	}
	
	public ArrayList<FiscalActivityInfo> getInfoIRPF() {
		return infoIRPF;
	}
	public FiscalActivity setInfoIRPF(ArrayList<FiscalActivityInfo> infoIRPF) {
		this.infoIRPF = infoIRPF;
		return this;
	}
	public FiscalActivity addInfoIRPF(FiscalActivityInfo fai) {
		if (this.infoIRPF == null) {
			this.infoIRPF = new ArrayList<FiscalActivityInfo>();
		}
		this.infoIRPF.add(fai);
		return this;
	}

	public ArrayList<FiscalActivityModule> getModuleIVA() {
		return moduleIVA;
	}
	public FiscalActivity setModuleIVA(ArrayList<FiscalActivityModule> moduleIVA) {
		this.moduleIVA = moduleIVA;
		return this;
	}
	public FiscalActivity addModuleIVA(FiscalActivityModule fam) {
		if (this.moduleIVA == null) {
			this.moduleIVA = new ArrayList<FiscalActivityModule>();
		}
		this.moduleIVA.add(fam);
		return this;
	}

	public ArrayList<FiscalActivityInfo> getInfoIVA() {
		return infoIVA;
	}
	public FiscalActivity setInfoIVA(ArrayList<FiscalActivityInfo> infoIVA) {
		this.infoIVA = infoIVA;
		return this;
	}
	public FiscalActivity addInfoIVA(FiscalActivityInfo fai) {
		if (this.infoIVA == null) {
			this.infoIVA = new ArrayList<FiscalActivityInfo>();
		}
		this.infoIVA.add(fai);
		return this;
	}

	public boolean hasInfo() {
		return (info !=null && info.size() > 0);
	}
	public boolean hasModuleIRPF() {
		return (moduleIRPF !=null && moduleIRPF.size() > 0);
	}
	public boolean hasInfoIRPF() {
		return (infoIRPF !=null && infoIRPF.size() > 0);
	}
	public boolean hasModuleIVA() {
		return (moduleIVA !=null && moduleIVA.size() > 0);
	}
	public boolean hasInfoIVA() {
		return (infoIVA !=null && infoIVA.size() > 0);
	}

	public boolean hasInfoOrModules() {
		return hasInfo() || hasModuleIRPF() || hasInfoIRPF() 
			|| hasModuleIVA() || hasInfoIVA();
	}
}
