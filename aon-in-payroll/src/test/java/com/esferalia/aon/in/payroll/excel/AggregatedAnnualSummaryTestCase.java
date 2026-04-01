package com.esferalia.aon.in.payroll.excel;

import static com.esferalia.aon.in.payroll.excel.AggregatedAnnualSummary.SummaryType.QUARTERLY;
import static com.esferalia.aon.in.payroll.excel.AggregatedAnnualSummary.SummaryType.MONTHLY;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.Salary.Deduction;
import com.esferalia.aon.occam.api.model.Salary.Payment;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.PaymentType;
import com.github.javafaker.Faker;

public class AggregatedAnnualSummaryTestCase {
	@Test
	public void repeatTest() {
		for (int i=0; i<10;i++) {
			test();
		}
	}
	
	private void test() {
		Faker faker = new Faker(new Locale("es", "ES"));
		AggregatedAnnualSummary.SummaryType type = faker.bool().bool() ? MONTHLY : QUARTERLY;
		Map<String, AggregatedAnnualYearlyEntry> mainMap = new LinkedHashMap<String, AggregatedAnnualYearlyEntry>();
		for (int i=0; i< faker.number().numberBetween(0, 10); i++) {
			String[] months = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
			String[] quarters = {"1\u00ba TRIM", "2\u00ba TRIM", "3\u00ba TRIM", "4\u00ba TRIM"};
			String[] periods = type == MONTHLY ? months : quarters;
			
			Map<String, AggregatedAnnualEntry> map = new LinkedHashMap<String, AggregatedAnnualEntry>();
			for (String period : periods) {
				AggregatedAnnualEntry entry = new AggregatedAnnualEntry();
				entry.setAccBase(getAmount(faker));
				entry.setBonuses(getAmount(faker));
				entry.setCcBase(getAmount(faker));
				entry.setEnterpriseCost(getAmount(faker));
				entry.setEnterpriseSS(getAmount(faker));
				entry.setExtraProrration(getAmount(faker));
				entry.setInKindIrpfBase(getAmount(faker));
				entry.setMoneyIrpfBase(getAmount(faker));
				entry.setTotalDeduction(getAmount(faker));
				entry.setTotalIrpfBase(getAmount(faker));
				entry.setTotalLiquid(getAmount(faker));
				entry.setTotalRaw(getAmount(faker));
				
				LinkedHashMap<String, Double> daysAndHours = new LinkedHashMap<String, Double>();
				for (int j=0; j< faker.number().numberBetween(0, 10); j++) {
					daysAndHours.put(faker.chuckNorris().fact(), getDaysHours(faker));
				}
				entry.setDaysAndHours(daysAndHours);
				
				LinkedList<Deduction> deductions = new LinkedList<Salary.Deduction>();
				for (int j=0; j< faker.number().numberBetween(0, 10); j++) {
					Deduction ded = new Deduction(
						getAmount(faker)
						, faker.ancient().god()
						, null
						, eightyPercent(faker) ? DeductionType.values()[faker.number().numberBetween(0, DeductionType.values().length-1)] : null);
					deductions.add(eightyPercent(faker) ? ded : null);
				}
				entry.setDeductions(deductions);
				
				LinkedList<Payment> payments = new LinkedList<Salary.Payment>();
				for (int j=0; j< faker.number().numberBetween(0, 10); j++) {
					Payment pay = new Payment(getAmount(faker)
							, getAmount(faker)
							, faker.company().buzzword()
							, faker.friends().character()
							, faker.ancient().hero()
							, eightyPercent(faker) ? PaymentType.values()[faker.number().numberBetween(0, PaymentType.values().length-1)] : null);
					payments.add(eightyPercent(faker) ? pay : null);
				}
				entry.setPayments(payments);
				
				map.put(period, entry);
				
			}
			AggregatedAnnualYearlyEntry yearlyEntry = new AggregatedAnnualYearlyEntry(
					eightyPercent(faker) ? faker.artist().name() : null
					, eightyPercent(faker) ? faker.business().creditCardNumber() : null
					, eightyPercent(faker) ? faker.educator().campus() : null
					, map);
			mainMap.put(faker.business().creditCardNumber(), yearlyEntry);
		}
		AggregatedAnnualSummary.getExcel(OutputStream.nullOutputStream()
				, faker.number().numberBetween(2000, 2050)
				, mainMap, faker.company().name()
				, faker.business().creditCardNumber()
				, type
				, faker.bool().bool());
	}
	private static Double getAmount(Faker faker) {
		int probab = faker.number().numberBetween(0, 100);
		return (probab > 20 ? faker.number().randomDouble(2, 0, 3000) : null);
	}
	private static Double getDaysHours(Faker faker) {
		int probab = faker.number().numberBetween(0, 10);
		return (probab > 20 ? faker.number().randomDouble(0, 0, 10) : null);
	}
	private static boolean eightyPercent(Faker faker) {
		int probab = faker.number().numberBetween(0, 100);
		return probab > 20;
	}
}
