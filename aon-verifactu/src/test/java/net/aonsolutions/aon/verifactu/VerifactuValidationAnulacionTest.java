package net.aonsolutions.aon.verifactu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CountryType2;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.GeneradoPorType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDOtroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PersonaFisicaJuridicaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAnulacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;

class VerifactuValidationAnulacionTest extends AbstractVerifactuTest {
	
	@Override protected Environment getEnvironment() { return VERIFACTU_ENV; }

	private static record Context( RegFactuSistemaFacturacion msg, RegistroFacturacionAnulacionType fra) {}
	private interface CompleteRegistroFacturaType {
		void complete( Context c );
	}
	private interface CompleteInvoiceCommunicatorContext {
		void complete(InvoiceCommunicatorContext icc);
	}
	
	private void assertInvoice(Invoice invoice, CompleteInvoiceCommunicatorContext completeIcc, CompleteRegistroFacturaType complete) throws InvoiceCommunicationException {
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext icc = getEnvironment().getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc, getEnvironment().getEnablerData(icc.getConfig()))
			.setOperation(InvoiceCommunicationOperation.ANNULMENT);
		if (completeIcc != null) {
			completeIcc.complete(icc);
		}
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(getEnvironment().getCtx(), InvoiceCommunicationType.VERIFACTU,vc, EMPTY_VERIFACTU_PHASE_LISTENER);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		RegistroFacturacionAnulacionType anulacion = fraType.getRegistroAnulacion();
		if (complete != null) {
			complete.complete(new Context( fras, anulacion ));
		}
		VerifactuValidation.validate(fras, fraType, invoice );
	}
	
	private void assertInvoiceNoMessage(Invoice invoice, CompleteInvoiceCommunicatorContext completeIcc, CompleteRegistroFacturaType complete) throws InvoiceCommunicationException {
		assertInvoice(invoice, completeIcc, complete );
		invoice.messageStream()
			.map(m -> m.getCode() + " - " + m.getMessage())
			.forEach(System.out::println);
		assertFalse( invoice.hasMessages() );
	}
	
	private void assertInvoiceMessage(Invoice invoice, CompleteInvoiceCommunicatorContext completeIcc, CompleteRegistroFacturaType complete, InvoiceCommunicationError ... error) throws InvoiceCommunicationException {
		assertInvoice(invoice, completeIcc, complete );
		assertTrue( invoice.hasMessages() );
		int errorSize = AonCollectionUtils.size(error);
		if ( invoice.getMessagesSize() > errorSize) {
			System.out.println(" ------------ ");
			invoice.messageStream()
				.map(m -> m.getCode() + " - " + m.getMessage())
				.forEach(System.out::println);
		}
		assertEquals( errorSize , invoice.getMessagesSize());
		AonCollectionUtils.stream(error)
			.forEach( e -> assertTrue( 
				invoice.messageStream()
					.anyMatch( m -> m.getLevel() == InvoiceErrorLevel.ERR
						&& m.getContext() != null
						&& m.getContext().getKey()== InvoiceErrorKey.COMMUNICATION
						&& AonStringUtils.contains(m.getMessage(), e.getCode())
					)
				)
			);
	}
	
	@Test
	void verifactu_4104_Test() {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class, () -> 
			assertInvoice( invoice
				, icc -> icc.getCompany().setDocument(null)
				, null)
		);
		assertNotNull(e);
		assertNotNull(e.getMessages());
		assertThat(InvoiceCommunicationError.VERIFACTU_4104).isIn(e.getMessages());
	}


	@Test
	void verifactu_4116_Test() {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class, () -> 
			assertInvoice( invoice
				, icc -> icc.getCompany().setDocument("AAAAAAAAA")
				, null)
		);
		assertNotNull(e);
		assertNotNull(e.getMessages());
		assertThat(InvoiceCommunicationError.VERIFACTU_4116).isIn(e.getMessages());
	}
	
	@Test
	void verifactu_1108_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		assertInvoiceMessage( invoice, null
			, c -> c.fra.getIDFactura().setIDEmisorFacturaAnulada("AAAAAAAAA")
			, InvoiceCommunicationError.VERIFACTU_1108
		);
	}

	@Test
	void verifactu_1224_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setGeneradoPor(GeneradoPorType.T);
				c.fra.setGenerador(null);
			}
			,  InvoiceCommunicationError.VERIFACTU_1224
		);
	}

	@Test
	void verifactu_1224_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setGeneradoPor(null);
				c.fra.setGenerador(new PersonaFisicaJuridicaType());
			}
			,  InvoiceCommunicationError.VERIFACTU_1224, InvoiceCommunicationError.VERIFACTU_1228
		);
	}

	@Test
	void verifactu_1259_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setGeneradoPor(GeneradoPorType.E);
				PersonaFisicaJuridicaType id = new PersonaFisicaJuridicaType();
				id.setNIF( c.msg.getCabecera().getObligadoEmision().getNIF() );
				c.fra.setGenerador(id);
			}
			,  InvoiceCommunicationError.VERIFACTU_1259
		);
	}
	
	@Test
	void verifactu_1258_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setGeneradoPor(GeneradoPorType.E);
				PersonaFisicaJuridicaType id = new PersonaFisicaJuridicaType();
				id.setNIF( "12345678A" );
				c.fra.setGenerador(id);
			}
			,  InvoiceCommunicationError.VERIFACTU_1258
		);
	}
	
	@Test
	void verifactu_1228_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setGeneradoPor(GeneradoPorType.E);
				PersonaFisicaJuridicaType id = new PersonaFisicaJuridicaType();
				id.setNIF( "11111111H" );
				id.setIDOtro(new IDOtroType());
				c.fra.setGenerador(id);
			}
			,  InvoiceCommunicationError.VERIFACTU_1228
		);
	}
	
	@Test
	void verifactu_1228_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setGeneradoPor(GeneradoPorType.E);
				PersonaFisicaJuridicaType id = new PersonaFisicaJuridicaType();
				id.setNIF( null );
				id.setIDOtro(null);
				c.fra.setGenerador(id);
			}
			,  InvoiceCommunicationError.VERIFACTU_1228,  InvoiceCommunicationError.VERIFACTU_1227
		);
	}
	
	@Test
	void verifactu_1122_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setGeneradoPor(GeneradoPorType.T);
				PersonaFisicaJuridicaType tercero = new PersonaFisicaJuridicaType();
				c.fra.setGenerador(tercero);
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.FR);	// Francia
				otro.setIDType("02");
				otro.setID("DE234567890");				// pais en los dos primeros dígitos.  
				tercero.setIDOtro(otro);
			}
			, InvoiceCommunicationError.VERIFACTU_1122
		);
	}

	@Test
	void verifactu_1222_Test_0() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_INTRACOMUNITARIA.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		assertInvoiceNoMessage( invoice, null
				, c -> {
					
				}
			);
		
	}

	@Test
	void verifactu_1222_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setGeneradoPor(GeneradoPorType.T);
				PersonaFisicaJuridicaType tercero = new PersonaFisicaJuridicaType();
				c.fra.setGenerador(tercero);
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.FR);	// Francia
				otro.setIDType("02");
				otro.setID("FR0123456789");				// Debe tner 11 caracteres
				tercero.setIDOtro(otro);
			}
			, InvoiceCommunicationError.VERIFACTU_1222
		);
	}
	
	@Test
	void verifactu_1229_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setGeneradoPor(GeneradoPorType.T);
				PersonaFisicaJuridicaType tercero = new PersonaFisicaJuridicaType();
				c.fra.setGenerador(tercero);
				IDOtroType otro = new IDOtroType();
				otro.setIDType("07");
				tercero.setIDOtro(otro);
			}
			, InvoiceCommunicationError.VERIFACTU_1229
		);
	}
	
	@Test
	void verifactu_1101_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setGeneradoPor(GeneradoPorType.T);
				PersonaFisicaJuridicaType tercero = new PersonaFisicaJuridicaType();
				c.fra.setGenerador(tercero);
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.AR);	// Argentina
				otro.setIDType("02");
				tercero.setIDOtro(otro);
			}
			, InvoiceCommunicationError.VERIFACTU_1101
		);
	}
	
	@Test
	void verifactu_1230_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setGeneradoPor(GeneradoPorType.D);
				PersonaFisicaJuridicaType tercero = new PersonaFisicaJuridicaType();
				c.fra.setGenerador(tercero);
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.AR);
				otro.setIDType("03");
				tercero.setIDOtro(otro);
			}
			, InvoiceCommunicationError.VERIFACTU_1230
		);
	}
		
	@Test
	void verifactu_1230_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setGeneradoPor(GeneradoPorType.D);
				PersonaFisicaJuridicaType tercero = new PersonaFisicaJuridicaType();
				c.fra.setGenerador(tercero);
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.AR);
				otro.setIDType("07");
				tercero.setIDOtro(otro);
			}
			, InvoiceCommunicationError.VERIFACTU_1230
		);
	}
	
	@Test
	void verifactu_1230_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		assertInvoiceNoMessage( invoice, null
			, c -> {
				c.fra.setGeneradoPor(GeneradoPorType.D);
				PersonaFisicaJuridicaType tercero = new PersonaFisicaJuridicaType();
				c.fra.setGenerador(tercero);
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.FR);	// Francia
				otro.setIDType("02");
				otro.setID("FR01234567890");				// pais en los dos primeros dígitos.  
				tercero.setIDOtro(otro);
			}
		);
	}
	
	@Test
	void verifactu_1231_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1).setActivity(InvoiceTypes.getActivityGeneral(getEnvironment()));
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setGeneradoPor(GeneradoPorType.T);
				PersonaFisicaJuridicaType tercero = new PersonaFisicaJuridicaType();
				c.fra.setGenerador(tercero);
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.AR);
				otro.setIDType("03");
				tercero.setIDOtro(otro);
			}
			, InvoiceCommunicationError.VERIFACTU_1231
		);
	}
}
