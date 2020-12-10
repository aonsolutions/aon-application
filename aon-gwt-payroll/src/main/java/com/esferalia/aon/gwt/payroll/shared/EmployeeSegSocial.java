package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class EmployeeSegSocial implements Serializable {

	private String nss;
	private String name;
	private Date birthDate;
	private String ipf;
	
	private EmployeeSegSocial() {
		super();
	}

	public EmployeeSegSocial(String nss, String name, Date birthDate, String ipf) {
		this.nss = nss;
		this.name = name;
		this.birthDate = birthDate;
		this.ipf = ipf;
	}

	public String getNss() {
		return nss;
	}

	public String getName() {
		return name;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public String getIpf() {
		return ipf.substring(1, ipf.length());
	}
	
}
