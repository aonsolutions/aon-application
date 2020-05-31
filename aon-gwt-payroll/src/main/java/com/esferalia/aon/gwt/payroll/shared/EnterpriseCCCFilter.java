package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class EnterpriseCCCFilter implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// CCC type filter
	private Integer cccType; 
	
	// Enterprise filter
	private Integer enterpriseId;
	
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
	
}
