package com.esferalia.aon.in.payroll.pdf.maker.payroll;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.ContingencyBases;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.ContingencyBases.ContingencyBasesBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll.DefaultPayrollBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFDeduction;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFPayment;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PayrollTypes;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.github.javafaker.Faker;

public class DetailedClassicPayrollTest {
	
	/**
	 * Generate a random ContigencyBases object
	 * @return random bases.
	 */
	private static ContingencyBases generateRandomContingencies() {
		ContingencyBasesBuilder builder = new ContingencyBasesBuilder();

		builder.setMonthlyAmount(Optional.of(9999.99)).setExtraProrationAmount(Optional.of(9999.99))
				.setCommonContBase(Optional.of(9999.99)).setCommonContType(Optional.of(99.99))
				.setCommonContApEnterprise(Optional.of(9999.99)).setProfessionalContBase(Optional.of(9999.99))
				.setAtEpType(Optional.of(99.99)).setAtEpApEnterprise(Optional.of(9999.99))
				.setUnemploymentType(Optional.of(99.99)).setUnemploymentApEnterprise(Optional.of(9999.99))
				.setProfesFormType(Optional.of(99.99)).setProfesFormApEnterprise(Optional.of(9999.99))
				.setFogasaType(Optional.of(99.99)).setFogasaApEnterprise(Optional.of(9999.99))
				.setForceMajeureBase(Optional.of(9999.99)).setForceMajeureType(Optional.of(99.99))
				.setForceMajeureApEnterprise(Optional.of(9999.99)).setNoStructBase(Optional.of(9999.99))
				.setNoStructType(Optional.of(99.99)).setNoStructApEnterprise(Optional.of(9999.99))
				.setIrpfEsp(Optional.of(99999.99)).setIrpfRetribDiner(Optional.of(99999.99))
				.setTotal(Optional.of(99999.99));
		
		return builder.build();
	}
	
	/**
	 * Generate random payments
	 * @return Map of random payments ordered by CRA
	 */
	private static Map<Integer, ArrayList<PDFPayment>> generateRandomPayments(){
		Map<Integer, ArrayList<PDFPayment>> payments = new HashMap<Integer, ArrayList<PDFPayment>>();
		Faker f = new Faker();
		
		for (int i = 0; i < randomBetweenZeroAnd(10); i++)
			payments.put((int) randomBetweenZeroAnd(61), new ArrayList<PDFPayment>());
		
		Set<Integer> keys = payments.keySet();
		for (Integer key : keys)
			for (int i = 0; i < randomBetweenZeroAnd(5) - 1; i++)
				payments.get(key).add(new PDFPayment(randomBetweenZeroAnd(999), f.pokemon().location() + " " + f.zelda().game()));
		
		return payments;
	}
	
	/**
	 * Generate random deductions
	 * @return Map of random deductions ordered by type
	 */
	private static Map<Integer, ArrayList<PDFDeduction>> generateRandomDeductions(){
		Map<Integer, ArrayList<PDFDeduction>> deductions = new HashMap<Integer, ArrayList<PDFDeduction>>();
		Faker f = new Faker();
		
		for (int i = 1; i <= 5 - 1; i++)
			deductions.put((int) i , new ArrayList<PDFDeduction>());
		
		Set<Integer> keys = deductions.keySet();
		DeductionType[] types = DeductionType.class.getEnumConstants();
		for (Integer key : keys) {			
			for (int i = 0; i < randomBetweenZeroAnd(10) - 1; i++) {
				deductions.get(key)
				.add(new PDFDeduction(randomBetweenZeroAnd(100000),
						f.color().name(),
						f.pokemon().location() + " " + f.zelda().character(),
						randomBetweenZeroAnd(100), types[f.number().numberBetween(0, types.length - 1)]));
			}
		}
		
		return deductions;
	}
	

	/**
	 * Returns a random number between 0 and max.
	 * @param max - Maximum number 
	 * @return random number.
	 */
	public static double randomBetweenZeroAnd(double max) {
		Double random = 1 + Math.random() * (max - 1);
		return random;
	}
	

