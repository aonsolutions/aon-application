package net.aonsolutions.aon.invoice.communication.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;

import uk.co.jemos.podam.api.PodamUtils;

class InvoiceCommunicationExceptionTest {

	@Test
	void empty() {
		InvoiceCommunicationException e = new InvoiceCommunicationException();
		assertEquals(InvoiceCommunicationError.AON_9000, e.getInvoiceCommunicationError());
		assertNull( e.getCause());
	}
	
	@Test
	void delegated() {
		IllegalArgumentException e0 = new  IllegalArgumentException("AAA");
		InvoiceCommunicationException e = new InvoiceCommunicationException(e0);
		assertEquals(InvoiceCommunicationError.AON_9000, e.getInvoiceCommunicationError());
		assertNotNull( e.getCause());
		assertEquals(e0, e.getCause());
	}
	
	@Test
	void supplied() {
		Integer i = PodamUtils.getIntegerInRange(0, InvoiceCommunicationError.values().length - 1);
		InvoiceCommunicationError e = InvoiceCommunicationError.values()[i];
		InvoiceCommunicationException ex = new InvoiceCommunicationException(e);
		assertEquals(e, ex.getInvoiceCommunicationError());
	}
}
