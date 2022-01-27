package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.format;
import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.Date;

public class ITPart implements Serializable {
	
	private static final long serialVersionUID = 1L;
		
	private Integer domain;
	private Integer id;
	private Byte type;
	private Integer it;
	private String collegeNumber;
	private Byte confirmOrderNumber;
	private String cias;
	private String date;
	private Byte status;
	
	public ITPart() {
		super();
	}

	public Integer getDomain() {
		return domain;
	}

	public Integer getId() {
		return id;
	}

	public ITPart setId(Integer id) {
		this.id = id;
		return this;
	}

	public ITPart setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Byte getType() {
		return null == type ? 0 : type;
	}

	public ITPart setType(Byte type) {
		this.type = type;
		return this;
	}

	public Integer getIt() {
		return it;
	}

	public ITPart setIt(Integer it) {
		this.it = it;
		return this;
	}

	public String getCollegeNumber() {
		return collegeNumber;
	}

	public ITPart setCollegeNumber(String collegeNumber) {
		this.collegeNumber = collegeNumber;
		return this;
	}

	public Byte getConfirmOrderNumber() {
		return confirmOrderNumber;
	}

	public ITPart setConfirmOrderNumber(Byte confirmOrderNumber) {
		this.confirmOrderNumber = confirmOrderNumber;
		return this;
	}

	public String getCias() {
		return cias;
	}

	public ITPart setCias(String cias) {
		this.cias = cias;
		return this;
	}

	public Date getDate() {
		return parse(date);
	}

	public ITPart setDate(Date date) {
		this.date = format(date);
		return this;
	}

	public Byte getStatus() {
		return null == status ? (byte) 0 : status;
	}

	public ITPart setStatus(Byte status) {
		this.status = status;
		return this;
	}
		
}
