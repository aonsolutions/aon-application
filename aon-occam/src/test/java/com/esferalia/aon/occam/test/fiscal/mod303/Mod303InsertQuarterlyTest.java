package com.esferalia.aon.occam.test.fiscal.mod303;

import java.time.LocalDate;
import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public class Mod303InsertQuarterlyTest extends AbstractOccamTest {

	@Test
	public void test() {
		int year = LocalDate.now().getYear();
		for (Period period : Period.values()) {
			Date start =  FiscalUtils.getPeriodStart(year,period);
			Date end =  FiscalUtils.getPeriodEnd(year,period);
			if (period.isQuarterPeriod()) {
				mod303InsertQuarterly(AonRandom.getRangeDate(start,end));
			}
		}
	}
	
	public void mod303InsertQuarterly(Date date) {
		System.out.println( "\t ---------------------");
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(date)
				.setMonthly(false)
				.setProrratePercent( AonRandom.gt(10)? 0 : AonRandom.getPercent() );

		
		Mod303 aeat = insertModel( params.setAdministration(Administration.COMMON_TERRITORY) );
		Mod303 araba = insertModel( params.setAdministration(Administration.ALAVA));
		Mod303 bizkaia = insertModel( params.setAdministration(Administration.BIZKAIA));
		Mod303 gipuzkoa = insertModel( params.setAdministration(Administration.GIPUZKOA));
		Mod303 navarra = insertModel( params.setAdministration(Administration.NAVARRA));
		
		Asserts.assertEqualsDouble("Araba " + araba.getModelFullName() + ". Resultado no coincide.", aeat.getDeclarationResult(), araba.getDeclarationResult());
		Asserts.assertEqualsDouble("Bizkaia " + bizkaia.getModelFullName() + ". Resultado no coincide.", aeat.getDeclarationResult(), bizkaia.getDeclarationResult());
		Asserts.assertEqualsDouble("Gipuzkoa " + gipuzkoa.getModelFullName() + ". Resultado no coincide.", aeat.getDeclarationResult(), gipuzkoa.getDeclarationResult());
		Asserts.assertEqualsDouble("Navarra " + navarra.getModelFullName() + ". Resultado no coincide.", aeat.getDeclarationResult(), navarra.getDeclarationResult());
	}

	private Mod303 insertModel( FiscalFakerParams params) {
		Mod303 mod303 = FiscalFaker.createMod303(params);
		MODEL303.save(getOccam(), mod303);
		Mod303 actual = MODEL303.get(getOccam(), mod303.getId());  
		Asserts.assertMod303(mod303, actual);
		FiscalTestSuite.printModel(actual);
		return actual;
	}

}
