package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoFacturaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoRectificativaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.DesgloseRectificacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RechazoPrevioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType.FacturasSustituidas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SubsanacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;

@ExtendWith(MyTestWatcher.class)
class VerifactuValidationTest extends AbstractVerifactuTest {
	
	@Test
	void verifactu_4104_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( ctx , DOMAIN_ID).setId(1).setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
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
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( ctx , DOMAIN_ID).setId(1).setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
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
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( ctx , DOMAIN_ID).setId(1).setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
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

	@Test
	void verifactu_1105_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( ctx , DOMAIN_ID).setId(1).setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext icc = getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc);
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(vc);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		fraType.getRegistroAlta().getIDFactura().setFechaExpedicionFactura(null);
		VerifactuValidation.validate(fras, fraType, invoice );
		assertTrue( invoice.hasMessages() );
		assertTrue( invoice.messageStream()
			.anyMatch( m -> m.getLevel() == InvoiceErrorLevel.ERR
				&& m.getContext() != null
				&& m.getContext().getKey()== InvoiceErrorKey.COMMUNICATION
				&& AonStringUtils.contains(m.getMessage(), InvoiceCommunicationError.VERIFACTU_1105.getCode())
			));
	}
	
	@Test
	void verifactu_1112_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( ctx , DOMAIN_ID).setId(1).setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext icc = getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc);
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(vc);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		Date tomorrow = AonDateUtils.addDays(new Date(), 1);
		fraType.getRegistroAlta().getIDFactura()
			.setFechaExpedicionFactura(VerifactuUtils.toString( tomorrow ));
		VerifactuValidation.validate(fras, fraType, invoice );
		assertTrue( invoice.hasMessages() );
		assertTrue( invoice.messageStream()
			.anyMatch( m -> m.getLevel() == InvoiceErrorLevel.ERR
				&& m.getContext() != null
				&& m.getContext().getKey()== InvoiceErrorKey.COMMUNICATION
				&& AonStringUtils.contains(m.getMessage(), InvoiceCommunicationError.VERIFACTU_1112.getCode())
			));
	}
	

	@Test
	void verifactu_1130_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( ctx , DOMAIN_ID).setId(1).setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext icc = getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc);
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(vc);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		fraType.getRegistroAlta().getIDFactura()
			.setNumSerieFactura("Hola\tMundo"); // Un tabulador
		VerifactuValidation.validate(fras, fraType, invoice );
		assertTrue( invoice.hasMessages() );
		assertTrue( invoice.messageStream()
			.anyMatch( m -> m.getLevel() == InvoiceErrorLevel.ERR
				&& m.getContext() != null
				&& m.getContext().getKey()== InvoiceErrorKey.COMMUNICATION
				&& AonStringUtils.contains(m.getMessage(), InvoiceCommunicationError.VERIFACTU_1130.getCode())
			));
	}

	@Test
	void verifactu_1153_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( ctx , DOMAIN_ID).setId(1).setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext icc = getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc);
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(vc);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		fraType.getRegistroAlta().setRechazoPrevio(RechazoPrevioType.X);
		fraType.getRegistroAlta().setSubsanacion(SubsanacionType.N);	
		VerifactuValidation.validate(fras, fraType, invoice );
		assertTrue( invoice.hasMessages() );
		assertTrue( invoice.messageStream()
			.anyMatch( m -> m.getLevel() == InvoiceErrorLevel.ERR
				&& m.getContext() != null
				&& m.getContext().getKey()== InvoiceErrorKey.COMMUNICATION
				&& AonStringUtils.contains(m.getMessage(), InvoiceCommunicationError.VERIFACTU_1153.getCode())
			));
	}
	
	@Test
	void verifactu_1161_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( ctx , DOMAIN_ID).setId(1).setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext icc = getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc);
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(vc);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		fraType.getRegistroAlta().setRechazoPrevio(RechazoPrevioType.S);
		fraType.getRegistroAlta().setSubsanacion(SubsanacionType.N);	
		VerifactuValidation.validate(fras, fraType, invoice );
		assertTrue( invoice.hasMessages() );
		assertTrue( invoice.messageStream()
			.anyMatch( m -> m.getLevel() == InvoiceErrorLevel.ERR
				&& m.getContext() != null
				&& m.getContext().getKey()== InvoiceErrorKey.COMMUNICATION
				&& AonStringUtils.contains(m.getMessage(), InvoiceCommunicationError.VERIFACTU_1161.getCode())
			));
	}

	@Test
	void verifactu_1106_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( ctx , DOMAIN_ID).setId(1).setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext icc = getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc);
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(vc);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		fraType.getRegistroAlta().setTipoFactura(null);
		VerifactuValidation.validate(fras, fraType, invoice );
		assertTrue( invoice.hasMessages() );
		assertTrue( invoice.messageStream()
			.anyMatch( m -> m.getLevel() == InvoiceErrorLevel.ERR
				&& m.getContext() != null
				&& m.getContext().getKey()== InvoiceErrorKey.COMMUNICATION
				&& AonStringUtils.contains(m.getMessage(), InvoiceCommunicationError.VERIFACTU_1106.getCode())
			));
	}

	@Test
	void verifactu_1114_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( ctx , DOMAIN_ID).setId(1).setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext icc = getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc);
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(vc);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		fraType.getRegistroAlta().setTipoFactura(ClaveTipoFacturaType.R_1);
		fraType.getRegistroAlta().setTipoRectificativa(null);
		VerifactuValidation.validate(fras, fraType, invoice );
		assertTrue( invoice.hasMessages() );
		assertTrue( invoice.messageStream()
			.anyMatch( m -> m.getLevel() == InvoiceErrorLevel.ERR
				&& m.getContext() != null
				&& m.getContext().getKey()== InvoiceErrorKey.COMMUNICATION
				&& AonStringUtils.contains(m.getMessage(), InvoiceCommunicationError.VERIFACTU_1114.getCode())
			));
	}
	
	@Test
	void verifactu_1115_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( ctx , DOMAIN_ID).setId(1).setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext icc = getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc);
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(vc);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		fraType.getRegistroAlta().setTipoRectificativa( ClaveTipoRectificativaType.S );
		VerifactuValidation.validate(fras, fraType, invoice );
		assertTrue( invoice.hasMessages() );
		assertTrue( invoice.messageStream()
			.anyMatch( m -> m.getLevel() == InvoiceErrorLevel.ERR
				&& m.getContext() != null
				&& m.getContext().getKey()== InvoiceErrorKey.COMMUNICATION
				&& AonStringUtils.contains(m.getMessage(), InvoiceCommunicationError.VERIFACTU_1115.getCode())
			));
	}
	
	@Test
	void verifactu_1117_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLE.get( ctx , DOMAIN_ID).setId(1).setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext icc = getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc);
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(vc);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		fraType.getRegistroAlta().setTipoFactura(ClaveTipoFacturaType.F_1);
		VerifactuValidation.validate(fras, fraType, invoice );
		assertTrue( invoice.hasMessages() );
		assertTrue( invoice.messageStream()
			.anyMatch( m -> m.getLevel() == InvoiceErrorLevel.ERR
				&& m.getContext() != null
				&& m.getContext().getKey()== InvoiceErrorKey.COMMUNICATION
				&& AonStringUtils.contains(m.getMessage(), InvoiceCommunicationError.VERIFACTU_1117.getCode())
			));
	}
	
	@Test
	void verifactu_1116_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLE.get( ctx , DOMAIN_ID).setId(1).setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext icc = getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc);
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(vc);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		fraType.getRegistroAlta().setFacturasSustituidas(new FacturasSustituidas( ) );	// NOT NULL
		fraType.getRegistroAlta().setTipoFactura(ClaveTipoFacturaType.F_1);
		VerifactuValidation.validate(fras, fraType, invoice );
		assertTrue( invoice.hasMessages() );
		assertTrue( invoice.messageStream()
			.anyMatch( m -> m.getLevel() == InvoiceErrorLevel.ERR
				&& m.getContext() != null
				&& m.getContext().getKey()== InvoiceErrorKey.COMMUNICATION
				&& AonStringUtils.contains(m.getMessage(), InvoiceCommunicationError.VERIFACTU_1116.getCode())
			));
	}
	
	@Test
	void verifactu_1118_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLE.get( ctx , DOMAIN_ID).setId(1).setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext icc = getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc);
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(vc);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		fraType.getRegistroAlta().setTipoRectificativa(ClaveTipoRectificativaType.S);
		fraType.getRegistroAlta().setImporteRectificacion(null);
		VerifactuValidation.validate(fras, fraType, invoice );
		assertTrue( invoice.hasMessages() );
		assertTrue( invoice.messageStream()
			.anyMatch( m -> m.getLevel() == InvoiceErrorLevel.ERR
				&& m.getContext() != null
				&& m.getContext().getKey()== InvoiceErrorKey.COMMUNICATION
				&& AonStringUtils.contains(m.getMessage(), InvoiceCommunicationError.VERIFACTU_1118.getCode())
			));
	}

	@Test
	void verifactu_1119_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLE.get( ctx , DOMAIN_ID).setId(1).setActivity(InvoiceTypes.ACTIVITY_GENERAL);
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext icc = getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc);
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(vc);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		fraType.getRegistroAlta().setTipoRectificativa(ClaveTipoRectificativaType.I);
		fraType.getRegistroAlta().setImporteRectificacion( new DesgloseRectificacionType() );
		VerifactuValidation.validate(fras, fraType, invoice );
		assertTrue( invoice.hasMessages() );
		assertTrue( invoice.messageStream()
			.anyMatch( m -> m.getLevel() == InvoiceErrorLevel.ERR
				&& m.getContext() != null
				&& m.getContext().getKey()== InvoiceErrorKey.COMMUNICATION
				&& AonStringUtils.contains(m.getMessage(), InvoiceCommunicationError.VERIFACTU_1119.getCode())
			));
	}
}
