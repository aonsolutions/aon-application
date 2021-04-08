package com.esferalia.aon.in.payroll.pdf.maker.settlement;

import static com.esferalia.aon.in.payroll.pdf.maker.PdfMaker.printSettlement;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.beans.Settlement;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.beans.Settlement.SettlementBuilder;
import com.github.javafaker.Faker;

public class SettlementTest {

	@Test
	public void settlementPrintTest() {
		try
		{
			SettlementBuilder builder = new SettlementBuilder();
			Faker			  f		  = new Faker();

			String employee_name	  = f.zelda().character() + " " + f.zelda().game();
			String employee_nif		  = ("17284203-F");
			Date   employee_antiquity = f.date().birthday(12, 1200);
			String employee_category  = f.pokemon().name();

			String enterprise_name	  = f.lordOfTheRings().location() + " " + f.pokemon().name() + " S.L";
			String enterprise_address = f.address().fullAddress();
			String enterprise_nif	  = "13943076-X";

			String end_cause = f.book().title();
			Date   end_date	 = f.date().birthday(12, 1200);

			int	accrual_total	= f.number().numberBetween(1000, 99999);
			int	deduction_total	= f.number().numberBetween(1000, 9999);

			builder.setEmployeeName(employee_name.toUpperCase()).setEmployeeNIF(employee_nif)
					.setEmployeeAntiquity(employee_antiquity).setEmployeeCategory(employee_category.toUpperCase())
					.setEnterpriseName(enterprise_name.toUpperCase()).setEnterpriseAddress(enterprise_address)
					.setEnterpriseNIF(enterprise_nif).setExistRepresentative(true).setEndCause(end_cause.toUpperCase())
					.setEndDate(end_date).setAccruals(new HashMap<>()).setDeductions(new HashMap<>())
					.setAccrualTotal(accrual_total).setDeductionTotal(deduction_total).setDate(new Date())
					.setLocation("Vitoria-gasteiz").setTotal(accrual_total - deduction_total);

			Settlement settlement = builder.build();
			printSettlement(new FileOutputStream("./Settlement.pdf"), settlement, Locale.forLanguageTag("Es"));
		} catch (FileNotFoundException | CanNotCreatePdfException e)
		{
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

}
