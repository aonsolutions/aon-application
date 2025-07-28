package net.aonsolutions.aon.verifactu.exceptions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonStringUtils;

class VerifactuErrorTest {

	@Test
	void code() {
		for (VerifactuError e : VerifactuError.values()) {
			String name = e.name();
			String code = e.getCode();
			assertNotNull( code );
			assertFalse( AonStringUtils.isBlank(code) );
			assertTrue("Por convencion, el nombre debe contener el codigo", AonStringUtils.contains(name, code));
		}
		
	}
}
