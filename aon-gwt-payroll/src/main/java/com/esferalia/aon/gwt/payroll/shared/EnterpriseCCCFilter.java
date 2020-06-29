package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.List;

public class EnterpriseCCCFilter implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// CCC type filter
	private Integer cccType; 
	
	// Enterprise filter
	private Integer enterpriseId;
	
	private List<Integer> enterprisesIds;
	private String geozone;
	private String type;
	
	public EnterpriseCCCFilter() {
		super();
		
		this.cccType = null;
		this.enterpriseId = null;
	}

	public Integer getCccType() {
		return cccType;
	}

	public void setCccType(Integer cccType) {
		this.cccType = cccType;
	}

	public Integer getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Integer enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public List<Integer> getEnterprisesIds() {
		return enterprisesIds;
	}

	public void setEnterprisesIds(List<Integer> enterprisesIds) {
		this.enterprisesIds = enterprisesIds;
	}

	public String getGeozone() {
		return geozone;
	}

	public void setGeozone(String geozone) {
		this.geozone = geozone;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
