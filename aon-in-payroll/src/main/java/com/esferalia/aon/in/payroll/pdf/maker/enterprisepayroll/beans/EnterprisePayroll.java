package com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EnterprisePayroll {
	
	private Optional<InputStream> logo;
	private Optional<Date> month;
	private Optional<String> header;
	private Optional<String> subheader;
	private Optional<Map<String,Map<String,EnterprisePayrollEntry>>> entries;

	public EnterprisePayroll(InputStream logo, Date month, String header, String subheader,Map<String, Map<String, EnterprisePayrollEntry>> entries,Map<String, Map<String, EnterprisePayrollEntry>> ss_entries) {
		this.logo = Optional.ofNullable(logo);
		this.month = Optional.ofNullable(month);
		this.header = Optional.ofNullable(header);
		this.subheader = Optional.ofNullable(subheader);
		this.entries = Optional.ofNullable(merge(entries,ss_entries));
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


	public Optional<InputStream> getLogo() {
		return logo;
	}

	public Optional<Date> getMonth() {
		return month;
	}

	public Optional<String> getHeader() {
		return header;
	}

	public Optional<String> getSubheader() {
		return subheader;
	}

	public Optional<Map<String, Map<String, EnterprisePayrollEntry>>> getEntries() {
		return entries;
	}

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
