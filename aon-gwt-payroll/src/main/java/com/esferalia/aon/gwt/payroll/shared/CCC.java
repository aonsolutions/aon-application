package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

import com.esferalia.aon.gwt.common.shared.HasId;

public class CCC implements Serializable, HasId<Integer>{

	private Integer id;
	private String code;
	private String geozone;
	private String regime;
	
	@Override
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getGeozone() {
		return geozone;
	}
	
	public void setGeozone(String geozone) {
		this.geozone = geozone;
	}
	
	public String getRegime() {
		return regime;
	}
	
	public void setRegime(String regime) {
		this.regime = regime;
	}
	
}
