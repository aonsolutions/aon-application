package com.esferalia.aon.occam.test.fiscal.mod349;

import static org.junit.Assert.assertNotNull;

import com.esferalia.aon.occam.api.fiscal.MODEL349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public abstract class Mod349AbstractTest extends AbstractOccamTest {
	
	protected void mod349Insert(FiscalFakerParams params) {
		System.out.println( "\t ---------------------");
		
		Mod349 aeat = insertModel(params.setAdministration(Administration.COMMON_TERRITORY));
		assertNotNull("Declaracion AEAT Nula",aeat);
		assertNotNull("Declaracion AEAT Nula",aeat.getId());
		
		Mod349 araba = insertModel(params.setAdministration(Administration.ALAVA));
		assertNotNull("Declaracion ALAVA Nula",araba);
		assertNotNull("Declaracion ALAVA Nula",araba.getId());

		Mod349 bizkaia = insertModel(params.setAdministration(Administration.BIZKAIA));
		assertNotNull("Declaracion BIZKAIA Nula",bizkaia);
		assertNotNull("Declaracion BIZKAIA Nula",bizkaia.getId());

		Mod349 gipuzkoa = insertModel(params.setAdministration(Administration.GIPUZKOA));
		assertNotNull("Declaracion GIPUZKOA Nula",gipuzkoa);
		assertNotNull("Declaracion GIPUZKOA Nula",gipuzkoa.getId());

		Mod349 navarra = insertModel(params.setAdministration(Administration.NAVARRA));
		assertNotNull("Declaracion NAVARRA Nula",navarra);
		assertNotNull("Declaracion NAVARRA Nula",navarra.getId());
		
		Asserts.assertEqualsCollection("Araba " + araba.getModelFullName() + ". Resultado no coincide.", aeat.getDetails(), araba.getDetails());
		Asserts.assertEqualsCollection("Araba " + araba.getModelFullName() + ". Resultado no coincide.", aeat.getDetails(), araba.getDetails());
		Asserts.assertEqualsCollection("Bizkaia " + bizkaia.getModelFullName() + ". Resultado no coincide.", aeat.getDetails(), bizkaia.getDetails());
		Asserts.assertEqualsCollection("Gipuzkoa " + gipuzkoa.getModelFullName() + ". Resultado no coincide.", aeat.getDetails(), gipuzkoa.getDetails());
		Asserts.assertEqualsCollection("Navarra " + navarra.getModelFullName() + ". Resultado no coincide.", aeat.getDetails(), navarra.getDetails());
	}
	
	
	// Inserta Normal, Sustitutiva y Complementaria
	protected void mod349InsertAll(FiscalFakerParams params) {
	
		// Normal
		params.setReplacement(false);
		params.setComplementary(false);
		mod349Insert(params);
		
		// Sustitutiva
		params.setReplacement(true);
		params.setComplementary(false);
		mod349Insert(params);
		
		// Complementaria
		params.setReplacement(false);
		params.setComplementary(true);
		mod349Insert(params);
	
	}
	
	private Mod349 insertModel(FiscalFakerParams params) {
		Mod349 mod349 = FiscalFaker.createMod349(params);
		MODEL349.save(getOccam(), mod349);
		Mod349 actual = MODEL349.get(getOccam(), mod349.getId());  
		Asserts.assertMod349(mod349, actual);
		FiscalTestSuite.printModel(actual);
		return actual;
	}
	
}
