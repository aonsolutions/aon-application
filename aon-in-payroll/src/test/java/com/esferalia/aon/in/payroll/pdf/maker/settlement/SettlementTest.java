package com.esferalia.aon.in.payroll.pdf.maker.settlement;

import static com.esferalia.aon.in.payroll.pdf.api.toolkit.NumberToolkit.random;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.StringToolkit.lorem;
import static com.esferalia.aon.in.payroll.pdf.maker.PdfMaker.printSettlement;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFDeduction;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFPayment;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.beans.Settlement;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.beans.Settlement.SettlementBuilder;
import com.github.javafaker.Faker;

public class SettlementTest {

	/**
	 * Standard settlement test with random values
	 */
	@Test
	public void settlementPrintTest() {

		try
		{

			//OutputStream out = new FileOutputStream("./Settlement.pdf");
			OutputStream out = new ByteArrayOutputStream();

			SettlementBuilder builder = new SettlementBuilder();
			Faker			  f		  = new Faker();

			String employeeName		 = f.zelda().character() + " " + f.zelda().game();
			String employeeNif		 = ("17284203-F");
			Date   employeeAntiquity = f.date().birthday(12, 1200);
			String employeeCategory	 = f.pokemon().name();

			String enterpriseName	 = f.lordOfTheRings().location() + " " + f.pokemon().name() + " S.L";
			String enterpriseAddress = f.address().fullAddress();
			String enterpriseNif	 = "13943076-X";

			String endCause	= f.book().title();
			Date   endDate	= f.date().birthday(12, 1200);

			int	accrualTotal   = f.number().numberBetween(1000, 99999);
			int	deductionTotal = f.number().numberBetween(1000, 9999);

			builder.setEmployeeName(employeeName.toUpperCase()).setEmployeeNIF(employeeNif)
					.setEmployeeAntiquity(employeeAntiquity).setEmployeeCategory(employeeCategory.toUpperCase())
					.setEnterpriseName(enterpriseName.toUpperCase()).setEnterpriseAddress(enterpriseAddress)
					.setEnterpriseNIF(enterpriseNif).setExistRepresentative(true).setEndCause(endCause.toUpperCase())
					.setEndDate(endDate).setPayments(new HashMap<>()).setDeductions(new HashMap<>())
					.setAccrualTotal(accrualTotal).setDeductionTotal(deductionTotal).setDate(new Date())
					.setLocation("Vitoria-gasteiz").setTotal(accrualTotal - deductionTotal);

			Settlement settlement = builder.build();
			printSettlement(out, settlement, Locale.forLanguageTag("Es"));
		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

	/**
	 * Testing when settlement is null
	 */
	@Test
	public void nullSettlementPrintTest() {
		try
		{
			//OutputStream out = new FileOutputStream("./nullSettlement.pdf");
			OutputStream out = new ByteArrayOutputStream();

			Settlement settle = null;
			printSettlement(out, settle, new Locale("Es"));
		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * Testing when OuputStream is null
	 */
	@Test
	public void nullOutputStreamPrintTest() {
		try
		{
			Settlement settle = null;
			printSettlement(null, settle, new Locale("Es"));
		} catch (CanNotCreatePdfException e)
		{
			String msg = e.getMessage();
			if (msg == null)
				fail("Unexpected exception: " + e);
			else if (!msg.equals("No output Stream given."))
				fail("Unexpected exception: " + e);

		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * Testing when Locale is null
	 */
	@Test
	public void nullLocalePrintTest() {
		try
		{
			//OutputStream out = new FileOutputStream("./nullLocaleSettlement.pdf");
			OutputStream out = new ByteArrayOutputStream();

			Settlement settle = null;
			printSettlement(out, settle, null);
		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception: " + e);
		}
	}

	/**
	 * Testing when no enterprise data is given
	 */
	@Test
	public void NoEnterpriseDataTest() {
		try
		{
			//OutputStream out = new FileOutputStream("./NoEnterpriseDataSettlement.pdf");
			OutputStream out = new ByteArrayOutputStream();

			SettlementBuilder builder = new SettlementBuilder();
			Faker			  f		  = new Faker();

			String employeeName		 = f.zelda().character() + " " + f.zelda().game();
			String employeeNif		 = ("17284203-F");
			Date   employeeAntiquity = f.date().birthday(12, 1200);
			String employeeCategory	 = f.pokemon().name();

			String endCause	= f.book().title();
			Date   endDate	= f.date().birthday(12, 1200);

			int	accrualTotal   = f.number().numberBetween(1000, 99999);
			int	deductionTotal = f.number().numberBetween(1000, 9999);

			builder.setEmployeeName(employeeName.toUpperCase()).setEmployeeNIF(employeeNif)
					.setEmployeeAntiquity(employeeAntiquity).setEmployeeCategory(employeeCategory.toUpperCase())
					.setExistRepresentative(true).setEndCause(endCause.toUpperCase()).setEndDate(endDate)
					.setPayments(new HashMap<>()).setDeductions(new HashMap<>()).setAccrualTotal(accrualTotal)
					.setDeductionTotal(deductionTotal).setDate(new Date()).setLocation("Vitoria-gasteiz")
					.setTotal(accrualTotal - deductionTotal);

			Settlement settlement = builder.build();
			printSettlement(out, settlement, Locale.forLanguageTag("Es"));
		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

	/**
	 * Testing when no employee data is given
	 */
	@Test
	public void NoEmployeeDataTest() {
		try
		{
			//OutputStream out = new FileOutputStream("./NoEmployeeDataSettlement.pdf");
			OutputStream out = new ByteArrayOutputStream();

			SettlementBuilder builder = new SettlementBuilder();
			Faker			  f		  = new Faker();

			String enterpriseName	 = f.lordOfTheRings().location() + " " + f.pokemon().name() + " S.L";
			String enterpriseAddress = f.address().fullAddress();
			String enterpriseNif	 = "13943076-X";

			String endCause	= f.book().title();
			Date   endDate	= f.date().birthday(12, 1200);

			int	accrualTotal   = f.number().numberBetween(1000, 99999);
			int	deductionTotal = f.number().numberBetween(1000, 9999);

			builder.setEnterpriseName(enterpriseName.toUpperCase()).setEnterpriseAddress(enterpriseAddress)
					.setEnterpriseNIF(enterpriseNif).setExistRepresentative(true).setEndCause(endCause.toUpperCase())
					.setEndDate(endDate).setPayments(new HashMap<>()).setDeductions(new HashMap<>())
					.setAccrualTotal(accrualTotal).setDeductionTotal(deductionTotal).setDate(new Date())
					.setLocation("Vitoria-gasteiz").setTotal(accrualTotal - deductionTotal);

			Settlement settlement = builder.build();
			printSettlement(out, settlement, Locale.forLanguageTag("Es"));
		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

	/**
	 * Testing when negative values are given
	 */
	@Test
	public void negativeValuesTest() {
		try
		{
			//OutputStream out = new FileOutputStream("./NegativeValuesSettlement.pdf");
			OutputStream out = new ByteArrayOutputStream();

			SettlementBuilder builder = new SettlementBuilder();
			Faker			  f		  = new Faker();

			String employeeName		 = f.zelda().character() + " " + f.zelda().game();
			String employeeNif		 = ("17284203-F");
			Date   employeeAntiquity = f.date().birthday(12, 1200);
			String employeeCategory	 = f.pokemon().name();

			String enterpriseName	 = f.lordOfTheRings().location() + " " + f.pokemon().name() + " S.L";
			String enterpriseAddress = f.address().fullAddress();
			String enterpriseNif	 = "13943076-X";

			String endCause	= f.book().title();
			Date   endDate	= f.date().birthday(12, 1200);

			int	accrualTotal   = -f.number().numberBetween(1000, 99999);
			int	deductionTotal = -f.number().numberBetween(1000, 9999);

			builder.setEmployeeName(employeeName.toUpperCase()).setEmployeeNIF(employeeNif)
					.setEmployeeAntiquity(employeeAntiquity).setEmployeeCategory(employeeCategory.toUpperCase())
					.setEnterpriseName(enterpriseName.toUpperCase()).setEnterpriseAddress(enterpriseAddress)
					.setEnterpriseNIF(enterpriseNif).setExistRepresentative(true).setEndCause(endCause.toUpperCase())
					.setEndDate(endDate).setPayments(new HashMap<>()).setDeductions(new HashMap<>())
					.setAccrualTotal(accrualTotal).setDeductionTotal(deductionTotal).setDate(new Date())
					.setLocation("Vitoria-gasteiz").setTotal(accrualTotal - deductionTotal);

			Settlement settlement = builder.build();
			printSettlement(out, settlement, Locale.forLanguageTag("Es"));
		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

	/**
	 * Testing extra large test
	 */
	@Test
	public void extraLargeTextsTest() {
		try
		{
			//OutputStream out = new FileOutputStream("./ExtraLargeTextsSettlement.pdf");
			OutputStream out = new ByteArrayOutputStream();

			SettlementBuilder builder = new SettlementBuilder();
			Faker			  f		  = new Faker();

			String employeeName = f.zelda().character() + " " + f.zelda().game() + f.zelda().character() + " "
					+ f.zelda().game() + f.zelda().character() + " " + f.zelda().game() + f.zelda().character() + " "
					+ f.zelda().game() + f.zelda().character() + " " + f.zelda().game() + f.zelda().character() + " "
					+ f.zelda().game();

			String employeeNif		 = lorem(1);
			Date   employeeAntiquity = f.date().birthday(12, 1200);
			String employeeCategory	 = lorem(1);

			String enterpriseName	 = f.lordOfTheRings().location() + " " + f.pokemon().name() + " S.L";
			String enterpriseAddress = lorem(1);
			String enterpriseNif	 = lorem(1);

			String endCause	= f.book().title();
			Date   endDate	= f.date().birthday(12, 1200);

			int	accrualTotal   = f.number().numberBetween(1000, 99999);
			int	deductionTotal = f.number().numberBetween(1000, 9999);

			builder.setEmployeeName(employeeName.toUpperCase()).setEmployeeNIF(employeeNif)
					.setEmployeeAntiquity(employeeAntiquity).setEmployeeCategory(employeeCategory.toUpperCase())
					.setEnterpriseName(enterpriseName.toUpperCase()).setEnterpriseAddress(enterpriseAddress)
					.setEnterpriseNIF(enterpriseNif).setExistRepresentative(true).setEndCause(endCause.toUpperCase())
					.setEndDate(endDate).setPayments(new HashMap<>()).setDeductions(new HashMap<>())
					.setAccrualTotal(accrualTotal).setDeductionTotal(deductionTotal).setDate(new Date())
					.setLocation("Vitoria-gasteiz").setTotal(accrualTotal - deductionTotal);

			Settlement settlement = builder.build();
			printSettlement(out, settlement, Locale.forLanguageTag("Es"));
		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

	/**
	 * Testing millionaire settle
	 */
	@Test
	public void MillionaireTest() {
		try
		{
			//OutputStream out = new FileOutputStream("./MillonaireSettlement.pdf");
			OutputStream out = new ByteArrayOutputStream();

			SettlementBuilder builder = new SettlementBuilder();
			Faker			  f		  = new Faker();

			String employeeName		 = f.zelda().character() + " " + f.zelda().game();
			String employeeNif		 = ("17284203-F");
			Date   employeeAntiquity = f.date().birthday(12, 1200);
			String employeeCategory	 = f.pokemon().name();

			String enterpriseName	 = f.lordOfTheRings().location() + " " + f.pokemon().name() + " S.L";
			String enterpriseAddress = f.address().fullAddress();
			String enterpriseNif	 = "13943076-X";

			String endCause	= f.book().title();
			Date   endDate	= f.date().birthday(12, 1200);

			int	accrualTotal   = f.number().numberBetween(1000, 9999999);
			int	deductionTotal = f.number().numberBetween(1000, 999999);

			// PAYMENTS
			Map<Integer, ArrayList<PDFPayment>> payments = new HashMap<Integer, ArrayList<PDFPayment>>();

			for (int i = 0; i < random(5) - 1; i++)
				payments.put((int) i + 1, new ArrayList<PDFPayment>());
			Set<Integer> keys = payments.keySet();

			for (Integer key : keys)
			{
				for (int i = 0; i < random(10) - 1; i++)
				{
					PDFPayment accrual = new PDFPayment(random(999999), "Descripcion por defecto.");
					payments.get(key).add(accrual);
				}
			}

			/// CREATE DEDUCTIONS
			Map<Integer, ArrayList<PDFDeduction>> deductions = new HashMap<Integer, ArrayList<PDFDeduction>>();

			deductions.put(1, new ArrayList<>());
			deductions.put(2, new ArrayList<>());
			deductions.put(3, new ArrayList<>());
			deductions.put(4, new ArrayList<>());
			deductions.put(5, new ArrayList<>());

			keys = deductions.keySet();
			for (Integer key : keys)
			{
				for (int i = 0; i < random(3); i++)
				{
					PDFDeduction deduction = new PDFDeduction(random(100000), "Descripcion por defecto", random(100));
					deductions.get(key).add(deduction);
				}
			}
			builder.setEmployeeName(employeeName.toUpperCase()).setEmployeeNIF(employeeNif)
					.setEmployeeAntiquity(employeeAntiquity).setEmployeeCategory(employeeCategory.toUpperCase())
					.setEnterpriseName(enterpriseName.toUpperCase()).setEnterpriseAddress(enterpriseAddress)
					.setEnterpriseNIF(enterpriseNif).setExistRepresentative(true).setEndCause(endCause.toUpperCase())
					.setEndDate(endDate).setPayments(payments).setDeductions(deductions).setAccrualTotal(accrualTotal)
					.setDeductionTotal(deductionTotal).setDate(new Date()).setLocation("Vitoria-gasteiz")
					.setTotal(accrualTotal - deductionTotal);

			Settlement settlement = builder.build();
			printSettlement(out, settlement, Locale.forLanguageTag("Es"));
		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

	/**
	 * Testing a big amount of payments
	 */
	@Test
	public void toManyPaymentsTest() {
		try
		{
			//OutputStream out = new FileOutputStream("./toManyPaymentsSettlement.pdf");
			OutputStream out = new ByteArrayOutputStream();
			
			SettlementBuilder builder = new SettlementBuilder();
			Faker			  f		  = new Faker();

			String employeeName		 = f.zelda().character() + " " + f.zelda().game();
			String employeeNif		 = ("17284203-F");
			Date   employeeAntiquity = f.date().birthday(12, 1200);
			String employeeCategory	 = f.pokemon().name();

			String enterpriseName	 = f.lordOfTheRings().location() + " " + f.pokemon().name() + " S.L";
			String enterpriseAddress = f.address().fullAddress();
			String enterpriseNif	 = "13943076-X";

			String endCause	= f.book().title();
			Date   endDate	= f.date().birthday(12, 1200);

			int	accrualTotal   = f.number().numberBetween(1000, 9999999);
			int	deductionTotal = f.number().numberBetween(1000, 999999);

			// PAYMENTS
			Map<Integer, ArrayList<PDFPayment>> payments = new HashMap<Integer, ArrayList<PDFPayment>>();

			for (int i = 0; i < random(5) - 1; i++)
				payments.put((int) i + 1, new ArrayList<PDFPayment>());
			Set<Integer> keys = payments.keySet();

			for (Integer key : keys)
			{
				for (int i = 0; i < random(10) - 1; i++)
				{
					PDFPayment accrual = new PDFPayment(random(999999), "Descripcion por defecto.");
					payments.get(key).add(accrual);
				}
			}

			/// CREATE DEDUCTIONS
			Map<Integer, ArrayList<PDFDeduction>> deductions = new HashMap<Integer, ArrayList<PDFDeduction>>();

			deductions.put(1, new ArrayList<>());
			deductions.put(2, new ArrayList<>());
			deductions.put(3, new ArrayList<>());
			deductions.put(4, new ArrayList<>());
			deductions.put(5, new ArrayList<>());

			keys = deductions.keySet();
			for (Integer key : keys)
			{
				for (int i = 0; i < random(3); i++)
				{
					PDFDeduction deduction = new PDFDeduction(random(100000), "Descripcion por defecto", random(100));
					deductions.get(key).add(deduction);
				}
			}
			builder.setEmployeeName(employeeName.toUpperCase()).setEmployeeNIF(employeeNif)
					.setEmployeeAntiquity(employeeAntiquity).setEmployeeCategory(employeeCategory.toUpperCase())
					.setEnterpriseName(enterpriseName.toUpperCase()).setEnterpriseAddress(enterpriseAddress)
					.setEnterpriseNIF(enterpriseNif).setExistRepresentative(true).setEndCause(endCause.toUpperCase())
					.setEndDate(endDate).setPayments(payments).setDeductions(deductions).setAccrualTotal(accrualTotal)
					.setDeductionTotal(deductionTotal).setDate(new Date()).setLocation("Vitoria-gasteiz")
					.setTotal(accrualTotal - deductionTotal);

			Settlement settlement = builder.build();
			printSettlement(out, settlement,
					Locale.forLanguageTag("Es"));
		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

	/**
	 * Testing a strange characters
	 */
	@Test
	public void strangeCharactersTest() {
		try
		{
			//OutputStream out = new FileOutputStream("./StrangeCharactersSettlement.pdf");
			OutputStream out = new ByteArrayOutputStream();

			String strangeCharacters = "\u00E1" + "\u00E2" + "\u00E4" + "\u00E5" + "\u00E6";

			SettlementBuilder builder = new SettlementBuilder();
			Faker			  f		  = new Faker();

			String employeeName		 = f.zelda().character() + " " + f.zelda().game();
			String employeeNif		 = ("17284203-F");
			Date   employeeAntiquity = f.date().birthday(12, 1200);
			String employeeCategory	 = f.pokemon().name();

			String enterpriseName	 = f.lordOfTheRings().location() + " " + f.pokemon().name() + " S.L";
			String enterpriseAddress = f.address().fullAddress();
			String enterpriseNif	 = "13943076-X";

			String endCause	= f.book().title();
			Date   endDate	= f.date().birthday(12, 1200);

			int	accrualTotal   = f.number().numberBetween(1000, 9999999);
			int	deductionTotal = f.number().numberBetween(1000, 999999);

			// PAYMENTS
			Map<Integer, ArrayList<PDFPayment>> payments = new HashMap<Integer, ArrayList<PDFPayment>>();

			for (int i = 0; i < random(5) - 1; i++)
				payments.put((int) i + 1, new ArrayList<PDFPayment>());
			Set<Integer> keys = payments.keySet();

			for (Integer key : keys)
			{
				for (int i = 0; i < random(10) - 1; i++)
				{
					PDFPayment accrual = new PDFPayment(random(999999), strangeCharacters);
					payments.get(key).add(accrual);
				}
			}

			/// CREATE DEDUCTIONS
			Map<Integer, ArrayList<PDFDeduction>> deductions = new HashMap<Integer, ArrayList<PDFDeduction>>();

			deductions.put(1, new ArrayList<>());
			deductions.put(2, new ArrayList<>());
			deductions.put(3, new ArrayList<>());
			deductions.put(4, new ArrayList<>());
			deductions.put(5, new ArrayList<>());

			keys = deductions.keySet();
			for (Integer key : keys)
			{
				for (int i = 0; i < random(3); i++)
				{
					PDFDeduction deduction = new PDFDeduction(random(100000), strangeCharacters, random(100));
					deductions.get(key).add(deduction);
				}
			}
			builder.setEmployeeName(employeeName.toUpperCase()).setEmployeeNIF(employeeNif)
					.setEmployeeAntiquity(employeeAntiquity).setEmployeeCategory(employeeCategory.toUpperCase())
					.setEnterpriseName(enterpriseName.toUpperCase()).setEnterpriseAddress(enterpriseAddress)
					.setEnterpriseNIF(enterpriseNif).setExistRepresentative(true).setEndCause(endCause.toUpperCase())
					.setEndDate(endDate).setPayments(payments).setDeductions(deductions).setAccrualTotal(accrualTotal)
					.setDeductionTotal(deductionTotal).setDate(new Date()).setLocation("Vitoria-gasteiz")
					.setTotal(accrualTotal - deductionTotal);

			Settlement settlement = builder.build();
			printSettlement(out, settlement, Locale.forLanguageTag("Es"));
		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

	/**
	 * Testing null parameters
	 */
	@Test
	public void nullParamsTest() {
		try
		{
			//OutputStream out = new FileOutputStream("./NullParamsSettlement.pdf");
			OutputStream out = new ByteArrayOutputStream();

			SettlementBuilder builder = new SettlementBuilder();
			Faker			  f		  = new Faker();

			String employeeName		 = null;
			String employeeNif		 = null;
			Date   employeeAntiquity = null;
			String employeeCategory	 = null;

			String enterpriseName	 = null;
			String enterpriseAddress = null;
			String enterpriseNif	 = null;

			String endCause	= null;
			Date   endDate	= null;

			Double accrualTotal	  = 0d;
			Double deductionTotal = 0d;

			// PAYMENTS
			Map<Integer, ArrayList<PDFPayment>>	  payments	 = null;
			Map<Integer, ArrayList<PDFDeduction>> deductions = null;

			builder.setEmployeeName(employeeName).setEmployeeNIF(employeeNif).setEmployeeAntiquity(employeeAntiquity)
					.setEmployeeCategory(employeeCategory).setEnterpriseName(enterpriseName)
					.setEnterpriseAddress(enterpriseAddress).setEnterpriseNIF(enterpriseNif)
					.setExistRepresentative(true).setEndCause(endCause).setEndDate(endDate).setPayments(payments)
					.setDeductions(deductions).setAccrualTotal(accrualTotal).setDeductionTotal(deductionTotal)
					.setDate(null).setLocation(null).setTotal(accrualTotal - deductionTotal);

			Settlement settlement = builder.build();
			printSettlement(out, settlement, Locale.forLanguageTag("Es"));
		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

	/**
	 * Testing null parameters even inside parameters
	 */
	@Test
	public void nullParamsTest2() {
		try
		{
			//OutputStream out = new FileOutputStream("./NullParamsCompleteSettlement.pdf");
			OutputStream out = new ByteArrayOutputStream();

			SettlementBuilder builder = new SettlementBuilder();
			Faker			  f		  = new Faker();

			String employeeName		 = null;
			String employeeNif		 = null;
			Date   employeeAntiquity = null;
			String employeeCategory	 = null;

			String enterpriseName	 = null;
			String enterpriseAddress = null;
			String enterpriseNif	 = null;

			String endCause	= null;
			Date   endDate	= null;

			Double accrualTotal	  = 0d;
			Double deductionTotal = 0d;

			// PAYMENTS
			Map<Integer, ArrayList<PDFPayment>> payments = new HashMap<Integer, ArrayList<PDFPayment>>();

			for (int i = 0; i < random(5) - 1; i++)
				payments.put((int) random(61), new ArrayList<PDFPayment>());

			Set<Integer> keys = payments.keySet();

			for (Integer key : keys)
			{
				for (int i = 0; i < random(10) - 1; i++)
				{
					PDFPayment accrual = new PDFPayment(random(9999999), null);
					payments.get(key).add(accrual);

				}
				payments.get(key).add(null);
			}

			/// CREATE DEDUCTIONS
			Map<Integer, ArrayList<PDFDeduction>> deductions = new HashMap<Integer, ArrayList<PDFDeduction>>();

			deductions.put(1, new ArrayList<>());
			deductions.put(2, new ArrayList<>());
			deductions.put(3, new ArrayList<>());
			deductions.put(4, new ArrayList<>());
			deductions.put(5, new ArrayList<>());

			keys = deductions.keySet();
			for (Integer key : keys)
			{
				for (int i = 0; i < random(3); i++)
				{
					PDFDeduction deduction = new PDFDeduction(null, null, null);
					deductions.get(key).add(deduction);

				}
				deductions.get(key).add(null);
			}

			builder.setEmployeeName(employeeName).setEmployeeNIF(employeeNif).setEmployeeAntiquity(employeeAntiquity)
					.setEmployeeCategory(employeeCategory).setEnterpriseName(enterpriseName)
					.setEnterpriseAddress(enterpriseAddress).setEnterpriseNIF(enterpriseNif)
					.setExistRepresentative(true).setEndCause(endCause).setEndDate(endDate).setPayments(payments)
					.setDeductions(deductions).setAccrualTotal(accrualTotal).setDeductionTotal(deductionTotal)
					.setDate(null).setLocation(null).setTotal(accrualTotal - deductionTotal);

			Settlement settlement = builder.build();
			printSettlement(out, settlement, Locale.forLanguageTag("Es"));
		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

	/**
	 * Testing unknown CRAs
	 */
	@Test
	public void UnknownCraTest() {
		try
		{
			//OutputStream out = new FileOutputStream("./UnknownCRASettlement.pdf");
			OutputStream out = new ByteArrayOutputStream();

			SettlementBuilder builder = new SettlementBuilder();
			Faker			  f		  = new Faker();

			String employeeName		 = f.zelda().character() + " " + f.zelda().game();
			String employeeNif		 = ("17284203-F");
			Date   employeeAntiquity = f.date().birthday(12, 1200);
			String employeeCategory	 = f.pokemon().name();

			String enterpriseName	 = f.lordOfTheRings().location() + " " + f.pokemon().name() + " S.L";
			String enterpriseAddress = f.address().fullAddress();
			String enterpriseNif	 = "13943076-X";

			String endCause	= f.book().title();
			Date   endDate	= f.date().birthday(12, 1200);

			int	accrualTotal   = f.number().numberBetween(1000, 9999999);
			int	deductionTotal = f.number().numberBetween(1000, 999999);

			// PAYMENTS
			Map<Integer, ArrayList<PDFPayment>> payments = new HashMap<Integer, ArrayList<PDFPayment>>();

			for (int i = 0; i < random(5) - 1; i++)
				payments.put(-99, new ArrayList<PDFPayment>());
			Set<Integer> keys = payments.keySet();

			for (Integer key : keys)
			{
				for (int i = 0; i < random(10) - 1; i++)
				{
					PDFPayment accrual = new PDFPayment(random(999999), "Descripcion por defecto.");
					payments.get(key).add(accrual);
				}
			}

			/// CREATE DEDUCTIONS
			Map<Integer, ArrayList<PDFDeduction>> deductions = new HashMap<Integer, ArrayList<PDFDeduction>>();

			deductions.put(1, new ArrayList<>());
			deductions.put(2, new ArrayList<>());
			deductions.put(3, new ArrayList<>());
			deductions.put(4, new ArrayList<>());
			deductions.put(5, new ArrayList<>());

			keys = deductions.keySet();
			for (Integer key : keys)
			{
				for (int i = 0; i < random(3); i++)
				{
					PDFDeduction deduction = new PDFDeduction(random(100000), "Descripcion por defecto", random(100));
					deductions.get(key).add(deduction);
				}
			}
			builder.setEmployeeName(employeeName.toUpperCase()).setEmployeeNIF(employeeNif)
					.setEmployeeAntiquity(employeeAntiquity).setEmployeeCategory(employeeCategory.toUpperCase())
					.setEnterpriseName(enterpriseName.toUpperCase()).setEnterpriseAddress(enterpriseAddress)
					.setEnterpriseNIF(enterpriseNif).setExistRepresentative(true).setEndCause(endCause.toUpperCase())
					.setEndDate(endDate).setPayments(payments).setDeductions(deductions).setAccrualTotal(accrualTotal)
					.setDeductionTotal(deductionTotal).setDate(new Date()).setLocation("Vitoria-gasteiz")
					.setTotal(accrualTotal - deductionTotal);

			Settlement settlement = builder.build();
			printSettlement(out, settlement, Locale.forLanguageTag("Es"));
		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception: " + e);
		}
	}

}
