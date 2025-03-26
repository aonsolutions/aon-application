package com.esferalia.aon.occam.test.fiscal.mod115;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

public class Mod115BIZKAIATest extends AbstractOccamTest {
	
	@Test
	public void mod115InsertAEAT() {
		System.out.println( "\t ---------------------");
		
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(getTestDate())
			.setMonthly(false)
			.setAdministration(Administration.BIZKAIA);
		
		Mod115 mod115 = FiscalFaker.createMod115(params);
		MODEL115.save(getOccam(), mod115);
		
		Mod115 actual = MODEL115.get(getOccam(), mod115.getId());  
		Asserts.assertMod115(mod115, actual);
		FiscalTestSuite.printModel(actual);
		
		InsertRandomInvoicesTest.insertInvoices( getTestDate() );
		
		FiscalFakerParams compParams = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(getTestDate())
			.setMonthly(false)
			.setAdministration(Administration.BIZKAIA)
			.setComplementary( true )
			.setGenerateFromYearStart( true );
		Mod115 comp = FiscalFaker.getMod115(compParams);
		comp.setMustIncludeInvoicesOnGeneration( true );
		comp = MODEL115.create(getOccam(), comp);
		MODEL115.save(getOccam(), comp);
		Mod115 actualComp = MODEL115.get(getOccam(), comp.getId());  
		Asserts.assertMod115(comp, actualComp);
		FiscalTestSuite.printModel(actualComp);
		
	}
	
}