	@Test
	public void randomPrintTest() {

		System.out.println("\n\n-----------------------------------");
		System.out.println(" PAYROLL CREATOR");
		System.out.println("-----------------------------------");
		System.out.println("\n Starting.....");

		DefaultPayrollBuilder builder = new DefaultPayrollBuilder();
		Map<Integer, ArrayList<PDFPayment>> payments = generateRandomPayments();
		ArrayList<PDFPayment> pays = payments.getOrDefault(1, new ArrayList<>());
		pays.add(new PDFPayment(2000d, "Salario base"));
		pays.add(new PDFPayment(1000d, "Salario anual"));
		payments.put(1, pays);
		Map<Integer, ArrayList<PDFDeduction>> de = generateRandomDeductions();
		
		ContingencyBases bases = generateRandomContingencies();		
		builder.setEnterprise(Optional.of("DEMO EMPRESA HERMANOS DE LA PAZ Y ASOCIADOS S.L"))
				.setAddress(Optional.of("Calle Duque de Wellington, 522 (01010)"))
				.setAddress2(Optional.of("Vitoria-Gasteiz"))
				.setCif(Optional.of("58595859M"))
				.setCcc(Optional.of("8935713546370"))
				.setEmployee(Optional.of("Iker Gónzalez Con Apellido Inventado de la Fuente Pérez Abech"))
				.setNif(Optional.of("47227931-F"))
				.setNss(Optional.of("11004767999"))
				.setProfessionalGroup(Optional.of("Director"))
				.setQuotationGroup(Optional.of("01"))
				.setAntiquity(Optional.of(new Date()))
				.setLiquidPeriodStart(Optional.of(new Date()))
				.setLiquidPeriodEnd(Optional.of(new Date()))
				.setTotalDays(Optional.of(30))
				.setAccruals(Optional.of(payments))
				.setDeductions(Optional.of(de))
				.setAccrualTotal(Optional.of(99999.99))
				.setDeductionTotal(Optional.of(9999.99))
				.setPayrollTotal(Optional.of(9999.99))
				.setTotalSSContributions(Optional.of(9999.99))
				.setPayrollType(Optional.of(PayrollTypes.Type.EXTRAS))
				.setContingencies(Optional.of(bases));

		try
		{
			OutputStream out = new FileOutputStream("./PayrollRandomClassic.pdf");
			// OutputStream out = new ByteArrayOutputStream();

			System.out.println(" Printing PDF file..... \n");
			PdfMaker.printDefaultClassicPayroll(out, builder.build(),DetailedClassicPayrollTest.class.getResourceAsStream("logo.png"), new Locale("Es"));
			System.out.println(" >> DONE.");
		} catch (CanNotCreatePdfException e)
		{
			e.printStackTrace();
			fail("Can not create the payroll");
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			fail("IOException");
		}
	}

