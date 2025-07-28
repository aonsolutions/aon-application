package net.aonsolutions.aon.verifactu.exceptions;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import uk.co.jemos.podam.api.PodamUtils;

class VerifactuExceptionTest {

	@Test
	void empty() {
		VerifactuException e = new VerifactuException();
		assertEquals(VerifactuError.AON_9000, e.getVerifactuError());
		assertNull( e.getCause());
	}
	
	@Test
	void delegated() {
		IllegalArgumentException e0 = new  IllegalArgumentException("AAA");
		VerifactuException e = new VerifactuException(e0);
		assertEquals(VerifactuError.AON_9000, e.getVerifactuError());
		assertNotNull( e.getCause());
		assertEquals(e0, e.getCause());
	}
	
	@Test
	void supplied() {
		Integer i = PodamUtils.getIntegerInRange(0, VerifactuError.values().length - 1);
		VerifactuError e = VerifactuError.values()[i];
		VerifactuException ex = new VerifactuException(e);
		assertEquals(e, ex.getVerifactuError());
	}
}
