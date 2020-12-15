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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnterprisePayrolltest {
	@Test
	@Ignore
	public void EnterprisePayrollTest1() throws IOException {
		AONContext context = AONContext.getAONContext("sherpa.aonsolutions.net", "admin");
		Stream<Salary> salaries = AON.getSalaries(context, f -> f.getIdProperty().gt(0));
		Stream<Salary> salaries_2 = AON.getSalaries(context, f -> f.getIdProperty().gt(0));

		Map<String, Map<String, EnterprisePayrollEntry>> entries = new HashMap<>();
		Map<String, Map<String, EnterprisePayrollEntry>> ss_entries = entries;

		HashMap<String, Map<String, EnterprisePayrollEntry>> aon_system = new HashMap();
		HashMap<String, Map<String, EnterprisePayrollEntry>> ss_system = new HashMap();

		String[] keys = {"Murcia", "Bilbo", "Madrid", "Barcelona"};

		Object[] final_aon_entries = Arrays.asList(salaries.map(s ->	createEnterprisePayroll(s,false)).toArray()).stream().toArray();
		Object[] final_ss_entries = Arrays.asList(salaries_2.map(s ->	createEnterprisePayroll(s,true)).toArray()).stream().toArray();

		HashMap<String, EnterprisePayrollEntry> map = new HashMap<>();
		aon_system.put("Aon Testing", map);
		ss_system.put("Aon Testing", map);

		for (Object entry : final_aon_entries) {
			EnterprisePayrollEntry e = (EnterprisePayrollEntry)entry;
			map.put(e.getEmpleado(),e);
			if(map.size()> 2) break;
		}

		for (Object entry : final_ss_entries) {
			EnterprisePayrollEntry e = (EnterprisePayrollEntry)entry;
			System.out.println(e.getEmpleadoSS());
			map.put(e.getEmpleadoSS(),e);
			if(map.size()> 2) break;
		}



		EnterprisePayroll payroll = new EnterprisePayroll("/home/akrck02/eclipse-workspace/aon-application/aon-seg-social/logo.png",new Date(),"Nomina de empresa","Aon Solutions",aon_system,ss_system);
		EnterprisePayrollTemplate.createAonPdf(payroll,"./aonEnteprisePayrollTemplate.pdf");


	}

	private Object show(EnterprisePayrollEntry s) {

		System.out.println("------------ENTRY-------------");
		System.out.println("nombre: " + s.getEmpleado());
		System.out.println("tipo: " + s.getTipo());
		System.out.println("devengado: " + s.getDevengado());
		System.out.println("ss trabajdor: " + s.getSsTrab());
		System.out.println("irpf: " + s.getIrpf());
		System.out.println("deducciones: " + s.getDeducciones());
		System.out.println("liquido: " + s.getLiquido());
		System.out.println("ss empresa: " + s.getSsEmpr());
		System.out.println("coste total: " + s.getCosteTotal());
		System.out.println("total ss: " + s.getSsTotalSS());
		return  s;
	}


	private EnterprisePayrollEntry createEnterprisePayroll(Salary salary, boolean segSocial){

		EnterprisePayrollEntry.EnterpriseEntryType type = EnterprisePayrollEntry.EnterpriseEntryType.AON_SYSTEM;
		if(segSocial) type = EnterprisePayrollEntry.EnterpriseEntryType.SEG_SOCIAL;


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
				0.00,
				salary.getTotalSSContributions());
		return entry;
	}
}