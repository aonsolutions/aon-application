package com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayrollEntry.EnterpriseEntryType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class EnterprisePayroll {
	
	private static final Locale LOCALE_ES = new Locale("es");

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
				Set<String>							orphanKeys  = new HashSet<String>();

				for (String key : entryKeys)
				{
					EnterprisePayrollEntry ssEn = ssEntries.get(key);
					if (aonEntries.containsKey(key))
					{
						EnterprisePayrollEntry aonEn = aonEntries.get(key);

						aonEn.mergeSsEntry(ssEn);
						aonEntries.put(key, aonEn);
					} else {
						orphanKeys.add(key);
					}
				}

				for (String key : orphanKeys) {
					EnterprisePayrollEntry ssEn = ssEntries.get(key);
					List<Map.Entry<String,EnterprisePayrollEntry>> orphanAonEntries =
					aonEntries.entrySet().stream()
					.filter(entry -> !entry.getValue().isMergedSS())
					.filter(entry-> !Objects.equals(entry.getValue().getTipo().orElse(""), SalaryType.EXTRA.getName(LOCALE_ES) ))
					.filter(entry -> entry.getValue().getNaf().equals(ssEn.getNaf()))
					.filter(entry -> isSameTipo(ssEn.getTipoSS().orElse("--"), entry.getValue().getTipo().orElse("??")))
					.toList()						;
					orphanAonEntries.forEach(entry -> aonEntries.remove(entry.getKey()));
					
					EnterprisePayrollEntry aonEn =
					orphanAonEntries.stream()
					.map(Map.Entry::getValue)
					.reduce(new EnterprisePayrollEntry(
							EnterpriseEntryType.AON_SYSTEM,
							ssEn.getNaf(),
							ssEn.getCcc(),
							ssEn.getStartDate(),
							ssEn.getEndDate(),
							ssEn.getEmpleadoSS().orElse(null), 
							ssEn.getTipo().orElse(null), 
							0.00, 
							0.00, 
							0.00, 
							0.00, 
							0.00, 
							0.00, 
							0.00, 
							0.00, 
							0.00), 
							(e1, e2) -> {
								e2.getEmpleado().ifPresent(s -> e1.setEmpleado(e2.getEmpleado()));
								e2.getTipo().ifPresent(s -> e1.setTipo(e2.getTipo()));
								e1.setDevengado(sum(e1.getDevengado(),e2.getDevengado()));
								e1.setSsTrab(sum(e1.getSsTrab(),e2.getSsTrab()));
								e1.setIrpf(sum(e1.getIrpf(),e2.getIrpf()));
								e1.setDeducciones(sum(e1.getDeducciones(),e2.getDeducciones()));
								e1.setLiquido(sum(e1.getLiquido(),e2.getLiquido()));
								e1.setSsEmpr(sum(e1.getSsEmpr(),e2.getSsEmpr()));
								e1.setSsTotal(sum(e1.getSsTotal(),e2.getSsTotal()));
								e1.setBonificaciones(sum(e1.getBonificaciones(),e2.getBonificaciones()));
								return e1;
							});
					
					
					aonEn.mergeSsEntry(ssEn);
					aonEntries.put(key, aonEn);
					
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
	
	private static Optional<Double> sum(Optional<Double> d1, Optional<Double> d2){
		return Optional.of(d1.orElse(0.00) + d2.orElse(0.00));
	}
	
	private static boolean isSameTipo(String ssTipo, String tipo) {
		if ( Objects.equals(tipo, SalaryType.SALARY.getName(LOCALE_ES))) {
			return Objects.equals(ssTipo, SalaryType.L00.getName(LOCALE_ES));
		} else if ( Objects.equals(tipo, SalaryType.SETTLE.getName(LOCALE_ES))) {
			return Objects.equals(ssTipo, SalaryType.L13.getName(LOCALE_ES));
		} else if ( Objects.equals(tipo, SalaryType.DELAY.getName(LOCALE_ES))) {
			return Objects.equals(ssTipo, SalaryType.L03.getName(LOCALE_ES));
		}     
		return false;
	}
}
