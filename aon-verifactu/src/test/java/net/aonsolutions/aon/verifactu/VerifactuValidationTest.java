package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;

@ExtendWith(MyTestWatcher.class)
class VerifactuValidationTest extends AbstractVerifactuTest {
	
	@Test
	void verifactu_4104_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( ctx , DOMAIN_ID).setId(1);
		List<Invoice> invoices = new LinkedList<>();
		invoice.setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		invoices.add( invoice );
		InvoiceCommunicatorContext icc = getInvoiceCommunicatorContext(invoices);
		icc.getCompany().setDocument(null);
		VerifactuContext vc = new VerifactuContext(icc);
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(vc);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		VerifactuValidation.validate(fras, fraType, invoice );
		assertTrue( invoice.hasMessages() );
		assertTrue( invoice.messageStream()
			.anyMatch( m -> m.getLevel() == InvoiceErrorLevel.ERR
				&& m.getContext() != null
				&& m.getContext().getKey()== InvoiceErrorKey.COMMUNICATION
				&& AonStringUtils.contains(m.getMessage(), InvoiceCommunicationError.VERIFACTU_4104.getCode())
			));
	}

	@Test
	void verifactu_4109_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( ctx , DOMAIN_ID).setId(1);
		List<Invoice> invoices = new LinkedList<>();
		invoice.setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		invoices.add( invoice );
		InvoiceCommunicatorContext icc = getInvoiceCommunicatorContext(invoices);
		icc.getCompany().setDocument("AAAAAAAAA");
		VerifactuContext vc = new VerifactuContext(icc);
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(vc);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		VerifactuValidation.validate(fras, fraType, invoice );
		assertTrue( invoice.hasMessages() );
		assertTrue( invoice.messageStream()
			.anyMatch( m -> m.getLevel() == InvoiceErrorLevel.ERR
				&& m.getContext() != null
				&& m.getContext().getKey()== InvoiceErrorKey.COMMUNICATION
				&& AonStringUtils.contains(m.getMessage(), InvoiceCommunicationError.VERIFACTU_4109.getCode())
			));
		
	}
	
	@Test
	void verifactu_1108_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( ctx , DOMAIN_ID).setId(1);
		List<Invoice> invoices = new LinkedList<>();
		invoice.setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		invoices.add( invoice );
		InvoiceCommunicatorContext icc = getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc);
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(vc);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		fraType.getRegistroAlta().getIDFactura().setIDEmisorFactura("AAAAAAAAA");
		VerifactuValidation.validate(fras, fraType, invoice );
		assertTrue( invoice.hasMessages() );
		assertTrue( invoice.messageStream()
			.anyMatch( m -> m.getLevel() == InvoiceErrorLevel.ERR
				&& m.getContext() != null
				&& m.getContext().getKey()== InvoiceErrorKey.COMMUNICATION
				&& AonStringUtils.contains(m.getMessage(), InvoiceCommunicationError.VERIFACTU_1108.getCode())
			));
		
	}
	
}
