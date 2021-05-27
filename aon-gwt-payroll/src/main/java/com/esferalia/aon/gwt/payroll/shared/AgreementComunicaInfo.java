package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AgreementComunicaInfo implements Serializable {
	private Integer id;
	private String description;
	private String ssNumber;
	
	public AgreementComunicaInfo() {
		super();
	}

	public AgreementComunicaInfo(Integer id, String description, String ssNumber) {
		super();
		this.id = id;
		this.description = description;
		this.ssNumber = ssNumber;
	}

	public Integer getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getSSNumber() {
		return ssNumber;
	}
	
}
