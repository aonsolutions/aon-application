package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ActivityInfo implements Serializable{

	private Integer id;
	private Integer domain;
	private String description;
	private String cnae2009Code;
	private String cnae2009Title;
	private String regime;
	private Boolean active;
	
	private Map<Integer, CCCInfo> cccs = new HashMap<>();
	private Map<String, String> allCNAE2009 = new HashMap<>();
	
	public ActivityInfo() {
		super();
	}
	
	public ActivityInfo(Boolean test) {
		if(test) {
			cccs.put(1, new CCCInfo("01110100000", (byte)0, "ARABA/ALAVA", 1147, 1));
			cccs.put(2, new CCCInfo("01112800000", (byte)0, "MADRID", 1147, 2));
			cccs.put(3, new CCCInfo("01110100000", (byte)0, "GIPUZKUA", 1147, 3));
		}
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

	public void deleteCCC(Integer cccId) {
		this.cccs.remove(cccId);
	}

	public void insertCCC(Integer cccId, String ccc, String cccRegime, String cccAccount, Byte type, String geozone) {
		this.cccs.put(cccId, new CCCInfo(ccc, cccRegime, cccAccount, type, geozone, getId(), cccId));
	}

	public Map<String, String> getAllCNAE2009() {
		return this.allCNAE2009;
	}
	
	public void addCNAE2009(String code, String description) {
		this.allCNAE2009.put(code, description);
	}
	
}
