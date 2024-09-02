package com.esferalia.aon.occam.test.fiscal.mod390hf;

import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AON;
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
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod390HFPreapare390InsertTest extends AbstractOccamTest {

	@Test
	public void test() {
		int year = LocalDate.now().getYear();
		
		int times = AonRandom.getInt(1, 100);
		for (int count = 0; count <times; count++) {
			InvoiceFakerParams params = new InvoiceFakerParams(ctx,getConfiguration()).setIssueDate( AonRandom.getRandomYearDay( year ) );
			AON.insertInvoice(getOccam(),InvoiceFaker.getRandom(params));
		}
		
		for (Mod303 model : MODEL303.getMod303s(getOccam()) ) {
			Mod303 mod303 = MODEL303.get(getOccam(), model.getId());
			if (mod303.isAraba() || mod303.isGipuzkoa() || mod303.isBizkaia()) {
				MODEL303.markAsPending(getOccam(), mod303);
				MODEL303.delete(getOccam(), mod303);
			}
		}
		
		for (Period period : Period.values()) {
			Date start =  FiscalUtils.getPeriodStart(year,period);
			Date end =  FiscalUtils.getPeriodEnd(year,period);
			if (period.isQuarterPeriod() && !period.isLastPeriod()) {
				mod303InsertQuarterly(AonRandom.getRangeDate(start,end));
			}
		}

		for (Mod303 model : MODEL303.getMod303s(getOccam()) ) {
			Mod303 mod303 = MODEL303.get(getOccam(), model.getId());
			if (mod303.isAraba() || mod303.isGipuzkoa() || mod303.isBizkaia()) {
				MODEL303.initializeForFinish(getOccam(), mod303);
				MODEL303.markAsFinished(getOccam(), mod303);
			}
		}
	}
	
	public void mod303InsertQuarterly(Date date) {
		System.out.println( "\t ---------------------");
		double prorratePercent = AonRandom.gt(10)? 0 : AonRandom.getPercent();
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setMonthly(false)
			.setProrratePercent( prorratePercent )
			.setSpecialProrrate( AonMathUtils.isNotZero(prorratePercent) && AonRandom.gt(60) )
			;

		Mod303 araba = insertModel( params.setAdministration(Administration.ALAVA));
		Mod303 bizkaia = insertModel( params.setAdministration(Administration.BIZKAIA));
		Mod303 gipuzkoa = insertModel( params.setAdministration(Administration.GIPUZKOA));
		
		Asserts.assertEqualsDouble("Araba " + araba.getModelFullName() + ". Resultado no coincide.", bizkaia.getDeclarationResult(), araba.getDeclarationResult());
		Asserts.assertEqualsDouble("Bizkaia " + bizkaia.getModelFullName() + ". Resultado no coincide.", gipuzkoa.getDeclarationResult(), bizkaia.getDeclarationResult());
		Asserts.assertEqualsDouble("Gipuzkoa " + gipuzkoa.getModelFullName() + ". Resultado no coincide.", araba.getDeclarationResult(), gipuzkoa.getDeclarationResult());
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
