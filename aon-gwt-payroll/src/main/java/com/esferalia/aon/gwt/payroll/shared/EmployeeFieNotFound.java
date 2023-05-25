package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class EmployeeFieNotFound implements Serializable {
	private String ccc;
	private String naf;
	private String ipf;
	private String fullName;
	
	public EmployeeFieNotFound() {
		super();
	}

	public String getCcc() {
		return ccc;
	}

	public EmployeeFieNotFound setCcc(String ccc) {
		this.ccc = ccc;
		return this;
	}

	public String getNaf() {
		return naf;
	}

	public EmployeeFieNotFound setNaf(String naf) {
		this.naf = naf;
		return this;
	}

	public String getIpf() {
		return ipf;
	}

	public EmployeeFieNotFound setIpf(String ipf) {
		this.ipf = ipf;
		return this;
	}

	public String getFullName() {
		return fullName;
	}

	public EmployeeFieNotFound setFullName(String fullName) {
		this.fullName = fullName;
		return this;
	}
}
