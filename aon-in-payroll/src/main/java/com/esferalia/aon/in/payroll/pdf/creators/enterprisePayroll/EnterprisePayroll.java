package com.esferalia.aon.in.payroll.pdf.creators.enterprisePayroll;

import java.util.Date;
import java.util.Map;

public class EnterprisePayroll {
	private String logo;
	private Date month;
	private String header;
	private String subheader;
	private Map<String,Map<String,EnterprisePayrollEntry>> entries;
	private Map<String,Map<String,EnterprisePayrollEntry>> ss_entries;

	public EnterprisePayroll(String logo, Date month, String header, String subheader,
							 Map<String, Map<String, EnterprisePayrollEntry>> entries,
							 Map<String, Map<String, EnterprisePayrollEntry>> ss_entries) {
		this.logo = logo;
		this.month = month;
		this.header = header;
		this.subheader = subheader;
		this.entries = entries;
		this.ss_entries = ss_entries;
	}

	public String getLogo() {return logo;}
	public Date getMonth() {return month;}
	public String getHeader() {return header;}
	public String getSubheader() {return subheader;}
	public Map<String, Map<String, EnterprisePayrollEntry>> getEntries() {return entries;}
	public Map<String, Map<String, EnterprisePayrollEntry>> getSs_entries() {return ss_entries;}
}
