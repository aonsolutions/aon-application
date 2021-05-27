package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityInfo implements Serializable{

	private Integer id;
	private Integer domain;
	private Integer enterprise;
	private String description;
	private String cnae2009Code;
	private String cnae2009Title;
	private String regime;
	private Date startDate;
	private Date endDate;
	private Boolean active;
	
	private Map<Integer, CCCInfo> cccs = new HashMap<>();
	private Map<Integer, CCCInfo> deleteCCCs = new HashMap<>();
	private Map<String, String> allCNAE2009 = new HashMap<>();
	
	public ActivityInfo() {
		super();
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDomain() {
		return domain;
	}

	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	
	public Integer getEnterprise() {
		return this.enterprise;
	}
	
	public void setEnterprise(Integer enterprise) {
		this.enterprise = enterprise;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCnae2009Code() {
		return cnae2009Code;
	}

	public void setCnae2009Code(String cnae2009Code) {
		this.cnae2009Code = cnae2009Code;
	}

	public String getCnae2009Title() {
		return cnae2009Title;
	}

	public void setCnae2009Title(String cnae2009Title) {
		this.cnae2009Title = cnae2009Title;
	}

	public String getRegime() {
		return regime;
	}

	public void setRegime(String regime) {
		this.regime = regime;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Map<Integer, CCCInfo> getCccs() {
		return cccs;
	}

	public void setCccs(Map<Integer, CCCInfo> cccs) {
		this.cccs = cccs;
	}
	
	public Map<Integer, CCCInfo> getDeleteCccs() {
		return deleteCCCs;
	}

	public void deleteCCC(Integer cccId) {
		CCCInfo cccInfo = this.cccs.get(cccId);
		this.deleteCCCs.put(cccId, cccInfo);
		this.cccs.remove(cccId);
	}

	public void insertCCC(Integer cccId, String ccc, String cccRegime, String cccAccount, Byte type, String geozone, String geozoneCode, Boolean useByContracts, Boolean useByCras) {
		this.cccs.put(cccId, new CCCInfo(ccc, cccRegime, cccAccount, type, geozone, geozoneCode, getId(), cccId, useByContracts, useByCras));
	}
	
	public void insertCCC(Integer cccId, String ccc, String cccRegime, String cccAccount, Byte type, String geozone, String geozoneCode) {
		Boolean useByContracts = this.cccs.get(cccId).isUseByContracts();
		
		this.cccs.put(cccId, new CCCInfo(ccc, cccRegime, cccAccount, type, geozone, geozoneCode, getId(), cccId, useByContracts));
	}

	public Map<String, String> getAllCNAE2009() {
		return this.allCNAE2009;
	}
	
	public void setAllCNAE2009(Map<String, String> allCNAE2009) {
		this.allCNAE2009 = allCNAE2009;
	}
	
	public void addCNAE2009(String code, String description) {
		this.allCNAE2009.put(code, description);
	}
	
}
