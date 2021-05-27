package com.code.aon.registry.test;

import com.code.aon.common.enumeration.Country;
import com.code.aon.registry.RegistryDocument;
import com.code.aon.registry.enumeration.DocumentType;

import junit.framework.TestCase;

public class TestRegistryDocument extends TestCase {

	public void testNull6_0_X() throws Exception {
		assertFalse(new RegistryDocument(null).isValid());
		assertFalse(new RegistryDocument(null).isValidNIF());
		assertFalse(new RegistryDocument(null).isValidCIF());
	}

	public void testNull() throws Exception {
		assertFalse(new RegistryDocument(null,null,null).isValid());
		assertFalse(new RegistryDocument(null,null,null).isValidNIF());
		assertFalse(new RegistryDocument(null,null,null).isValidCIF());
	}
	
	public void testEmpty() throws Exception {
		assertFalse(new RegistryDocument(null,null,"").isValid());
		assertFalse(new RegistryDocument(null,null,"").isValidNIF());
		assertFalse(new RegistryDocument(null,null,"").isValidCIF());
	}
	public void testNIF() throws Exception {
		assertTrue(new RegistryDocument(Country.ES,DocumentType.NIF,"44671367P").isValid());
		assertTrue(new RegistryDocument(Country.ES,DocumentType.NIF,"44671367P").isValidNIF());
		assertFalse(new RegistryDocument(Country.ES,DocumentType.NIF,"44671367P").isValidCIF());
		assertFalse(new RegistryDocument(Country.ES,DocumentType.NIF,"44671367P").isValidNIE());

		assertFalse(new RegistryDocument(null,DocumentType.NIF,"44671367P").isValid());
		assertFalse(new RegistryDocument(Country.ES,null,"44671367P").isValid());
		
		assertFalse(new RegistryDocument(Country.ES,DocumentType.CIF,"44671367P").isValid());

		assertFalse(new RegistryDocument(Country.ES,DocumentType.NIF,"44673367P").isValid());
		assertFalse(new RegistryDocument(Country.ES,DocumentType.NIF,"44F73367P").isValid());
	}

	public void testNIE() throws Exception {
		assertTrue(new RegistryDocument(Country.ES,DocumentType.NIE,"X0648572H").isValid());
		assertTrue(new RegistryDocument(Country.ES,DocumentType.NIE,"X0648572H").isValidNIE());
		assertFalse(new RegistryDocument(Country.ES,DocumentType.NIE,"X0648572H").isValidNIF());
		assertFalse(new RegistryDocument(Country.ES,DocumentType.NIE,"X0648572H").isValidCIF());

		assertTrue(new RegistryDocument(Country.ES,DocumentType.NIE,"X1321417K").isValid());
		assertFalse(new RegistryDocument(null,DocumentType.NIE,"X1321417K").isValid());
		assertFalse(new RegistryDocument(Country.ES,null,"X1321417K").isValid());
		assertFalse(new RegistryDocument(Country.ES,DocumentType.NIF,"X1321417K").isValid());
		assertFalse(new RegistryDocument(Country.ES,DocumentType.CIF,"X1321417K").isValid());

		assertTrue(new RegistryDocument(Country.ES,DocumentType.NIE,"X2403207Y").isValid());

		assertFalse(new RegistryDocument(Country.ES,DocumentType.NIE,"X240e207Y").isValid());
		assertFalse(new RegistryDocument(Country.ES,DocumentType.NIE,"X24e  207Y").isValid());
	}

	public void testCIF() throws Exception {
		assertTrue(new RegistryDocument(Country.ES,DocumentType.CIF,"A01035724").isValid());
		assertTrue(new RegistryDocument(Country.ES,DocumentType.CIF,"A01253988").isValid());
		assertTrue(new RegistryDocument(Country.ES,DocumentType.CIF,"B01058163").isValid());
		assertTrue(new RegistryDocument(Country.ES,DocumentType.CIF,"B14507610").isValid());
	}

	public void testComunidades() throws Exception {
		assertTrue(new RegistryDocument(Country.ES,DocumentType.CIF,"Q0100122A").isValid());
	}

	public void testAyuntamientos() throws Exception {
		assertTrue(new RegistryDocument(Country.ES,DocumentType.CIF,"P0104500D").isValid());
	}

	public void testSociendadesComunitarias() throws Exception {
		assertTrue(new RegistryDocument(Country.ES,DocumentType.CIF,"D77009215").isValid());
		assertFalse(new RegistryDocument(Country.ES,DocumentType.CIF,"0X289216S").isValid());
	}
}
