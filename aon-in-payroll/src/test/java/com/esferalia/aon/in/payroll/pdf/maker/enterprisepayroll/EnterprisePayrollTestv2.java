package com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll;

import static com.esferalia.aon.in.payroll.pdf.maker.PdfMaker.printEnterprisePayroll;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayroll;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayrollEntry;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayrollEntry.EnterpriseEntryType;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.github.javafaker.Faker;

public class EnterprisePayrollTestv2 {
	
	@Test
	public void testEnterprisePayroll() {
		
		try {
			
			System.out.println("\n\n-----------------------------------");
			System.out.println(" ENTERPRISE PAYROLL CREATOR");
			System.out.println("-----------------------------------");
			System.out.println("\n Starting.....");
			
			
			Map<String, Map<String, EnterprisePayrollEntry>> entries = new HashMap<>();
			Map<String, Map<String, EnterprisePayrollEntry>> ssEntries = new HashMap<>();;

			HashMap<String, EnterprisePayrollEntry> categoria1 = new HashMap<>();
			HashMap<String, EnterprisePayrollEntry> categoria2 = new HashMap<>();
			HashMap<String, EnterprisePayrollEntry> categoria3 = new HashMap<>();
			HashMap<String, EnterprisePayrollEntry> categoria4 = new HashMap<>();
			HashMap<String, EnterprisePayrollEntry> categoria5 = new HashMap<>();
			HashMap<String, EnterprisePayrollEntry> categoria6 = new HashMap<>();

			Faker f = new Faker();
			
			System.out.println(" Starting java faker.....");
			System.out.println(" Collecting data .....");
			
			for (int i = 0; i < 4; i++) {
				String cat = "Categoria " + i;
				EnterprisePayrollEntry e = new EnterprisePayrollEntry(EnterpriseEntryType.AON_SYSTEM, 
						f.number().digits(12),
						f.number().digits(11),
						AonDateUtils.getFirstDayOfMonth(new Date()),
						AonDateUtils.getLastDayOfMonth(new Date()),
						AonDateUtils.getLastDayOfMonth(new Date()),
						f.name().fullName(), 
						"N\u00f3mina",
						f.number().randomDouble(2, 0, 99999),
						f.number().randomDouble(2, 0, 99999),
						f.number().randomDouble(2, 0, 99999),
						f.number().randomDouble(2, 0, 99999),
						f.number().randomDouble(2, 0, 99999), 
						f.number().randomDouble(2, 0, 99999),
						f.number().randomDouble(2, 0, 99999), 
						f.number().randomDouble(2, 0, 99999), 
						f.number().randomDouble(2, 0, 99999)
				);

				System.out.println(" > new Entry: " + e.getEmpleado());
				categoria1.put(cat, e);
				categoria2.put(cat, e);
				categoria3.put(cat, e);
			}
			System.out.println(" Setting up categories .....");
			
			for (int i = 0; i < 4; i++) {
				String cat = "cet " + i;
				EnterprisePayrollEntry e = new EnterprisePayrollEntry(EnterpriseEntryType.AON_SYSTEM, 
						f.number().digits(12),
						f.number().digits(11),
						AonDateUtils.getFirstDayOfMonth(new Date()),
						AonDateUtils.getLastDayOfMonth(new Date()),
						AonDateUtils.getLastDayOfMonth(new Date()),
						f.name().fullName(), 
						"N\u00f3mina",
						f.number().randomDouble(2, 0, 99999),
						f.number().randomDouble(2, 0, 99999),
						f.number().randomDouble(2, 0, 99999),
						f.number().randomDouble(2, 0, 99999),
						f.number().randomDouble(2, 0, 99999), 
						f.number().randomDouble(2, 0, 99999),
						f.number().randomDouble(2, 0, 99999), 
						f.number().randomDouble(2, 0, 99999), 
						f.number().randomDouble(2, 0, 99999)
				);
				System.out.println(" > new Entry: " + e.getEmpleado());
				
				categoria1.put(cat, e);
				categoria2.put(cat, e);
				categoria3.put(cat, e);
			}
			System.out.println(" Settting up data.....");
			
			for (int i = 0; i < 4; i++) {
				String cat = "Categoria " + i;
				EnterprisePayrollEntry e = new EnterprisePayrollEntry(EnterpriseEntryType.SEG_SOCIAL, 
						f.number().digits(12),
						f.number().digits(11),
						AonDateUtils.getFirstDayOfMonth(new Date()),
						AonDateUtils.getLastDayOfMonth(new Date()),
						AonDateUtils.getLastDayOfMonth(new Date()),
						f.name().fullName(), 
						"N\u00f3mina",
						f.number().randomDouble(2, 0, 99999),
						f.number().randomDouble(2, 0, 99999),
						f.number().randomDouble(2, 0, 99999),
						f.number().randomDouble(2, 0, 99999),
						f.number().randomDouble(2, 0, 99999), 
						f.number().randomDouble(2, 0, 99999),
						f.number().randomDouble(2, 0, 99999), 
						f.number().randomDouble(2, 0, 99999), 
						f.number().randomDouble(2, 0, 99999)
				);
				System.out.println(" > new Entry: " + e.getEmpleado());
				
				categoria3.put(cat, e);
				categoria4.put(cat, e);
				categoria5.put(cat, e);
				categoria6.put(cat, e);
			}
			
			entries.put(f.pokemon().location(),categoria1);
			entries.put(f.pokemon().location(),categoria2);
			entries.put(f.pokemon().location(),categoria3);
			
			ssEntries.put(f.pokemon().location(),categoria4);
			ssEntries.put(f.pokemon().location(),categoria5);
			ssEntries.put(f.pokemon().location(),categoria6);

			EnterprisePayroll payroll = new EnterprisePayroll(null, new Date(), "N\u00d3MINA EMPRESA", f.zelda().game() + " S.L",	entries, ssEntries);
			printEnterprisePayroll(payroll,new FileOutputStream("ListadoCostesPDF.pdf"),Optional.of(new Locale("Es")));
			
		} catch (IOException | CanNotCreatePdfException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testEnterprisePayrollMergeI() {
		
			final Faker FAKER =Faker.instance(); 
			
			
			Map<String, Map<String, EnterprisePayrollEntry>> entries = new HashMap<>();
			Map<String, Map<String, EnterprisePayrollEntry>> ssEntries = new HashMap<>();
			
			Date firstDayOfMonth = AonDateUtils.getFirstDayOfMonth(new Date());
			Date lastDayOfMonth =  AonDateUtils.getLastDayOfMonth(firstDayOfMonth);
			
			EnterprisePayrollEntry ssEntry = new EnterprisePayrollEntry(
					EnterpriseEntryType.SEG_SOCIAL,
					FAKER.number().digits(12),
					FAKER.number().digits(11),
					AonDateUtils.getFirstDayOfMonth(new Date()),
					AonDateUtils.getLastDayOfMonth(new Date()),
					AonDateUtils.getLastDayOfMonth(new Date()),
					FAKER.name().fullName(), 
					"N\u00f3mina",
					FAKER.number().randomDouble(2, 0, 99999),
					FAKER.number().randomDouble(2, 0, 99999),
					FAKER.number().randomDouble(2, 0, 99999),
					FAKER.number().randomDouble(2, 0, 99999),
					FAKER.number().randomDouble(2, 0, 99999), 
					FAKER.number().randomDouble(2, 0, 99999),
					FAKER.number().randomDouble(2, 0, 99999), 
					FAKER.number().randomDouble(2, 0, 99999), 
					FAKER.number().randomDouble(2, 0, 99999)
			);

			ssEntries.put("Workplace",  Collections.singletonMap( String.format("0001-SALARY-%1$td-%1$tm-%1$tY", lastDayOfMonth ), ssEntry ));

			Optional<Map<String, Map<String, EnterprisePayrollEntry>>> mergedEntries = 
			new EnterprisePayroll(null, null, null, null, entries, ssEntries).getEntries();
			
			
			
			
	}

	@Test
	@Disabled
	public void testEnterprisePayroll2() {

		try {
			Map<String, Map<String, EnterprisePayrollEntry>> entries = new HashMap<>();
			Map<String, Map<String, EnterprisePayrollEntry>> ssEntries = new HashMap<>();;

			HashMap<String, EnterprisePayrollEntry> categoria1 = new HashMap<>();
			HashMap<String, EnterprisePayrollEntry> categoria2 = new HashMap<>();
			HashMap<String, EnterprisePayrollEntry> categoria3 = new HashMap<>();
			HashMap<String, EnterprisePayrollEntry> categoria4 = new HashMap<>();
			HashMap<String, EnterprisePayrollEntry> categoria5 = new HashMap<>();
			HashMap<String, EnterprisePayrollEntry> categoria6 = new HashMap<>();

			for (int i = 0; i < 4; i++) {
				String cat = "Categoria " + i;
				EnterprisePayrollEntry e = new EnterprisePayrollEntry(EnterpriseEntryType.AON_SYSTEM, null, null, null, null, null, null, "N\u00f3mina",
						null, null, null, null, null, null, 9999.99, 9.99, 9999.99);

				categoria1.put(cat, e);
				categoria2.put(cat, e);
				categoria3.put(cat, e);
			}
			
			for (int i = 0; i < 4; i++) {
				String cat = "cet " + i;
				EnterprisePayrollEntry e = new EnterprisePayrollEntry(EnterpriseEntryType.AON_SYSTEM, null, null, null, null, null, null, "N\u00f3mina",
						null, null, null, null, null, null, 9999.99, 9.999,9999.99);

				categoria1.put(cat, e);
				categoria2.put(cat, e);
				categoria3.put(cat, e);
			}
			
			for (int i = 0; i < 4; i++) {
				String cat = "Categoria " + i;
				EnterprisePayrollEntry e = new EnterprisePayrollEntry(EnterpriseEntryType.SEG_SOCIAL, null, null, null, null, null, null, "N\u00f3mina",
						null, null, null, null, null, null, 29999.99, null,9999.99);

				categoria3.put(cat, e);
				categoria4.put(cat, e);
				categoria5.put(cat, e);
				categoria6.put(cat, e);
			}
			
			entries.put("Amurrio",categoria1);
			entries.put("Vitoria",categoria2);
			entries.put("Murgia",categoria3);
			
			ssEntries.put("Amurrio",categoria4);
			ssEntries.put("Donosti",categoria5);
			ssEntries.put("Bilbao",categoria6);

			EnterprisePayroll payroll = new EnterprisePayroll(null, new Date(), "N\u00d3MINA EMPRESA", "AON SOLUTIONS S.L",	entries, ssEntries);
			PdfMaker.printEnterprisePayroll(payroll,new ByteArrayOutputStream(),Optional.of(new Locale("Es")));
			
		} catch (IOException | CanNotCreatePdfException e) {
			e.printStackTrace();
		}
	}
}
