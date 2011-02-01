package com.code.aon.registry.test;

import com.code.aon.registry.RegistryDocument;

import junit.framework.TestCase;

public class TestRegistryDocument extends TestCase {

	public void testNull() throws Exception {
		assertFalse(new RegistryDocument(null).isValid());
		assertFalse(new RegistryDocument(null).isValidDNI());
		assertFalse(new RegistryDocument(null).isValidNIF());
	}
	public void testEmpty() throws Exception {
		assertFalse(new RegistryDocument("").isValid());
		assertFalse(new RegistryDocument("").isValidDNI());
		assertFalse(new RegistryDocument("").isValidNIF());
	}
	public void testDNI() throws Exception {
		assertTrue(new RegistryDocument("44671367P").isValid());
		assertTrue(new RegistryDocument("44671367P").isValidDNI());
		assertFalse(new RegistryDocument("44671367P").isValidNIF());
		
		assertFalse(new RegistryDocument("44673367P").isValid());
		assertFalse(new RegistryDocument("44F73367P").isValidDNI());
		assertFalse(new RegistryDocument("44F73367P").isValidNIF());
	}

	public void testDNIExtranjeros() throws Exception {
		assertTrue(new RegistryDocument("X0648572H").isValid());
		assertTrue(new RegistryDocument("X0648572H").isValidDNI());
		assertFalse(new RegistryDocument("X0648572H").isValidNIF());

		assertTrue(new RegistryDocument("X1321417K").isValid());
		assertTrue(new RegistryDocument("X1321417K").isValidDNI());
		assertFalse(new RegistryDocument("X1321417K").isValidNIF());

		assertTrue(new RegistryDocument("X2403207Y").isValid());
		assertTrue(new RegistryDocument("X2403207Y").isValidDNI());
		assertFalse(new RegistryDocument("X2403207Y").isValidNIF());

		assertFalse(new RegistryDocument("X240e207Y").isValid());
		assertFalse(new RegistryDocument("X240e207Y").isValidDNI());
		assertFalse(new RegistryDocument("X240e207Y").isValidNIF());

		assertFalse(new RegistryDocument("X24e  207Y").isValid());
		assertFalse(new RegistryDocument("X24e  207Y").isValidDNI());
		assertFalse(new RegistryDocument("X24e  207Y").isValidNIF());
	}

	public void testNIF() throws Exception {
		assertTrue(new RegistryDocument("A01035724").isValid());
		assertTrue(new RegistryDocument("A01253988").isValid());
		assertTrue(new RegistryDocument("B01058163").isValid());
		assertTrue(new RegistryDocument("B14507610").isValid());
	}

	public void testComunidades() throws Exception {
		assertTrue(new RegistryDocument("Q0100122A").isValid());
	}

	public void testAyuntamientos() throws Exception {
		assertTrue(new RegistryDocument("P0104500D").isValid());
	}

	public void testSociendadesComunitarias() throws Exception {
		assertTrue(new RegistryDocument("D77009215").isValid());
		assertFalse(new RegistryDocument("0X289216S").isValid() );
	}
}
