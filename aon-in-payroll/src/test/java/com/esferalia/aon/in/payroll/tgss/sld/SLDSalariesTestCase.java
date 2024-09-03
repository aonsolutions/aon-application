package com.esferalia.aon.in.payroll.tgss.sld;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.watson.util.AonDateUtils;

import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.TestCalculationQuery;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.Calc;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.Period;

public class SLDSalariesTestCase {

	@Test
	@Disabled
	public void testGetSalaries() throws IOException, SegSocialException {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("/solutions/aon/seg/social/AyudaTFNMT.p12") ){
			
			String ccc = "11122534302";
			String regimen = "0111";
			
			byte certificateData [] = certificateInputStream.readAllBytes();
			
			Collection<Employee> ssEmployees = 
			SistemaRED.getEmployees(
			certificateData, 
			"123456", 
			"pkcs12",
			regimen,
			ccc
			);
			
			String nafs [] = ssEmployees.stream().map(e -> e.getNss()).skip(0).limit(5).toArray(String[]::new);
			
			Date firstDayOfMonth = AonDateUtils.getFirstDayOfMonth(new Date());
			Date from = AonDateUtils.add(firstDayOfMonth, Calendar.MONTH, -1);
			Date to = AonDateUtils.getLastDayOfMonth(from);
			
			Map<String, Map<String, Map<Period, Map<String, Calc>>>> typeCalcs =
			SistemaRED.getCalcByNAF(
			certificateData, 
			"123456", 
			"pkcs12", 
			ccc, 
			SistemaRED.Regime.GENERAL,  
			from, 
			to, 
			SistemaRED.LiquidationType.L00_NORMAL, 
			SistemaRED.LiquidationOrigin.TODAS,
			"00312622",
			nafs
			);
			
			typeCalcs.forEach((type, nafCalcs) -> nafCalcs.forEach(( naf, periodCalcs ) -> {
				Salary salary = SLDSalaries.getSalary(type, ccc, naf, periodCalcs);
				
				double totalDeductions = salary.getDeductions()
				.stream().collect(Collectors.summingDouble(d -> d.getAmount()));
				Asserts.assertEqualsDouble(salary.getEmployeeSSNumber(), salary.getTotalSSContributions(), totalDeductions);
				
				
				double totalCgpBase = salary.getContextData(ContextVariable.CGP_BASE.getName()
				, Collectors.summingDouble(s->Double.parseDouble(s)));
				Asserts.assertEqualsDouble(salary.getEmployeeSSNumber(), salary.getProfessionalContingenciesBase(), totalCgpBase);

				double totalCgcBase = salary.getContextData(ContextVariable.CGC_BASE.getName()
				, Collectors.summingDouble(s->Double.parseDouble(s)));
				Asserts.assertEqualsDouble(salary.getEmployeeSSNumber(), salary.getCommonContingenciesBase(), totalCgcBase);
				
				double totalCosts = salary.getCosts()
				.stream().collect(Collectors.summingDouble(d -> d.getAmount()));
				double totalBonus = salary.getBonuses()
				.stream().collect(Collectors.summingDouble(d -> d.getAmount()));
				Asserts.assertEqualsDouble(salary.getEmployeeSSNumber(), salary.getTotalEnterprise(), totalCosts -totalBonus);
				
				
			} ));
		}
	}
	
	@Test
	@Disabled
	public void testGetSalariesByPeriod() throws IOException, SegSocialException {
		try(InputStream certificateInputStream=TestCalculationQuery.class.getResourceAsStream("/solutions/aon/seg/social/AyudaTFNMT.p12") ){
			
			String ccc = "11122534302";
			
			byte certificateData [] = certificateInputStream.readAllBytes();
			
			Date firstDayOfMonth = AonDateUtils.getFirstDayOfMonth(new Date());
			Date from = AonDateUtils.add(firstDayOfMonth, Calendar.MONTH, -1);
			Date to = AonDateUtils.getLastDayOfMonth(from);
			
			Map<String, Map<String, Map<Period, Map<String, Calc>>>> typeCalcs =
			SistemaRED.getCalcByNAF(
			certificateData, 
			"123456", 
			"pkcs12", 
			ccc, 
			SistemaRED.Regime.GENERAL,  
			from, 
			to, 
			SistemaRED.LiquidationType.L00_NORMAL, 
			SistemaRED.LiquidationOrigin.TODAS,
			"111066333849" //nafs
			);
			
			typeCalcs.forEach((type, nafCalcs) -> nafCalcs.forEach(( naf, periodCalcs ) -> {
				
				Salary sumSalary = new Salary();
				sumSalary.setTotalEnterprise(0.00);
				sumSalary.setTotalSSContributions(0.00);
				sumSalary.setCommonContingenciesBase(0.00);
				sumSalary.setProfessionalContingenciesBase(0.00);
				
				periodCalcs.forEach((period, calcs) -> {
					
					if ( period == null )
						return;
					
					System.out.printf("%s: %tD..%tD\r\n", naf, period.getStartDate(), period.getEndDate());
					
					Salary salary = SLDSalaries.getSalary(type, ccc, naf, periodCalcs, period);
					
					// assert null against zero
					if ( salary.getTotalEnterprise() == null )
						salary.setTotalEnterprise(0.00);
					if ( salary.getTotalSSContributions() == null )
						salary.setTotalSSContributions(0.00);
					if ( salary.getCommonContingenciesBase() == null )
						salary.setCommonContingenciesBase(0.00);
					if ( salary.getProfessionalContingenciesBase() == null )
						salary.setProfessionalContingenciesBase(0.00);
					
					double totalDeductions = salary.getDeductions()
					.stream().collect(Collectors.summingDouble(d -> d.getAmount()));
					Asserts.assertEqualsDouble(salary.getEmployeeSSNumber(), salary.getTotalSSContributions(), totalDeductions);
					

					double totalCgpBase = salary.getContextData(ContextVariable.CGP_BASE.getName()
					, Collectors.summingDouble(s-> Double.parseDouble(s)));
					Asserts.assertEqualsDouble(salary.getEmployeeSSNumber(), salary.getProfessionalContingenciesBase(), totalCgpBase);

					double totalCgcBase = salary.getContextData(ContextVariable.CGC_BASE.getName()
					, Collectors.summingDouble(s->Double.parseDouble(s)));
					Asserts.assertEqualsDouble(salary.getEmployeeSSNumber(), salary.getCommonContingenciesBase(), totalCgcBase);
					
					double totalCosts = salary.getCosts()
					.stream().collect(Collectors.summingDouble(d -> d.getAmount()));
					double totalBonus = salary.getBonuses()
					.stream().collect(Collectors.summingDouble(d -> d.getAmount()));
					Asserts.assertEqualsDouble(salary.getEmployeeSSNumber(), salary.getTotalEnterprise(), totalCosts -totalBonus);
					
					sumSalary.setTotalEnterprise(sumSalary.getTotalEnterprise() + totalCosts -totalBonus);
					sumSalary.setTotalSSContributions(sumSalary.getTotalSSContributions() + totalDeductions);
					sumSalary.setCommonContingenciesBase(sumSalary.getCommonContingenciesBase() + totalCgcBase);
					sumSalary.setProfessionalContingenciesBase(sumSalary.getProfessionalContingenciesBase() + totalCgpBase);
				
				});
				
				Salary allSalary = SLDSalaries.getSalary(type, ccc, naf, periodCalcs);
				
				
				assertEquals(allSalary.getTotalEnterprise(), sumSalary.getTotalEnterprise(),naf);
				assertEquals(allSalary.getTotalSSContributions(), sumSalary.getTotalSSContributions(),naf);
				assertEquals(allSalary.getCommonContingenciesBase(), sumSalary.getCommonContingenciesBase(),naf);
				
				
				
			} ));
		}
	}

}
