package com.esferalia.aon.in.payroll.pdf.creators;

import com.esferalia.aon.in.payroll.pdf.creators.enterprisePayroll.EnterprisePayroll;
import com.esferalia.aon.in.payroll.pdf.creators.enterprisePayroll.EnterprisePayrollEntry;
import com.esferalia.aon.in.payroll.pdf.creators.enterprisePayroll.EnterprisePayrollTemplate;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnterprisePayrolltest {
	@Test
	public void EnterprisePayrollTest1() throws IOException {
		AONContext context = AONContext.getAONContext("sherpa.aonsolutions.net", "admin");

		String[] possibleDates = {"2019-1", "2019-10", "2019-11","2019-12","2019-2","2019-3","2019-4","2019-5","2019-6","2019-7","2019-8","2019-9","2020-08"};
		HashMap<String, Map<String, EnterprisePayrollEntry>> aon_system = new HashMap();
		HashMap<String, Map<String, EnterprisePayrollEntry>> ss_system = new HashMap();

		for (String dateStr : possibleDates) {
			Stream<Salary> salaries = AON.getSalaries(context, f -> f.getStartDateProperty().between(parseDate(dateStr + "-01", "yyyy-MM-dd"), parseDate(dateStr + "-30", "yyyy-MM-dd")));
			Stream<Salary> salaries_2 = AON.getSalaries(context, f -> f.getStartDateProperty().between(parseDate(dateStr + "-01", "yyyy-MM-dd"), parseDate(dateStr + "-30", "yyyy-MM-dd")));

			Map<String, Map<String, EnterprisePayrollEntry>> entries = new HashMap<>();
			Map<String, Map<String, EnterprisePayrollEntry>> ss_entries = entries;

			Object[] final_aon_entries = Arrays.asList(salaries.map(s -> createEnterprisePayroll(s, false)).toArray()).stream().toArray();
			Object[] final_ss_entries = Arrays.asList(salaries_2.map(s -> createEnterprisePayroll(s, true)).toArray()).stream().toArray();

			HashMap<String, EnterprisePayrollEntry> map = new HashMap<>();
			aon_system.put(dateStr, map);

			HashMap<String, EnterprisePayrollEntry> map_2 = new HashMap<>();
			ss_system.put(dateStr, map_2);

			for (Object entry : final_aon_entries) {
				EnterprisePayrollEntry e = (EnterprisePayrollEntry) entry;
				map.put(e.getEmpleado(), e);

			}

			for (Object entry : final_ss_entries) {
				EnterprisePayrollEntry e = (EnterprisePayrollEntry) entry;
				System.out.println(e.getEmpleadoSS());
				map_2.put(e.getEmpleadoSS(), e);

			}
		}

		EnterprisePayroll payroll = new EnterprisePayroll(null, new Date(), "Nomina de empresa", "Aon Solutions", aon_system, ss_system);
		EnterprisePayrollTemplate.createAonPdf(payroll, "./Test1.pdf");
	}

	@Test
	public void EnterprisePayrollTest2() throws IOException {
		AONContext context = AONContext.getAONContext("sherpa.aonsolutions.net", "admin");

		String[] possibleDates = {"2019-1", "2019-10", "2019-11","2019-12","2019-2","2019-3","2019-4","2019-5","2019-6","2019-7","2019-8","2019-9","2020-08"};
		HashMap<String, Map<String, EnterprisePayrollEntry>> aon_system = new HashMap();

		for (String dateStr : possibleDates) {
			Stream<Salary> salaries = AON.getSalaries(context, f -> f.getStartDateProperty().between(parseDate(dateStr + "-01", "yyyy-MM-dd"), parseDate(dateStr + "-30", "yyyy-MM-dd")));

			Map<String, Map<String, EnterprisePayrollEntry>> entries = new HashMap<>();
			Object[] final_aon_entries = Arrays.asList(salaries.map(s -> createEnterprisePayroll(s, false)).toArray()).stream().toArray();

			HashMap<String, EnterprisePayrollEntry> map = new HashMap<>();
			aon_system.put(dateStr, map);

			HashMap<String, EnterprisePayrollEntry> map_2 = new HashMap<>();
			for (Object entry : final_aon_entries) {
				EnterprisePayrollEntry e = (EnterprisePayrollEntry) entry;
				map.put(e.getEmpleado(), e);

			}
		}

		EnterprisePayroll payroll = new EnterprisePayroll(null, new Date(), "Nomina de empresa", "Aon Solutions", aon_system, aon_system);
		EnterprisePayrollTemplate.createAonPdf(payroll, "./Test2.pdf");
	}

	@Test
	public void EnterprisePayrollTest3() throws IOException {
		AONContext context = AONContext.getAONContext("sherpa.aonsolutions.net", "admin");

		String[] possibleDates = {"2019-1", "2019-10", "2019-11","2019-12","2019-2","2019-3","2019-4","2019-5","2019-6","2019-7","2019-8","2019-9","2020-08"};
		HashMap<String, Map<String, EnterprisePayrollEntry>> aon_system = new HashMap();

		for (String dateStr : possibleDates) {
			Stream<Salary> salaries = AON.getSalaries(context, f -> f.getStartDateProperty().between(parseDate(dateStr + "-01", "yyyy-MM-dd"), parseDate(dateStr + "-30", "yyyy-MM-dd")));

			Map<String, Map<String, EnterprisePayrollEntry>> entries = new HashMap<>();
			Object[] final_aon_entries = Arrays.asList(salaries.map(s -> createEnterprisePayroll(s, true)).toArray()).stream().toArray();

			HashMap<String, EnterprisePayrollEntry> map = new HashMap<>();
			aon_system.put(dateStr, map);

			HashMap<String, EnterprisePayrollEntry> map_2 = new HashMap<>();
			for (Object entry : final_aon_entries) {
				EnterprisePayrollEntry e = (EnterprisePayrollEntry) entry;
				map.put(e.getEmpleado(), e);

			}
		}

		EnterprisePayroll payroll = new EnterprisePayroll(null, new Date(), "Nomina de empresa", "Aon Solutions", null, aon_system);
		EnterprisePayrollTemplate.createAonPdf(payroll, "./Test3.pdf");
	}

	private EnterprisePayrollEntry createEnterprisePayroll(Salary salary, boolean segSocial) {
		EnterprisePayrollEntry.EnterpriseEntryType type = EnterprisePayrollEntry.EnterpriseEntryType.AON_SYSTEM;
		if (segSocial) type = EnterprisePayrollEntry.EnterpriseEntryType.SEG_SOCIAL;

		EnterprisePayrollEntry entry = new EnterprisePayrollEntry(
				type,
				salary.getEmployeeName(),
				"Nomina",
				salary.getTotalPayment(),
				salary.getTotalSSContributions(),
				salary.getTotalIrpf(),
				salary.getTotalDeduction(),
				salary.getTotalLiquid(),
				salary.getTotalEnterprise(),
				salary.getTotalPayment() + salary.getTotalEnterprise(),
				salary.getTotalSSContributions() + salary.getTotalEnterprise());

		return entry;
	}

	public static Date parseDate(String dateStr, String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		Date formattedDate;

		try {
			formattedDate = dateFormatter.parse(dateStr);
			return formattedDate;
		} catch (ParseException e) {
			return null;
		}
	}

}