	@Test
	public void CraZeroPrintTest() {

		System.out.println("\n\n-----------------------------------");
		System.out.println(" PAYROLL [CRA - 00]");
		System.out.println("-----------------------------------");

		DefaultPayrollBuilder builder = new DefaultPayrollBuilder();
		Map<Integer, ArrayList<PDFPayment>> ac = new HashMap<Integer, ArrayList<PDFPayment>>();

		Faker f = new Faker();
		ac.put(0, new ArrayList<PDFPayment>());
		for (int i = 0; i < randomBetweenZeroAnd(10) - 1; i++)
			ac.get(0).add(new PDFPayment(randomBetweenZeroAnd(999), f.zelda().game() + " " + f.pokemon().location() + " " + f.book().title()));
		

		ContingencyBasesBuilder conBuilder = new ContingencyBasesBuilder();
		builder.setEnterprise(Optional.of("DEMO EMPRESA HERMANOS DE LA PAZ Y ASOCIADOS S.L"))
				.setAddress(Optional.of("Calle Duque de Wellington, 522 (01010)"))
				.setAddress2(Optional.of("Vitoria-Gazteiz"))
				.setCif(Optional.of("58595859M"))
				.setCcc(Optional.of("8935713546370"))
				.setEmployee(Optional.of("Iker Gónzalez Con Apellido Inventado de la Fuente Pérez Abech"))
				.setNif(Optional.of("47227931-F"))
				.setNss(Optional.of("11004767999"))
				.setProfessionalGroup(Optional.of("Director"))
				.setQuotationGroup(Optional.of("01"))
				.setAntiquity(Optional.of(new Date()))
				.setLiquidPeriodStart(Optional.of(new Date()))
				.setLiquidPeriodEnd(Optional.of(new Date()))
				.setTotalDays(Optional.of(30))
				.setAccruals(Optional.of(ac))
				.setDeductions(Optional.of(new HashMap<>()))
				.setAccrualTotal(Optional.of(99999.99))
				.setDeductionTotal(Optional.of(9999.99))
				.setPayrollTotal(Optional.of(9999.99))
				.setPayrollType(Optional.of(PayrollTypes.Type.EXTRAS))
				.setContingencies(Optional.of(conBuilder.build()));

		try
		{
			System.out.println(" Printing PDF file..... \n");
			OutputStream out = new FileOutputStream("./PayrollCraZeroClassic.pdf");
			// OutputStream out = new ByteArrayOutputStream();

			PdfMaker.printDefaultClassicPayroll(out, builder.build(),DetailedClassicPayrollTest.class.getResourceAsStream("logo.png"), new Locale("Es"));
			System.out.println(" >> DONE.");
		} catch (CanNotCreatePdfException e)
		{
			e.printStackTrace();
			fail("Can not create the payroll");
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			fail("IOException");
		}
	}	

	
	@Test
	public void LargePrintTest() {

		System.out.println("\n\n-----------------------------------");
		System.out.println(" PAYROLL [Large version]");
		System.out.println("-----------------------------------");

		DefaultPayrollBuilder builder = new DefaultPayrollBuilder();
		Map<Integer, ArrayList<PDFPayment>> ac = new HashMap<Integer, ArrayList<PDFPayment>>();
		Faker f = new Faker();

		ac.put(0, new ArrayList<PDFPayment>());
		for (int i = 0; i < randomBetweenZeroAnd(10) - 1; i++)
			ac.get(0).add(
					new PDFPayment(
							randomBetweenZeroAnd(999),
							f.zelda().game() + " " + f.pokemon().location() +
							"  " + f.book().title() +  
							"  " + f.book().title() +  
							"  " + f.book().title() +
							"  " + f.book().title() +
							"  " + f.book().title() +
							"  " + f.book().title() +
							"  " + f.book().title() +
							"  " + f.book().title() +
							"  " + f.book().title() 
							) 
					);
		
		
		Map<Integer, ArrayList<PDFDeduction>> de = new HashMap<Integer, ArrayList<PDFDeduction>>();

		de.put(1, new ArrayList<PDFDeduction>());
		de.put(2, new ArrayList<PDFDeduction>());
		de.put(3, new ArrayList<PDFDeduction>());
		de.put(4, new ArrayList<PDFDeduction>());
		de.put(5, new ArrayList<PDFDeduction>());
		
		for (int i = 0; i < randomBetweenZeroAnd(10) - 1; i++) {
			PDFDeduction deduction = new PDFDeduction(
					randomBetweenZeroAnd(999),	
					f.color().name(),
					f.zelda().game() + " " + f.pokemon().location() +
					"  " + f.book().title() +  
					"  " + f.book().title() +  
					"  " + f.book().title() +
					"  " + f.book().title() +
					"  " + f.book().title() +
					"  " + f.book().title() +
					"  " + f.book().title() +
					"  " + f.book().title() +
					"  " + f.book().title(),
					randomBetweenZeroAnd(99)
				);
			
			de.get(1).add(deduction);
			de.get(2).add(deduction);
			de.get(3).add(deduction);
			de.get(4).add(deduction);
			de.get(5).add(deduction);
		}

		ContingencyBasesBuilder conBuilder = new ContingencyBasesBuilder();
		builder.setEnterprise(Optional.of("DEMO EMPRESA HERMANOS DE LA PAZ Y ASOCIADOS S.L"))
				.setAddress(Optional.of("Calle Duque de Wellington, 522 (01010)"))
				.setAddress2(Optional.of("Vitoria-Gazteiz"))
				.setCif(Optional.of("58595859M"))
				.setCcc(Optional.of("8935713546370"))
				.setEmployee(Optional.of("Iker Gónzalez Con Apellido Inventado de la Fuente Pérez Abech"))
				.setNif(Optional.of("47227931-F"))
				.setNss(Optional.of("11004767999"))
				.setProfessionalGroup(Optional.of("Director"))
				.setQuotationGroup(Optional.of("01"))
				.setAntiquity(Optional.of(new Date()))
				.setLiquidPeriodStart(Optional.of(new Date()))
				.setLiquidPeriodEnd(Optional.of(new Date()))
				.setTotalDays(Optional.of(30))
				.setAccruals(Optional.of(ac))
				.setDeductions(Optional.of(de))
				.setAccrualTotal(Optional.of(99999.99))
				.setDeductionTotal(Optional.of(9999.99))
				.setPayrollTotal(Optional.of(9999.99))
				.setPayrollType(Optional.of(PayrollTypes.Type.EXTRAS))
				.setContingencies(Optional.of(conBuilder.build()));

		try
		{
			System.out.println(" Printing PDF file..... \n");
			OutputStream out = new FileOutputStream("./PayrollLargeClassic.pdf");
			// OutputStream out = new ByteArrayOutputStream();

			PdfMaker.printDefaultClassicPayroll(out, builder.build(),DetailedClassicPayrollTest.class.getResourceAsStream("logo.png"), new Locale("Es"));
			System.out.println(" >> DONE.");
		} catch (CanNotCreatePdfException e)
		{
			e.printStackTrace();
			fail("Can not create the payroll");
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			fail("IOException");
		}
	}	
}
