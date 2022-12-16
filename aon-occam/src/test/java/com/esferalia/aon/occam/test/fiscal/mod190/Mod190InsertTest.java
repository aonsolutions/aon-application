package com.esferalia.aon.occam.test.fiscal.mod190;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public class Mod190InsertTest extends AbstractOccamTest {
	
	@Test
	public void mod190InsertQuarterlyTest() {
		mod190Insert(new Date());
	}

	public void mod190Insert(Date date) {
		System.out.println( "\t ---------------------");
		
		Mod190 aeat = insertModel( Administration.COMMON_TERRITORY, date);
		assertNotNull("Declaracion AEAT Nula",aeat);
		assertNotNull("Declaracion AEAT Nula",aeat.getId());
		
		Mod190 araba = insertModel( Administration.ALAVA, date);
		assertNotNull("Declaracion ALAVA Nula",araba);
		assertNotNull("Declaracion ALAVA Nula",araba.getId());

		Mod190 bizkaia = insertModel( Administration.BIZKAIA, date);
		assertNotNull("Declaracion BIZKAIA Nula",bizkaia);
		assertNotNull("Declaracion BIZKAIA Nula",bizkaia.getId());

		Mod190 gipuzkoa = insertModel( Administration.GIPUZKOA, date);
		assertNotNull("Declaracion GIPUZKOA Nula",gipuzkoa);
		assertNotNull("Declaracion GIPUZKOA Nula",gipuzkoa.getId());

		Mod190 navarra = insertModel( Administration.NAVARRA, date);
		assertNotNull("Declaracion NAVARRA Nula",navarra);
		assertNotNull("Declaracion NAVARRA Nula",navarra.getId());
		
		Asserts.assertEqualsCollection("Araba " + araba.getModelFullName() + ". Resultado no coincide.", aeat.getDetails(), araba.getDetails());
		Asserts.assertEqualsCollection("Araba " + araba.getModelFullName() + ". Resultado no coincide.", aeat.getDetails(), araba.getDetails());
		Asserts.assertEqualsCollection("Bizkaia " + bizkaia.getModelFullName() + ". Resultado no coincide.", aeat.getDetails(), bizkaia.getDetails());
		Asserts.assertEqualsCollection("Gipuzkoa " + gipuzkoa.getModelFullName() + ". Resultado no coincide.", aeat.getDetails(), gipuzkoa.getDetails());
		Asserts.assertEqualsCollection("Navarra " + navarra.getModelFullName() + ". Resultado no coincide.", aeat.getDetails(), navarra.getDetails());
	}

	private Mod190 insertModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setAdministration(admon);
		Mod190 mod190 = FiscalFaker.createMod190(params);
		MODEL190.save(getOccam(), mod190);
		Mod190 actual = MODEL190.get(getOccam(), mod190.getId());  
		Asserts.assertMod190(mod190, actual);
		FiscalTestSuite.printModel(actual);
		return actual;
	}
}
