package com.esferalia.aon.in.payroll.pdf.creators.enterprisePayroll;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EnterprisePayroll {
	private String logo;
	private Date month;
	private String header;
	private String subheader;
	private Map<String,Map<String,EnterprisePayrollEntry>> entries;

	public EnterprisePayroll(String logo, Date month, String header, String subheader,
							 Map<String, Map<String, EnterprisePayrollEntry>> entries,
							 Map<String, Map<String, EnterprisePayrollEntry>> ss_entries) {
		this.logo = logo;
		this.month = month;
		this.header = header;
		this.subheader = subheader;
		this.entries = merge(entries,ss_entries);
	}

	public Map<String,Map<String,EnterprisePayrollEntry>> merge(Map<String, Map<String, EnterprisePayrollEntry>> aon_category_entries, Map<String, Map<String, EnterprisePayrollEntry>> ss_category_entries){

		if(ss_category_entries == null) ss_category_entries = new HashMap<>();
		if(aon_category_entries == null) aon_category_entries = new HashMap<>();
		Set<String> ss_categories = ss_category_entries.keySet();

		for (String category : ss_categories){
			if(aon_category_entries.containsKey(category)){
				Map<String, EnterprisePayrollEntry> aon_entries = aon_category_entries.get(category);
				Map<String, EnterprisePayrollEntry> ss_entries = ss_category_entries.get(category);
				Set<String> entry_keys = ss_entries.keySet();

				for (String key : entry_keys){
					EnterprisePayrollEntry ss_en = ss_entries.get(key);
					if(aon_entries.containsKey(key)){
						EnterprisePayrollEntry aon_en = aon_entries.get(key);

						aon_en.merge_ss_entry(ss_en);
						aon_entries.put(key,aon_en);
					}else aon_entries.put(key,ss_en);
				}
			}else	aon_category_entries.put(category,ss_category_entries.get(category));
		}
		return aon_category_entries;
	}

	public String getLogo() {return logo;}
	public Date getMonth() {return month;}
	public String getHeader() {return header;}
	public String getSubheader() {return subheader;}
	public Map<String, Map<String, EnterprisePayrollEntry>> getEntries() {return entries;}

	@Override
	public String toString() {
		return "EnterprisePayroll{" +
				"logo='" + logo + '\'' +
				", month=" + month +
				", header='" + header + '\'' +
				", subheader='" + subheader + '\'' +
				", entries=" + entries +
				'}';
	}
}
