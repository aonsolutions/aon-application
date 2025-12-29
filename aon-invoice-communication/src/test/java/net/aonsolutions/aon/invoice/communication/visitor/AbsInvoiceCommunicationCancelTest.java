package net.aonsolutions.aon.invoice.communication.visitor;
 
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;

import net.aonsolutions.aon.verifactu.AbstractVerifactuTest;
import net.aonsolutions.aon.verifactu.InvoiceTypes;

abstract class AbsInvoiceCommunicationCancelTest extends AbstractVerifactuTest {

	@Test
	void venta_nacional_simpleAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get(getEnvironment());
		save(invoice);
	}

	protected abstract void save(Invoice invoice) throws InvoiceCommunicationException;
	
}
