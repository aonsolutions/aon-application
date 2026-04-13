package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.watson.util.AonStringUtils;

public class EmployeeSegSocial implements Serializable {

	private String nss;
	private String name;
	private String birthDate;
	private String ipf;

	private EmployeeSegSocial() {
		super();
	}

	public EmployeeSegSocial(String nss, String name, Date birthDate, String ipf) {
		this.nss = nss;
		this.name = name;
		this.birthDate = format(birthDate);
		this.ipf = ipf;
	}

	public String getNss() {
		return nss;
	}

	public String getName() {
		return name;
	}

	public Date getBirthDate() {
		return parse(birthDate);
	}

	public String getIpf() {
		return ipf.substring(1, ipf.length());
	}

	protected static Date parse(String str) {
		if (str == null)
			return null;
		int year = Integer.parseInt(str.substring(0, 4));
		int month = Integer.parseInt(str.substring(4, 6));
		int day = Integer.parseInt(str.substring(6, 8));
		return new Date(year, month, day);
	}

	protected static String format(Date date) {
		if (date == null)
			return null;

		int year = date.getYear();
		int month = date.getMonth();
		int day = date.getDate();
		return AonStringUtils.leftPad(Integer.toString(year), 4, '0')
				+ AonStringUtils.leftPad(Integer.toString(month), 2, '0')
				+ AonStringUtils.leftPad(Integer.toString(day), 2, '0');
	}

}
