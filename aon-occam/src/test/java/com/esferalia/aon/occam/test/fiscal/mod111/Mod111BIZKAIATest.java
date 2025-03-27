package com.esferalia.aon.occam.test.fiscal.mod111;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

public class Mod111BIZKAIATest extends AbstractOccamTest {
	
	@Test
	public void mod111InsertAEAT() {
		System.out.println( "\t ---------------------");
		
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(getTestDate())
			.setMonthly(false)
			.setAdministration(Administration.BIZKAIA);
		
		Mod111 mod111 = FiscalFaker.createMod111(params);
		MODEL111.save(getOccam(), mod111);
		
		Mod111 actual = MODEL111.get(getOccam(), mod111.getId());  
		Asserts.assertMod111(mod111, actual);
		FiscalTestSuite.printModel(actual);
		
		InsertRandomInvoicesTest.insertInvoices( getTestDate() );
		
		FiscalFakerParams compParams = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(getTestDate())
			.setMonthly(false)
			.setAdministration(Administration.BIZKAIA)
			.setComplementary( true )
			.setGenerateFromYearStart( true );
		Mod111 comp = FiscalFaker.getMod111(compParams);
		comp.setMustIncludeInvoicesOnGeneration( true );
		comp.setMustIncludeSalariesOnGeneration( true );
		comp = MODEL111.create(getOccam(), comp);
		MODEL111.save(getOccam(), comp);
		Mod111 actualComp = MODEL111.get(getOccam(), comp.getId());  
		Asserts.assertMod111(comp, actualComp);
		FiscalTestSuite.printModel(actualComp);
		
	}
	
}
