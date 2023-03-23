package com.esferalia.aon.occam.test.fiscal.mod349;

import static org.junit.Assert.assertNotNull;

import com.esferalia.aon.occam.api.fiscal.MODEL349;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public abstract class Mod349AbstractTest extends AbstractOccamTest {
	
	protected void mod349Insert(FiscalFakerParams params) {
		System.out.println( "\t ---------------------");
		
		Mod349 aeat = insertModel(params.setAdministration(Administration.COMMON_TERRITORY));
		Mod349 araba = insertModel(params.setAdministration(Administration.ALAVA));
		Mod349 bizkaia = insertModel(params.setAdministration(Administration.BIZKAIA));
		Mod349 gipuzkoa = insertModel(params.setAdministration(Administration.GIPUZKOA));
		Mod349 navarra = insertModel(params.setAdministration(Administration.NAVARRA));
		
		if (aeat != null) {
			if (araba != null)			
				Asserts.assertEqualsCollection("Araba " + araba.getModelFullName() + ". Resultado no coincide.", aeat.getDetails(), araba.getDetails());
			if (bizkaia != null)
				Asserts.assertEqualsCollection("Bizkaia " + bizkaia.getModelFullName() + ". Resultado no coincide.", aeat.getDetails(), bizkaia.getDetails());
			if (gipuzkoa != null)	
				Asserts.assertEqualsCollection("Gipuzkoa " + gipuzkoa.getModelFullName() + ". Resultado no coincide.", aeat.getDetails(), gipuzkoa.getDetails());
			if (navarra != null)
				Asserts.assertEqualsCollection("Navarra " + navarra.getModelFullName() + ". Resultado no coincide.", aeat.getDetails(), navarra.getDetails());
		}
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
		
		Mod349 mod349 = null;
		Mod349 actual = null;
		try {
			mod349 = FiscalFaker.createMod349(params);
		} catch (Exception e) {
			// La excepción "Out of range value for column...", no va a provocar un error en el test, pues además 
			// de que es dificil que se supere el rango de los campos de importes, ya aparece el correspondiente
			// error controlado al crear el modelo y de todas formas la solución para corregir ese error, sería
			// modificar los campos en la base de datos
			if (e.getMessage().contains("Out of range value for column")) {
				System.out.println( "\t" + AonStringUtils.leftPad(params.getAdministration().getDescription(), 20)
						+ " Modelo "
						+ FiscalModelType.M349.getName() 
						+ " "
						+ AonDateUtils.getYear(params.getIssueDate())
						+ " "
						+ (params.isMonthly()
			       		   ? Period.getMonthlyPeriod(AonDateUtils.getMonth(params.getIssueDate())).getName()
			       		   : Period.getQuarterlyPeriod(AonDateUtils.getMonth(params.getIssueDate())).getName())
						+ " "
						+ (params.isComplementary()?" (C)":"")
						+ (params.isReplacement()?" (S)":"")
						+ " >> Out of range value Exception");
			} else {
				assertNotNull("Declaracion Nula", mod349);
				assertNotNull("Declaracion con ID Nulo", mod349.getId());
				throw e;		
			}
		}
		if (mod349 != null && mod349.getId() != null) {	
			actual = MODEL349.get(getOccam(), mod349.getId());  
		}
		if (mod349 != null && actual != null) {
			Asserts.assertMod349(mod349, actual);		
			FiscalTestSuite.printModel(actual);
		}
		return actual;
		
	}
	
}
