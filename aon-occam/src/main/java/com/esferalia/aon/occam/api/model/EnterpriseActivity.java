package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class EnterpriseActivity implements Serializable {

	private static final long serialVersionUID = 1603402672575353203L;
	
	private Integer id;
	private String description;
	private boolean principal;
	private Integer cnae;
	private String cnaeCode;
	private String cnaeDescription;

	public Integer getId() {
		return id;
	}

	public EnterpriseActivity setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public EnterpriseActivity setDescription(String description) {
		this.description = description;
		return this;
	}
	public boolean isPrincipal() {
		return principal;
	}
	public EnterpriseActivity setPrincipal(boolean principal) {
		this.principal = principal;
		return this;		
	}

	public Integer getCnae() {
		return cnae;
	}

	public EnterpriseActivity setCnae(Integer cnae) {
		this.cnae = cnae;
		return this;		
	}
	public String getCnaeCode() {
		return cnaeCode;
	}

	public EnterpriseActivity setCnaeCode(String cnaeCode) {
		this.cnaeCode = cnaeCode;
		return this;		
	}

	public String getCnaeDescription() {
		return cnaeDescription;
	}

	public EnterpriseActivity setCnaeDescription(String cnaeDescription) {
		this.cnaeDescription = cnaeDescription;
		return this;		
	}


}
