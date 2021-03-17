package com.esferalia.aon.in.payroll.pdf.creators.settlement;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.creators.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.creators.settlement.beans.Settlement;
import com.esferalia.aon.in.payroll.pdf.creators.settlement.beans.Settlement.SettlementBuilder;
import com.github.javafaker.Faker;

public class SettlementTest {

	@Test
	public void settlementPrintTest(){
		try {
		SettlementBuilder builder = new SettlementBuilder();
		Faker f = new Faker();
		
		String employee_name 		= f.zelda().character() + " " +  f.zelda().game();
		String employee_nif  		= ("17284203-F");
		Date   employee_antiquity 	= f.date().birthday(12, 1200);
		String employee_category    = f.pokemon().name();
		
		String enterprise_name 		= f.lordOfTheRings().location() + " " + f.pokemon().name() + " S.L";
		String enterprise_address	= f.address().fullAddress();
		String enterprise_nif		= "13943076-X";
		
		String end_cause 			= f.book().title();
		Date   end_date				= f.date().birthday(12, 1200);
		
		int accrual_total 	 = f.number().numberBetween(1000, 99999);
		int deduction_total  = f.number().numberBetween(1000, 9999);
		
		builder
		.setEmployee_name				(employee_name.toUpperCase())
		.setEmployee_NIF				(employee_nif)
		.setEmployee_antiquity			(employee_antiquity)
		.setEmployee_category			(employee_category.toUpperCase())
		.setEnterprise_name				(enterprise_name.toUpperCase())
		.setEnterprise_address			(enterprise_address)
		.setEnterprise_NIF				(enterprise_nif)
		.setExist_representative		(true)
		.setEnd_cause					(end_cause.toUpperCase())
		.setEnd_date					(end_date)
		.setAccruals					(new ArrayList<>())
		.setDeductions					(new ArrayList<>())
		.setAccrual_total				(accrual_total)
		.setDeduction_total				(deduction_total)
		.setDate						(new Date())
		.setLocation					("Vitoria-gasteiz")
		.setTotal						(accrual_total - deduction_total)
		;
		
		Settlement settlement = builder.build();
		PdfMaker.print_settlement(new FileOutputStream("./Settlement.pdf"), settlement, Locale.forLanguageTag("Es"));
		}catch(FileNotFoundException | CanNotCreatePdfException e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

}
