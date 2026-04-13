package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.watson.util.AonStringUtils;

public class SecondaryUserCertificate implements Serializable {

	private static final long serialVersionUID = 1L;

	private String authoritation;
	private String authoritation_entity;
	private String main_user_name;
	private String main_user_ipf;
	private String main_user_naf;

	private String name;
	private String province;
	private String ipf;
	private String naf;
	private String situation;
	private String situation_date;
	private String telephone;
	private String fax;
	private String mobile;
	private String mail;

	public SecondaryUserCertificate() {
		super();
	}

	public SecondaryUserCertificate(String authoritation, String authoritation_entity, String main_user_name,
			String main_user_ipf, String main_user_naf, String name, String province, String ipf, String naf,
			String situation, Date situation_date, String telephone, String fax, String mobile, String mail) {
		super();
		this.authoritation = authoritation;
		this.authoritation_entity = authoritation_entity;
		this.main_user_name = main_user_name;
		this.main_user_ipf = main_user_ipf;
		this.main_user_naf = main_user_naf;
		this.name = name;
		this.province = province;
		this.ipf = ipf;
		this.naf = naf;
		this.situation = situation;
		this.situation_date = format(situation_date);
		this.telephone = telephone;
		this.fax = fax;
		this.mobile = mobile;
		this.mail = mail;
	}

	public String getAuthoritation() {
		return authoritation;
	}

	public String getAuthoritation_entity() {
		return authoritation_entity;
	}

	public String getMain_user_name() {
		return main_user_name;
	}

	public String getMain_user_ipf() {
		return main_user_ipf;
	}

	public String getMain_user_naf() {
		return main_user_naf;
	}

	public String getName() {
		return name;
	}

	public String getProvince() {
		return province;
	}

	public String getIpf() {
		return ipf;
	}

	public String getNaf() {
		return (null != naf && naf.length() == 9) ? '0' + naf : naf;
	}

	public String getSituation() {
		return situation;
	}

	public Date getSituation_date() {
		return parse(situation_date);
	}

	public String getTelephone() {
		return telephone;
	}

	public String getFax() {
		return fax;
	}

	public String getMobile() {
		return mobile;
	}

	public String getMail() {
		return mail;
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