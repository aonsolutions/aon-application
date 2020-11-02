package com.esferalia.aon.in.payroll.tgss.idc;

import java.util.Date;

public class EmployeeQuotePEC {
	private String ssNum;
	private String enterpriseCCC;
	private String code;
	private String description;
	private String type;
	private String quota;
	private Date start;
	private Date end;
	
	public EmployeeQuotePEC(String ssNum, String enterpriseCCC, String code, String description, String type,
			String quota, Date start, Date end) {
		super();
		this.ssNum = ssNum;
		this.enterpriseCCC = enterpriseCCC;
		this.code = code;
		this.description = description;
		this.type = type;
		this.quota = quota;
		this.start = start;
		this.end = end;
	}

	public String getSsNum() {
		return ssNum;
	}

	public String getEnterpriseCCC() {
		return enterpriseCCC;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getType() {
		return type;
	}

	public String getQuota() {
		return quota;
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}
	
}