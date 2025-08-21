package net.aonsolutions.aon.invoice.communication.exceptions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.watson.util.AonStringUtils;

class InvoiceCommunicationErrorTest {

	@Test
	void code() {
		for (InvoiceCommunicationError e : InvoiceCommunicationError.values()) {
			String name = e.name();
			String code = e.getCode();
			assertNotNull( code );
			assertFalse( AonStringUtils.isBlank(code) );
			assertTrue(AonStringUtils.contains(name, code), "Por convencion, el nombre debe contener el codigo");
		}
		
	}
}
