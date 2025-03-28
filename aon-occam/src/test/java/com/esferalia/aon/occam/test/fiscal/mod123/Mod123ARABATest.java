package com.esferalia.aon.occam.test.fiscal.mod123;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

public class Mod123ARABATest extends AbstractOccamTest {
	
	@Test
	public void mod123InsertAEAT() {
		System.out.println( "\t ---------------------");
		
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(getTestDate())
			.setMonthly(false)
			.setAdministration(Administration.ALAVA);
		
		Mod123 mod123 = FiscalFaker.createMod123(params);
		MODEL123.save(getOccam(), mod123);
		
		Mod123 actual = MODEL123.get(getOccam(), mod123.getId());  
		Asserts.assertMod123(mod123, actual);
		FiscalTestSuite.printModel(actual);
		
		InsertRandomInvoicesTest.insertInvoices( getTestDate() );
		
		FiscalFakerParams compParams = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(getTestDate())
			.setMonthly(false)
			.setAdministration(Administration.ALAVA)
			.setComplementary( true )
			.setGenerateFromYearStart( true );
		Mod123 comp = FiscalFaker.getMod123(compParams);
		comp.setMustIncludeInvoicesOnGeneration( true );
		comp = MODEL123.create(getOccam(), comp);
		MODEL123.save(getOccam(), comp);
		Mod123 actualComp = MODEL123.get(getOccam(), comp.getId());  
		Asserts.assertMod123(comp, actualComp);
		FiscalTestSuite.printModel(actualComp);
		
		InsertRandomInvoicesTest.insertInvoices( getTestDate() );
		
		FiscalFakerParams sustParams = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(getTestDate())
			.setMonthly(false)
			.setAdministration(Administration.ALAVA)
			.setReplacement( true )
			.setGenerateFromYearStart( true );
		Mod123 sust = FiscalFaker.getMod123(sustParams);
		sust.setMustIncludeInvoicesOnGeneration( true );
		sust = MODEL123.create(getOccam(), sust);
		MODEL123.save(getOccam(), sust);
		Mod123 actualSust = MODEL123.get(getOccam(), sust.getId());  
		Asserts.assertMod123(sust, actualSust);
		FiscalTestSuite.printModel(actualSust);
	}
	
}
