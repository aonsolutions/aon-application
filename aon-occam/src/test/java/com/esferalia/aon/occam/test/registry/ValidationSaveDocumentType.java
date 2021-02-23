package com.esferalia.aon.occam.test.registry;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class ValidationSaveDocumentType extends AbstractOccamTest {

	@Test
	public void testOtherCountry() {
		Registry registry = AonFaker.getRegistry( ctx );
		DocumentType documentType = registry.getDocumentType();
		registry.setDocumentCountry(Country.FR);
		registry = RegistryDAO.save(ctx, registry);
		assertEquals(documentType, registry.getDocumentType() );
	}

	@Test
	public void testNullDocumentType() {
		Registry registry = AonFaker.getRegistry( ctx );
		registry.setDocument("23049210J");
		registry.setDocumentCountry(Country.ES);
		registry.setDocumentType(DocumentType.values()[AonRandom.number(DocumentType.NIF.ordinal(), DocumentType.NIE.ordinal())]);		
		registry = RegistryDAO.save(ctx, registry);
		assertEquals(DocumentType.NIF, registry.getDocumentType() );
	}
	
	@Test
	public void testAutocompleteDNI() {
		Registry registry = AonFaker.getRegistry( ctx );
		registry.setDocument("23049210J");
		registry.setDocumentCountry(Country.ES);
		registry.setDocumentType(DocumentType.values()[AonRandom.number(DocumentType.NIF.ordinal(), DocumentType.NIE.ordinal())]);		
		registry = RegistryDAO.save(ctx, registry);
		assertEquals(DocumentType.NIF, registry.getDocumentType() );
	}

	@Test
	public void testAutocompleteCIF() {
		Registry registry = AonFaker.getRegistry( ctx );
		registry.setDocument("D08078115");
		registry.setDocumentCountry(Country.ES);
		registry.setDocumentType(DocumentType.values()[AonRandom.number(DocumentType.NIF.ordinal(), DocumentType.NIE.ordinal())]);		
		registry = RegistryDAO.save(ctx, registry);
		assertEquals(DocumentType.CIF, registry.getDocumentType() );
	}

	@Test
	public void testAutocompleteNIE() {
		Registry registry = AonFaker.getRegistry( ctx );
		registry.setDocument("X1291539C");
		registry.setDocumentCountry(Country.ES);
		registry.setDocumentType(DocumentType.values()[AonRandom.number(DocumentType.NIF.ordinal(), DocumentType.NIE.ordinal())]);
		registry = RegistryDAO.save(ctx, registry);
		assertEquals(DocumentType.NIE, registry.getDocumentType() );
	}
}
