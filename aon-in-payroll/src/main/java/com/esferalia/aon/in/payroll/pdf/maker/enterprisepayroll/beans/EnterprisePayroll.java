package com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EnterprisePayroll {

	private Optional<InputStream>									   logo;
	private Optional<Date>											   month;
	private Optional<String>										   header;
	private Optional<String>										   subheader;
	private Optional<Map<String, Map<String, EnterprisePayrollEntry>>> entries;

	public EnterprisePayroll(
			InputStream logo, Date month, String header, String subheader,
			Map<String, Map<String, EnterprisePayrollEntry>> entries,
			Map<String, Map<String, EnterprisePayrollEntry>> ssEntries
	) {
		this.logo	   = Optional.ofNullable(logo);
		this.month	   = Optional.ofNullable(month);
		this.header	   = Optional.ofNullable(header);
		this.subheader = Optional.ofNullable(subheader);
		this.entries   = Optional.ofNullable(merge(entries, ssEntries));
	}

	public Map<String, Map<String, EnterprisePayrollEntry>> merge(
			Map<String, Map<String, EnterprisePayrollEntry>> aonCategoryEntries,
			Map<String, Map<String, EnterprisePayrollEntry>> ssCategoryEntries
	) {
		if (ssCategoryEntries == null)
			ssCategoryEntries = new HashMap<>();
		if (aonCategoryEntries == null)
			aonCategoryEntries = new HashMap<>();
		Set<String> ssCategories = ssCategoryEntries.keySet();

		for (String category : ssCategories)
		{
			if (aonCategoryEntries.containsKey(category))
			{
				Map<String, EnterprisePayrollEntry>	aonEntries = aonCategoryEntries.get(category);
				Map<String, EnterprisePayrollEntry>	ssEntries  = ssCategoryEntries.get(category);
				Set<String>							entryKeys  = ssEntries.keySet();

				for (String key : entryKeys)
				{
					EnterprisePayrollEntry ssEn = ssEntries.get(key);
					if (aonEntries.containsKey(key))
					{
						EnterprisePayrollEntry aonEn = aonEntries.get(key);

						aonEn.mergeSsEntry(ssEn);
						aonEntries.put(key, aonEn);
					} else
						aonEntries.put(key, ssEn);
				}
			} else
				aonCategoryEntries.put(category, ssCategoryEntries.get(category));
		}
		return aonCategoryEntries;
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
}
