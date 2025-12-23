package net.aonsolutions.aon.invoice.communication.visitor;
 
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;

import net.aonsolutions.aon.verifactu.AbstractVerifactuTest;
import net.aonsolutions.aon.verifactu.InvoiceTypes;

abstract class AbsInvoiceCommunicationSaveTest extends AbstractVerifactuTest {

	@Test
	void venta_nacional_simpleAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get(getEnvironment());
		save(invoice);
	}
	
	@Test
	void venta_nacional_simplificadaAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA.get(getEnvironment());
		save(invoice);
	}
	
	@Test
	void venta_nacional_simplificada_con_customer_sin_direccionAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA_CON_CUSTOMER_SIN_DIRECCION.get(getEnvironment());
		save(invoice);
	}
	
	@Test
	void venta_nacional_suplidosAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SUPLIDOS.get(getEnvironment());
		save(invoice);
	}
	
	@Test
	void venta_nacional_rectificativa_simpleAEATTest() throws InvoiceCommunicationException {
		Invoice invoice1 = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get(getEnvironment());
		invoice1 = save(invoice1);

		Invoice invoice2 = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLE.get(getEnvironment());
		invoice2.setRectificationInvoice(invoice1.getId());
		invoice2.setRectificationInvoiceDate(invoice1.getIssueDate());
		invoice2.setRectificationInvoiceNumber(invoice1.getNumber());
		invoice2.setRectificationInvoiceSeries(invoice1.getSeries());
		invoice2.setRectificationInvoiceReference(invoice1.getReferenceCode());
		save(invoice2);
	}
	
	@Test
	void venta_nacional_rectificativa_simplificadaAEATTest() throws InvoiceCommunicationException {
		Invoice invoice1 = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get(getEnvironment());
		invoice1 = save(invoice1);

		Invoice invoice2 = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLIFICADA.get(getEnvironment());
		invoice2.setRectificationInvoice(invoice1.getId());
		invoice2.setRectificationInvoiceDate(invoice1.getIssueDate());
		invoice2.setRectificationInvoiceNumber(invoice1.getNumber());
		invoice2.setRectificationInvoiceSeries(invoice1.getSeries());
		invoice2.setRectificationInvoiceReference(invoice1.getReferenceCode());
		save(invoice2);
	}
	
	@Test
	void venta_ispAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_ISP.get(getEnvironment());
		save(invoice);
	}
	
	@Test
	void venta_nacional_reAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get(getEnvironment());
		save(invoice);
	}
	
	@Test
	void venta_nacional_irpf_professionalAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_IRPF_PROFESSIONAL.get(getEnvironment());
		save(invoice);
	}
	
	@Test
	void venta_intracomunitaria_serviciosAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_INTRACOMUNITARIA_SERVICIOS.get(getEnvironment());
		save(invoice);
	}
	
	@Test
	void venta_intracomunitaria_no_serviciosAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_INTRACOMUNITARIA.get(getEnvironment());
		save(invoice);
	}

	@Test
	void venta_extracomunitariaAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_EXTRACOMUNITARIA.get(getEnvironment());
		save(invoice);
	}
	
	@Test
	void venta_extracomunitaria_servicioAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_EXTRACOMUNITARIA_SERVICIO.get(getEnvironment());
		save(invoice);
	}

	@Test
	void venta_can_ceu_melAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_CAN_CEU_MEL.get(getEnvironment());
		save(invoice);
	}

	@Test
	void venta_can_ceu_mel_servicioAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_CAN_CEU_MEL_SERVICIO.get(getEnvironment());
		save(invoice);
	}

	@Test
	void venta_nacional_exenta_e1AEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_EXENTA_E1.get(getEnvironment());
		save(invoice);
	}
	
	@Test
	void venta_anuladaAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_ANULADA.get(getEnvironment());
		save(invoice);
	}
	
	@Test
	void venta_nacional_simple_criterio_cajaAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE_CRITERIO_CAJA.get(getEnvironment());
		save(invoice);
	}

	protected abstract Invoice save(Invoice invoice) throws InvoiceCommunicationException;

}
