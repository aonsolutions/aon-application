package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ActivityInfo implements Serializable{

	private Integer id;
	private String description;
	private Integer cnae2009;
	private String regime;
	private Boolean active;
	
	private Map<Integer, CCCInfo> cccs = new HashMap<>();
	
	public ActivityInfo() {
		super();
	}
	
	public ActivityInfo(Boolean test) {
		if(test) {
			this.id = 1147;
			this.description = "OFICINAS Y DESPACHOS";
			this.cnae2009 = 6201;
			this.regime = "General";
			this.active = true;
			
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getCnae2009() {
		return cnae2009;
	}

	public void setCnae2009(Integer cnae2009) {
		this.cnae2009 = cnae2009;
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

	public void insertCCC(Integer cccId, String ccc, Byte type, String geozone) {
		this.cccs.put(cccId, new CCCInfo(ccc, type, geozone, getId(), cccId));
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
