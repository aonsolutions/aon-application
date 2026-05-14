package net.aonsolutions.aon.verifactu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CalificacionOperacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoFacturaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoRectificativaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CompletaSinDestinatarioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CountryType2;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CuponType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.DesgloseRectificacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.DetalleType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDOtroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.MacrodatoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.OperacionExentaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PersonaFisicaJuridicaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RechazoPrevioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType.Destinatarios;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType.FacturasSustituidas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SimplificadaCualificadaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SubsanacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.TercerosODestinatarioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;

class VerifactuValidationAltaTest extends AbstractVerifactuTest {
	
	@Override protected Environment getEnvironment() { return VERIFACTU_ENV; }
	
	private static record Context( RegFactuSistemaFacturacion msg, RegistroFacturacionAltaType fra, DetalleType det ) {}
	private interface CompleteRegistroFacturaType {
		void complete( Context c );
	}
	private interface CompleteInvoiceCommunicatorContext {
		void complete(InvoiceCommunicatorContext icc);
	}
	
	private void assertInvoice(Invoice invoice, CompleteInvoiceCommunicatorContext completeIcc, CompleteRegistroFacturaType complete) throws InvoiceCommunicationException {
		invoice.setSeries(VerifactuTestsUtils.series(getEnvironment().getCtx(), invoice.isRectifier()));
		invoice.setNumber(VerifactuTestsUtils.number());
		invoice.setReferenceCode(VerifactuTestsUtils.referenceCode(invoice));
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext icc = getEnvironment().getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc, getEnvironment().getEnablerData(icc.getConfig()));
		if (completeIcc != null) {
			completeIcc.complete(icc);
		}
		RegFactuSistemaFacturacion fras = Invoice2Verifactu.build(getEnvironment().getCtx(), InvoiceCommunicationType.VERIFACTU,vc, EMPTY_VERIFACTU_PHASE_LISTENER);
		RegistroFacturaType fraType = fras.getRegistroFactura().get(0);
		RegistroFacturacionAltaType alta = fraType.getRegistroAlta();
		DetalleType firstDet = alta.getDesglose().getDetalleDesglose().get(0);
		if (complete != null) {
			complete.complete(new Context( fras, alta, firstDet ));
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
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
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
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class, () -> 
			assertInvoice( invoice
				, icc -> icc.getCompany().setDocument("AAAAAAAAA")
				, null)
		);
		assertNotNull(e);
		assertNotNull(e.getMessages());
		assertThat(InvoiceCommunicationError.VERIFACTU_4116).isIn(e.getMessages());
	}
	
//	@Test
//	void verifactu_4105_Test() {
//		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
//		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class, () -> 
//			assertInvoice( invoice
//				, null
//				, c -> c.msg.getCabecera().setRepresentante( new PersonaFisicaJuridicaESType() )
//				)
//		);
//		assertNotNull(e);
//		assertNotNull(e.getInvoiceCommunicationError());
//		assertEquals(InvoiceCommunicationError.VERIFACTU_4105, e.getInvoiceCommunicationError());
//	}
//	
//	@Test
//	void verifactu_4117_Test() {
//		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
//		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class, () -> 
//			assertInvoice( invoice
//				, null
//				, c -> {
//					PersonaFisicaJuridicaESType repr = new PersonaFisicaJuridicaESType();
//					repr.setNIF("AAAAAAAAA");
//					c.msg.getCabecera().setRepresentante( repr );
//				}
//			)
//		);
//		assertNotNull(e);
//		assertNotNull(e.getInvoiceCommunicationError());
//		assertEquals(InvoiceCommunicationError.VERIFACTU_4117, e.getInvoiceCommunicationError());
//	}
	
	@Test
	void verifactu_1108_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.fra.getIDFactura().setIDEmisorFactura("AAAAAAAAA")
			, InvoiceCommunicationError.VERIFACTU_1108
		);
	}

	@Test
	void verifactu_1105_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.fra.getIDFactura().setFechaExpedicionFactura(null)
			, InvoiceCommunicationError.VERIFACTU_1105
		);
	}
	
	@Test
	void verifactu_1112_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date tomorrow = AonDateUtils.addDays(new Date(), 1);
				c.fra
					.getIDFactura()
					.setFechaExpedicionFactura(VerifactuUtils.toString( tomorrow ));
			}
			, InvoiceCommunicationError.VERIFACTU_1112
		);
	}
	

	@Test
	void verifactu_1130_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.fra.getIDFactura().setNumSerieFactura("Hola\tMundo") // Un tabulador
			, InvoiceCommunicationError.VERIFACTU_1130
		);
	}
	@Test
	void verifactu_1130_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.fra.getIDFactura().setNumSerieFactura("Hola\"Mundo") // Comillas
			, InvoiceCommunicationError.VERIFACTU_1130
		);
	}
	@Test
	void verifactu_1130_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.fra.getIDFactura().setNumSerieFactura("Hola ' Mundo") // Comilla simple
			, InvoiceCommunicationError.VERIFACTU_1130
		);
	}
	@Test
	void verifactu_1130_Test_4() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.fra.getIDFactura().setNumSerieFactura("Hola < Mundo") // Menor
			, InvoiceCommunicationError.VERIFACTU_1130
		);
	}
	@Test
	void verifactu_1130_Test_5() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.fra.getIDFactura().setNumSerieFactura("Hola > Mundo") // Mayor
			, InvoiceCommunicationError.VERIFACTU_1130
		);
	}
	@Test
	void verifactu_1130_Test_6() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.fra.getIDFactura().setNumSerieFactura("Hola = Mundo") // Igual
			, InvoiceCommunicationError.VERIFACTU_1130
		);
	}

	@Test
	void verifactu_1153_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setRechazoPrevio(RechazoPrevioType.X);
				c.fra.setSubsanacion(SubsanacionType.N);	
			}
			, InvoiceCommunicationError.VERIFACTU_1153
		);
	}
	
	@Test
	void verifactu_1161_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setRechazoPrevio(RechazoPrevioType.S);
				c.fra.setSubsanacion(SubsanacionType.N);	
			}
			, InvoiceCommunicationError.VERIFACTU_1161
		);
	}

	@Test
	void verifactu_1106_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.fra.setTipoFactura(null)
			, InvoiceCommunicationError.VERIFACTU_1106
		);
	}

	@Test
	void verifactu_1114_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setTipoFactura(ClaveTipoFacturaType.R_1);
				c.fra.setTipoRectificativa(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1114, InvoiceCommunicationError.VERIFACTU_4102
		);
	}
	
	@Test
	void verifactu_1115_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.fra.setTipoRectificativa( ClaveTipoRectificativaType.I )
			, InvoiceCommunicationError.VERIFACTU_1115
		);
	}
	
	@Test
	void verifactu_1117_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setTipoRectificativa(null);
				c.fra.setTipoFactura(ClaveTipoFacturaType.F_1);
			}
			, InvoiceCommunicationError.VERIFACTU_1117
		);
	}
	
	@Test
	void verifactu_1116_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setFacturasSustituidas(new FacturasSustituidas( ) );	// NOT NULL
				c.fra.setTipoFactura(ClaveTipoFacturaType.F_1);
			}
			, InvoiceCommunicationError.VERIFACTU_1116
		);
	}
	
	@Test
	void verifactu_1118_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setTipoRectificativa(ClaveTipoRectificativaType.S);
				c.fra.setImporteRectificacion(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1118
		);
	}

	@Test
	void verifactu_1119_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setTipoRectificativa(ClaveTipoRectificativaType.I);
				c.fra.setImporteRectificacion( new DesgloseRectificacionType() );
			}
			, InvoiceCommunicationError.VERIFACTU_1119
		);
	}
	
	@Test
	void verifactu_1134_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date minDate = AonDateUtils.add(new Date(), Calendar.YEAR, -20);
				minDate = AonDateUtils.add(minDate, Calendar.DAY_OF_MONTH, -1);
				c.fra.setFechaOperacion(VerifactuUtils.toString(minDate));
			}
			, InvoiceCommunicationError.VERIFACTU_1134
		);
	}
	
		
	@Test
	void verifactu_1125_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date maxDate = AonDateUtils.add(new Date(), Calendar.DAY_OF_MONTH, 370);
				c.fra.setFechaOperacion(VerifactuUtils.toString(maxDate));
			}
			, InvoiceCommunicationError.VERIFACTU_1125
		);
	}
	

	@Test
	void verifactu_1136_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.fra.setFacturaSimplificadaArt7273(null)
			,  InvoiceCommunicationError.VERIFACTU_1136
		);
	}
	
	@Test
	void verifactu_1183_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setFacturaSimplificadaArt7273(SimplificadaCualificadaType.S);
				c.fra.setTipoFactura( ClaveTipoFacturaType.R_5 );
				c.fra.setDestinatarios(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1183
		);
	}
	
	@Test
	void verifactu_1152_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2024);
				c.fra.getIDFactura().setFechaExpedicionFactura(VerifactuUtils.toString(date));
			}
			, InvoiceCommunicationError.VERIFACTU_1152
		);
	}
	
	@Test
	void verifactu_1184_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.fra.setFacturaSinIdentifDestinatarioArt61D( null )
			, InvoiceCommunicationError.VERIFACTU_1184
		);
	}
	
	@Test
	void verifactu_1185_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setTipoFactura(ClaveTipoFacturaType.F_1);
				c.fra.setFacturaSinIdentifDestinatarioArt61D( CompletaSinDestinatarioType.S );
			}
			, InvoiceCommunicationError.VERIFACTU_1185
		);
	}
	
	@Test
	void verifactu_1137_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.fra.setMacrodato(null)
			, InvoiceCommunicationError.VERIFACTU_1137
		);
	}
	
	@Test
	void verifactu_1138_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setMacrodato(MacrodatoType.S);
				c.det.setBaseImponibleOimporteNoSujeto(VerifactuUtils.toString(5000));
				c.det.setTipoImpositivo(VerifactuUtils.toString(10));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(500));
				c.fra.setCuotaTotal(VerifactuUtils.toString(500));
				c.fra.setImporteTotal(VerifactuUtils.toString(5500));
			}
			, InvoiceCommunicationError.VERIFACTU_1138
		);
	}
	
	@Test
	void verifactu_1155_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setEmitidaPorTerceroODestinatario(null);
				c.fra.setTercero(new PersonaFisicaJuridicaType());
			}
			, InvoiceCommunicationError.VERIFACTU_1155
		);
	}
	
	@Test
	void verifactu_1186_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setEmitidaPorTerceroODestinatario(TercerosODestinatarioType.T);
				c.fra.setTercero(null);
			}
			,  InvoiceCommunicationError.VERIFACTU_1186
		);
	}

	@Test
	void verifactu_1187_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setEmitidaPorTerceroODestinatario(TercerosODestinatarioType.D);
				c.fra.setTercero(new PersonaFisicaJuridicaType());
			}
			, InvoiceCommunicationError.VERIFACTU_1187
		);
	}
	
	@Test
	void verifactu_1188_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setEmitidaPorTerceroODestinatario(TercerosODestinatarioType.T);
				PersonaFisicaJuridicaType tercero = new PersonaFisicaJuridicaType();
				tercero.setNIF( c.fra.getIDFactura().getIDEmisorFactura() );
				c.fra.setTercero(tercero);
			}
			, InvoiceCommunicationError.VERIFACTU_1188
		);
	}
	
	@Test
	void verifactu_1211_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setEmitidaPorTerceroODestinatario(TercerosODestinatarioType.T);
				PersonaFisicaJuridicaType tercero = new PersonaFisicaJuridicaType();
				tercero.setNIF( "11111111A" );
				c.fra.setTercero(tercero);
				tercero.setIDOtro(new IDOtroType());
			}
			, InvoiceCommunicationError.VERIFACTU_1211
		);
	}
	
	@Test
	void verifactu_1222_Test_0() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_INTRACOMUNITARIA.get( getEnvironment());
		assertInvoiceNoMessage( invoice, null
				, c -> {
					
				}
			);
		
	}

	@Test
	void verifactu_1222_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setEmitidaPorTerceroODestinatario(TercerosODestinatarioType.T);
				PersonaFisicaJuridicaType tercero = new PersonaFisicaJuridicaType();
				c.fra.setTercero(tercero);
				IDOtroType otro = new IDOtroType();
				otro.setIDType("07");
				tercero.setIDOtro(otro);
			}
			, InvoiceCommunicationError.VERIFACTU_1222
		);
	}
	
	@Test
	void verifactu_1111_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setEmitidaPorTerceroODestinatario(TercerosODestinatarioType.T);
				PersonaFisicaJuridicaType tercero = new PersonaFisicaJuridicaType();
				c.fra.setTercero(tercero);
				IDOtroType otro = new IDOtroType();
				otro.setIDType("03");
				tercero.setIDOtro(otro);
			}
			, InvoiceCommunicationError.VERIFACTU_1111
		);
	}
	
	@Test
	void verifactu_1126_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setEmitidaPorTerceroODestinatario(TercerosODestinatarioType.T);
				PersonaFisicaJuridicaType tercero = new PersonaFisicaJuridicaType();
				c.fra.setTercero(tercero);
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.ES);
				otro.setIDType("01");
				tercero.setIDOtro(otro);
			}
			, InvoiceCommunicationError.VERIFACTU_1126
		);
	}

	@Test
	void verifactu_1101_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setEmitidaPorTerceroODestinatario(TercerosODestinatarioType.T);
				PersonaFisicaJuridicaType tercero = new PersonaFisicaJuridicaType();
				c.fra.setTercero(tercero);
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.AR);	// Argentina
				otro.setIDType("02");
				tercero.setIDOtro(otro);
			}
			, InvoiceCommunicationError.VERIFACTU_1101
		);
	}
	
	@Test
	void verifactu_1222_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setEmitidaPorTerceroODestinatario(TercerosODestinatarioType.T);
				PersonaFisicaJuridicaType tercero = new PersonaFisicaJuridicaType();
				c.fra.setTercero(tercero);
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
	void verifactu_1122_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setEmitidaPorTerceroODestinatario(TercerosODestinatarioType.T);
				PersonaFisicaJuridicaType tercero = new PersonaFisicaJuridicaType();
				c.fra.setTercero(tercero);
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
	void verifactu_1190_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.fra.setTipoFactura(ClaveTipoFacturaType.F_2)
			, InvoiceCommunicationError.VERIFACTU_1190
		);
	}

	@Test
	void verifactu_1189_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.fra.setDestinatarios(null)
			, InvoiceCommunicationError.VERIFACTU_1189
		);
	}
	
	@Test
	void verifactu_1239_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Destinatarios destinatarios = new Destinatarios();
				c.fra.setDestinatarios(destinatarios);
				
				PersonaFisicaJuridicaType id = new PersonaFisicaJuridicaType();
				id.setNIF("11111111A");
				id.setIDOtro( new IDOtroType());
				destinatarios.getIDDestinatario().add( id );
				
			}
			, InvoiceCommunicationError.VERIFACTU_1239
		);
	}
	
	@Test
	void verifactu_1239_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Destinatarios destinatarios = new Destinatarios();
				c.fra.setDestinatarios(destinatarios);
				
				PersonaFisicaJuridicaType id = new PersonaFisicaJuridicaType();
				id.setNIF( null );
				id.setIDOtro( null );
				destinatarios.getIDDestinatario().add( id );
			}
			, InvoiceCommunicationError.VERIFACTU_1239
		);
	}

	@Test
	void verifactu_1126_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Destinatarios destinatarios = new Destinatarios();
				c.fra.setDestinatarios(destinatarios);
				
				PersonaFisicaJuridicaType id = new PersonaFisicaJuridicaType();
				IDOtroType idOtro = new IDOtroType();
				idOtro.setIDType("07");
				idOtro.setCodigoPais(CountryType2.FR);
				id.setIDOtro( idOtro );
				destinatarios.getIDDestinatario().add( id );
			}
			, InvoiceCommunicationError.VERIFACTU_1126
		);
	}
	
	@Test
	void verifactu_1111_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Destinatarios destinatarios = new Destinatarios();
				c.fra.setDestinatarios(destinatarios);
				
				PersonaFisicaJuridicaType id = new PersonaFisicaJuridicaType();
				IDOtroType idOtro = new IDOtroType();
				idOtro.setIDType("03");
				idOtro.setCodigoPais(null);
				id.setIDOtro( idOtro );
				destinatarios.getIDDestinatario().add( id );
			}
			, InvoiceCommunicationError.VERIFACTU_1111
		);
	}
	
	@Test
	void verifactu_1126_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Destinatarios destinatarios = new Destinatarios();
				c.fra.setDestinatarios(destinatarios);
				
				PersonaFisicaJuridicaType id = new PersonaFisicaJuridicaType();
				IDOtroType idOtro = new IDOtroType();
				idOtro.setIDType("03");
				idOtro.setCodigoPais(CountryType2.FR);
				id.setIDOtro( idOtro );
				destinatarios.getIDDestinatario().add( id );
			}
			, InvoiceCommunicationError.VERIFACTU_1126
		);
	}
	
		
	@Test
	void verifactu_1101_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Destinatarios destinatarios = new Destinatarios();
				PersonaFisicaJuridicaType id = new PersonaFisicaJuridicaType();
				IDOtroType idOtro = new IDOtroType();
				idOtro.setCodigoPais(CountryType2.AR);	// Argentina
				idOtro.setIDType("02");
				id.setIDOtro(idOtro);
				destinatarios.getIDDestinatario().add( id );
				c.fra.setDestinatarios(destinatarios);
			}
			, InvoiceCommunicationError.VERIFACTU_1101
		);
	}

	@Test
	void verifactu_1222_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Destinatarios destinatarios = new Destinatarios();
				PersonaFisicaJuridicaType id = new PersonaFisicaJuridicaType();
				IDOtroType idOtro = new IDOtroType();
				idOtro.setCodigoPais(CountryType2.FR);	// Francia
				idOtro.setIDType("02");
				idOtro.setID("FR0123456789");				// Debe tner 11 caracteres
				id.setIDOtro(idOtro);
				destinatarios.getIDDestinatario().add( id );
				c.fra.setDestinatarios(destinatarios);
			}
			, InvoiceCommunicationError.VERIFACTU_1222
		);
	}
	
	
	@Test
	void verifactu_1122_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Destinatarios destinatarios = new Destinatarios();
				PersonaFisicaJuridicaType id = new PersonaFisicaJuridicaType();
				IDOtroType idOtro = new IDOtroType();
				idOtro.setCodigoPais(CountryType2.FR);	// Francia
				idOtro.setIDType("02");
				idOtro.setID("DE234567890");			// pais en los dos primeros dígitos.  
				id.setIDOtro(idOtro);
				destinatarios.getIDDestinatario().add( id );
				c.fra.setDestinatarios(destinatarios);
			}
			, InvoiceCommunicationError.VERIFACTU_1122
		);
	}
	
	@Test
	void verifactu_1157_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setTipoFactura(ClaveTipoFacturaType.R_2);
				c.fra.setTipoRectificativa( ClaveTipoRectificativaType.I );
				c.fra.setCupon(CuponType.S);
			}
			, InvoiceCommunicationError.VERIFACTU_1157, InvoiceCommunicationError.VERIFACTU_4102
		);
	}
	
	@Test
	void verifactu_1124_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.det.setTipoImpositivo("23")
			, InvoiceCommunicationError.VERIFACTU_1124
		);
	}
	
	@Test
	void verifactu_1208_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setTipoImpositivo(null);
				c.fra.setImporteTotal(VerifactuUtils.toString(121));
			}
			, InvoiceCommunicationError.VERIFACTU_1208
		);

	}		
	@Test
	void verifactu_1208_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setCuotaRepercutida(null);
				c.fra.setCuotaTotal(VerifactuUtils.toString(0));
				c.fra.setImporteTotal(VerifactuUtils.toString(100));
			}
			, InvoiceCommunicationError.VERIFACTU_1208
		);
	}
		
	@Test
	void verifactu_1235_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2024);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(2));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(2));
				c.fra.setCuotaTotal(VerifactuUtils.toString(2));
				c.fra.setImporteTotal(VerifactuUtils.toString(102));
			}
			, InvoiceCommunicationError.VERIFACTU_1235
		);
	}
	
	@Test
	void verifactu_1235_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2025);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(2));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(2));
				c.fra.setCuotaTotal(VerifactuUtils.toString(2));
				c.fra.setImporteTotal(VerifactuUtils.toString(102));
			}
			, InvoiceCommunicationError.VERIFACTU_1235
		);
	}
	
	@Test
	void verifactu_1235_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceNoMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getDate(2024,10,1);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(2));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(2));
				c.fra.setCuotaTotal(VerifactuUtils.toString(2));
				c.fra.setImporteTotal(VerifactuUtils.toString(102));
			}
		);
	}
	
	@Test
	void verifactu_1194_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2022);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(5));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(5));
				c.fra.setCuotaTotal(VerifactuUtils.toString(5));
				c.fra.setImporteTotal(VerifactuUtils.toString(105));
			}
			, InvoiceCommunicationError.VERIFACTU_1194
		);
	}
	
	@Test
	void verifactu_1194_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2025);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(5));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(5));
				c.fra.setCuotaTotal(VerifactuUtils.toString(5));
				c.fra.setImporteTotal(VerifactuUtils.toString(105));
			}
			, InvoiceCommunicationError.VERIFACTU_1194
		);
	}
	
	@Test
	void verifactu_1194_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceNoMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getDate(2024,6,1);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(5));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(5));
				c.fra.setCuotaTotal(VerifactuUtils.toString(5));
				c.fra.setImporteTotal(VerifactuUtils.toString(105));
			}
		);
	}

	@Test
	void verifactu_1235_Test_4() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2024);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(7.5));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(7.5));
				c.fra.setCuotaTotal(VerifactuUtils.toString(7.5));
				c.fra.setImporteTotal(VerifactuUtils.toString(107.5));
			}
			, InvoiceCommunicationError.VERIFACTU_1235
		);
	}
	
	@Test
	void verifactu_1235_Test_5() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2025);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(7.5));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(7.5));
				c.fra.setCuotaTotal(VerifactuUtils.toString(7.5));
				c.fra.setImporteTotal(VerifactuUtils.toString(107.5));
			}
			, InvoiceCommunicationError.VERIFACTU_1235
		);
	}
	
	@Test
	void verifactu_1235_Test_6() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceNoMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getDate(2024,11,1);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(7.5));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(7.5));
				c.fra.setCuotaTotal(VerifactuUtils.toString(7.5));
				c.fra.setImporteTotal(VerifactuUtils.toString(107.5));
			}
		);
	}

	@Test
	void verifactu_1257_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.det.setBaseImponibleACoste(VerifactuUtils.toString(100.5))
			, InvoiceCommunicationError.VERIFACTU_1257
		);
	}
	
	@Test
	void verifactu_1257_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceNoMessage( invoice, null
			, c -> {
				c.det.setClaveRegimen(ClaveRegimen.C06.getValue());
				c.det.setBaseImponibleACoste(VerifactuUtils.toString(100.5));
			}
		);
	}

	@Test
	void verifactu_1257_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceNoMessage( invoice, null
			, c -> {
				c.det.setImpuesto("02");
				c.det.setClaveRegimen(null);
				c.det.setBaseImponibleACoste(VerifactuUtils.toString(100.5));
			}
		);
	}

	@Test
	void verifactu_1279_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.det.setClaveRegimen(ClaveRegimen.C01_NATIONAL.getValue())
			, InvoiceCommunicationError.VERIFACTU_1279
		);
	}
	
	@Test
	void verifactu_1280_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.det.setTipoRecargoEquivalencia(null)
			, InvoiceCommunicationError.VERIFACTU_1280
		);
	}
			
	@Test
	void verifactu_1280_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.det.setCuotaRecargoEquivalencia(null)
			, InvoiceCommunicationError.VERIFACTU_1280
		);
	}
	
	@Test
	void verifactu_1281_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.det.setCalificacionOperacion(CalificacionOperacionType.S_2)
			, InvoiceCommunicationError.VERIFACTU_1281, InvoiceCommunicationError.VERIFACTU_1198, InvoiceCommunicationError.VERIFACTU_1207
		);
	}
	
	@Test
	void verifactu_1127_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(8.5))
			, InvoiceCommunicationError.VERIFACTU_1127
		);
	}
	

	@Test
	void verifactu_1165_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2022);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0));
			}
			, InvoiceCommunicationError.VERIFACTU_1165
		);
	}
	
	@Test
	void verifactu_1165_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2025);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0));
			}
			, InvoiceCommunicationError.VERIFACTU_1165
		);
	}
	
	@Test
	void verifactu_1165_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceNoMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getDate(2024,3,1);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0));
			}
		);
	}


	@Test
	void verifactu_1166_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2022);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0.26));
				c.det.setTipoImpositivo(VerifactuUtils.toString(2));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(2));
				c.fra.setCuotaTotal(VerifactuUtils.toString(2));
				c.fra.setImporteTotal(VerifactuUtils.toString(102));
			}
			, InvoiceCommunicationError.VERIFACTU_1166, InvoiceCommunicationError.VERIFACTU_1235
		);
	}
	
	@Test
	void verifactu_1166_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2025);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0.26));
				c.det.setTipoImpositivo(VerifactuUtils.toString(2));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(2));
				c.fra.setCuotaTotal(VerifactuUtils.toString(2));
				c.fra.setImporteTotal(VerifactuUtils.toString(102));
			}
			, InvoiceCommunicationError.VERIFACTU_1166, InvoiceCommunicationError.VERIFACTU_1235
		);
	}
	
	@Test
	void verifactu_1166_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceNoMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getDate(2024,11,1);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0.26));
				c.det.setTipoImpositivo(VerifactuUtils.toString(2));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(2));
				c.fra.setCuotaTotal(VerifactuUtils.toString(2));
				c.fra.setImporteTotal(VerifactuUtils.toString(102));
			}
		);
	}

	@Test
	void verifactu_1170_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2022);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0.26));
				c.det.setTipoImpositivo(VerifactuUtils.toString(0));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(0));
				c.fra.setCuotaTotal(VerifactuUtils.toString(0));
				c.fra.setImporteTotal(VerifactuUtils.toString(100.26));
			}
			, InvoiceCommunicationError.VERIFACTU_1170
		);
	}
	
	@Test
	void verifactu_1170_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceNoMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2025);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0.26));
				c.det.setTipoImpositivo(VerifactuUtils.toString(0));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(0));
				c.fra.setCuotaTotal(VerifactuUtils.toString(0));
				c.fra.setImporteTotal(VerifactuUtils.toString(100.26));
			}
		);
	}
	
	@Test
	void verifactu_1127_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0.26));
				c.det.setTipoImpositivo(VerifactuUtils.toString(4));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(4));
				c.fra.setCuotaTotal(VerifactuUtils.toString(4));
				c.fra.setImporteTotal(VerifactuUtils.toString(104));
			}
			, InvoiceCommunicationError.VERIFACTU_1127
		);
	}
		
	@Test
	void verifactu_1167_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2022);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(5));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(5));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0.5));
				c.fra.setCuotaTotal(VerifactuUtils.toString(5.5));
				c.fra.setImporteTotal(VerifactuUtils.toString(105.5));
			}
			, InvoiceCommunicationError.VERIFACTU_1167, InvoiceCommunicationError.VERIFACTU_1194
		);
	}
	
	@Test
	void verifactu_1167_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2023);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(5));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(5));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0.5));
				c.fra.setCuotaTotal(VerifactuUtils.toString(5.5));
				c.fra.setImporteTotal(VerifactuUtils.toString(105.5));
			}
			, InvoiceCommunicationError.VERIFACTU_1167
		);
	}
	
	@Test
	void verifactu_1167_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceNoMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getDate(2022,8,1);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(5));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(5));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0.5));
				c.fra.setCuotaTotal(VerifactuUtils.toString(5.5));
				c.fra.setImporteTotal(VerifactuUtils.toString(105.5));
			}
		);
	}

	@Test
	void verifactu_1164_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setTipoImpositivo(VerifactuUtils.toString(10));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(10));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0.5));
				c.det.setCuotaRecargoEquivalencia(VerifactuUtils.toString(0.5));
				c.fra.setCuotaTotal(VerifactuUtils.toString(10.5));
				c.fra.setImporteTotal(VerifactuUtils.toString(110.5));
			}
			, InvoiceCommunicationError.VERIFACTU_1164
		);
	}

	@Test
	void verifactu_1168_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2022);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(5));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(5));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0.62));
				c.fra.setCuotaTotal(VerifactuUtils.toString(5.62));
				c.fra.setImporteTotal(VerifactuUtils.toString(105.62));
			}
			, InvoiceCommunicationError.VERIFACTU_1168, InvoiceCommunicationError.VERIFACTU_1194
		);
	}
	
	@Test
	void verifactu_1168_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2025);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(5));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(5));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0.62));
				c.fra.setCuotaTotal(VerifactuUtils.toString(5.62));
				c.fra.setImporteTotal(VerifactuUtils.toString(105.62));
			}
			, InvoiceCommunicationError.VERIFACTU_1168, InvoiceCommunicationError.VERIFACTU_1194
		);
	}
	
	@Test
	void verifactu_1168_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getDate(2024,1,1);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(4));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(4));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0.62));
				c.fra.setCuotaTotal(VerifactuUtils.toString(4.62));
				c.fra.setImporteTotal(VerifactuUtils.toString(104.62));
			}
			, InvoiceCommunicationError.VERIFACTU_1168
		);
	}

	@Test
	void verifactu_1168_Test_4() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceNoMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getDate(2024,1,1);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(5));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(5));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(0.62));
				c.fra.setCuotaTotal(VerifactuUtils.toString(5.62));
				c.fra.setImporteTotal(VerifactuUtils.toString(105.62));
			}
		);
	}
	
	@Test
	void verifactu_1169_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2022);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(7.5));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(7.5));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(1));
				c.det.setCuotaRecargoEquivalencia(VerifactuUtils.toString(1));
				c.fra.setCuotaTotal(VerifactuUtils.toString(8.5));
				c.fra.setImporteTotal(VerifactuUtils.toString(108.5));
			}
			, InvoiceCommunicationError.VERIFACTU_1169, InvoiceCommunicationError.VERIFACTU_1235
		);
	}
	
	@Test
	void verifactu_1169_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getYearFirstDay(2025);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(7.5));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(8.5));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(1));
				c.det.setCuotaRecargoEquivalencia(VerifactuUtils.toString(1));
				c.fra.setCuotaTotal(VerifactuUtils.toString(9.5));
				c.fra.setImporteTotal(VerifactuUtils.toString(109.5));
			}
			, InvoiceCommunicationError.VERIFACTU_1169, InvoiceCommunicationError.VERIFACTU_1235
		);
	}
	
	@Test
	void verifactu_1169_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getDate(2024,9,20);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(4));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(4));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(1));
				c.fra.setCuotaTotal(VerifactuUtils.toString(4));
				c.fra.setImporteTotal(VerifactuUtils.toString(104));
			}
			, InvoiceCommunicationError.VERIFACTU_1169
		);
	}

	@Test
	void verifactu_1169_Test_4() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceNoMessage( invoice, null
			, c -> {
				Date date = AonDateUtils.getDate(2024,9,20);
				c.fra.setFechaOperacion(VerifactuUtils.toString(date));
				c.det.setTipoImpositivo(VerifactuUtils.toString(7.5));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(7.5));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(1));
				c.det.setCuotaRecargoEquivalencia(VerifactuUtils.toString(1));
				c.fra.setCuotaTotal(VerifactuUtils.toString(8.5));
				c.fra.setImporteTotal(VerifactuUtils.toString(108.5	));	
			}
		);
	}
	
	@Test
	void verifactu_1163_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setTipoImpositivo(VerifactuUtils.toString(21));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(1.4));
			}
			, InvoiceCommunicationError.VERIFACTU_1163
		);
	}
	
	@Test
	void verifactu_1163_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceNoMessage( invoice, null
			, c -> {
				c.det.setTipoImpositivo(VerifactuUtils.toString(10));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(10));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(1.4));
				c.det.setCuotaRecargoEquivalencia(VerifactuUtils.toString(1.4));
				c.fra.setCuotaTotal(VerifactuUtils.toString(11.4));
				c.fra.setImporteTotal(VerifactuUtils.toString(111.4 ));
			}
		);
	}
	

	@Test
	void verifactu_1162_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setTipoImpositivo(VerifactuUtils.toString(10));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(10));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(1.75));
				c.det.setCuotaRecargoEquivalencia(VerifactuUtils.toString(1.75));
				c.fra.setCuotaTotal(VerifactuUtils.toString(11.75));
				c.fra.setImporteTotal(VerifactuUtils.toString(111.75 ));
			}
			, InvoiceCommunicationError.VERIFACTU_1162
		);
	}
	
	@Test
	void verifactu_1162_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceNoMessage( invoice, null
			, c -> {
				c.det.setTipoImpositivo(VerifactuUtils.toString(21));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(1.75));
			}
		);
	}

	@Test
	void verifactu_1162_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setTipoImpositivo(VerifactuUtils.toString(10));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(10));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(5.2));
				c.det.setCuotaRecargoEquivalencia(VerifactuUtils.toString(5.2));
				c.fra.setCuotaTotal(VerifactuUtils.toString(15.2 ));
				c.fra.setImporteTotal(VerifactuUtils.toString(115.2 ));
			}
			, InvoiceCommunicationError.VERIFACTU_1162
		);
	}
	
	@Test
	void verifactu_1162_Test_4() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get( getEnvironment());
		assertInvoiceNoMessage( invoice, null
			, c -> {
				c.det.setTipoImpositivo(VerifactuUtils.toString(21));
				c.det.setTipoRecargoEquivalencia(VerifactuUtils.toString(5.2));
			}
		);
	}
	
	@Test
	void verifactu_1197_Test_4() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setTipoFactura( ClaveTipoFacturaType.F_2 );
				c.det.setCalificacionOperacion(CalificacionOperacionType.S_2);
			}
			, InvoiceCommunicationError.VERIFACTU_1197 , InvoiceCommunicationError.VERIFACTU_1207
		);
	}
	
	@Test
	void verifactu_1198_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_ISP.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setCalificacionOperacion(CalificacionOperacionType.S_2);
				c.det.setTipoImpositivo(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1198
		);
	}
	
	@Test
	void verifactu_1198_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_ISP.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setCalificacionOperacion(CalificacionOperacionType.S_2);
				c.det.setCuotaRepercutida(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1198
		);
	}
	
	@Test
	void verifactu_1237_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setCalificacionOperacion(CalificacionOperacionType.N_1);
				c.det.setTipoImpositivo("21"); 
				c.det.setCuotaRepercutida(null);
				c.det.setTipoRecargoEquivalencia(null);
				c.det.setCuotaRecargoEquivalencia(null);
				c.fra.setCuotaTotal(VerifactuUtils.toString(0));
				c.fra.setImporteTotal(VerifactuUtils.toString(100));
			}
			, InvoiceCommunicationError.VERIFACTU_1237
		);
	}
	
	@Test
	void verifactu_1237_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setCalificacionOperacion(CalificacionOperacionType.N_1);
				c.det.setTipoImpositivo(null); 
				c.det.setCuotaRepercutida("21");
				c.det.setTipoRecargoEquivalencia(null);
				c.det.setCuotaRecargoEquivalencia(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1237, InvoiceCommunicationError.VERIFACTU_1207
		);
	}

	@Test
	void verifactu_1237_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setCalificacionOperacion(CalificacionOperacionType.N_1);
				c.det.setTipoImpositivo(null); 
				c.det.setCuotaRepercutida(null);
				c.det.setTipoRecargoEquivalencia("1");
				c.det.setCuotaRecargoEquivalencia(null);
				c.fra.setCuotaTotal(VerifactuUtils.toString(0));
				c.fra.setImporteTotal(VerifactuUtils.toString(100));
			}
			, InvoiceCommunicationError.VERIFACTU_1237, InvoiceCommunicationError.VERIFACTU_1279
		);
	}

	@Test
	void verifactu_1237_Test_4() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setCalificacionOperacion(CalificacionOperacionType.N_1);
				c.det.setTipoImpositivo(null); 
				c.det.setCuotaRepercutida(null);
				c.det.setTipoRecargoEquivalencia(null);
				c.det.setCuotaRecargoEquivalencia("21");
			}
			, InvoiceCommunicationError.VERIFACTU_1237, InvoiceCommunicationError.VERIFACTU_1279
		);
	}

	@Test
	void verifactu_1237_Test_5() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setCalificacionOperacion(CalificacionOperacionType.N_2);
				c.det.setTipoImpositivo("21"); 
				c.det.setCuotaRepercutida(null);
				c.det.setTipoRecargoEquivalencia(null);
				c.det.setCuotaRecargoEquivalencia(null);
				c.fra.setCuotaTotal(VerifactuUtils.toString(0));
				c.fra.setImporteTotal(VerifactuUtils.toString(100));
			}
			, InvoiceCommunicationError.VERIFACTU_1237
		);
	}
	
	@Test
	void verifactu_1237_Test_6() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setCalificacionOperacion(CalificacionOperacionType.N_2);
				c.det.setTipoImpositivo(null); 
				c.det.setCuotaRepercutida("21");
				c.det.setTipoRecargoEquivalencia(null);
				c.det.setCuotaRecargoEquivalencia(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1237, InvoiceCommunicationError.VERIFACTU_1207
		);
	}

	@Test
	void verifactu_1237_Test_7() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setCalificacionOperacion(CalificacionOperacionType.N_2);
				c.det.setTipoImpositivo(null); 
				c.det.setCuotaRepercutida(null);
				c.det.setTipoRecargoEquivalencia("1");
				c.det.setCuotaRecargoEquivalencia(null);
				c.fra.setCuotaTotal(VerifactuUtils.toString(0));
				c.fra.setImporteTotal(VerifactuUtils.toString(100));
			}
			, InvoiceCommunicationError.VERIFACTU_1237, InvoiceCommunicationError.VERIFACTU_1279
		);
	}

	@Test
	void verifactu_1237_Test_8() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setCalificacionOperacion(CalificacionOperacionType.N_2);
				c.det.setTipoImpositivo(null); 
				c.det.setCuotaRepercutida(null);
				c.det.setTipoRecargoEquivalencia(null);
				c.det.setCuotaRecargoEquivalencia("21");
			}
			, InvoiceCommunicationError.VERIFACTU_1237, InvoiceCommunicationError.VERIFACTU_1279
		);
	}
	
	@Test
	void verifactu_1196_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setCalificacionOperacion(CalificacionOperacionType.S_1);
				c.det.setOperacionExenta(OperacionExentaType.E_1); 
			}
			, InvoiceCommunicationError.VERIFACTU_1196
		);
	}

	@Test
	void verifactu_1195_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setCalificacionOperacion(null);
				c.det.setOperacionExenta(null); 
			}
			, InvoiceCommunicationError.VERIFACTU_1195, InvoiceCommunicationError.VERIFACTU_1207
		);
	}
	
	@Test
	void verifactu_1238_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_EXENTA_E1.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setTipoImpositivo("21"); 
				c.det.setCuotaRepercutida(null);
				c.det.setTipoRecargoEquivalencia(null);
				c.det.setCuotaRecargoEquivalencia(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1238
		);
	}
	
	@Test
	void verifactu_1238_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_EXENTA_E1.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setTipoImpositivo(null); 
				c.det.setCuotaRepercutida(VerifactuUtils.toString(21));
				c.det.setTipoRecargoEquivalencia(null);
				c.det.setCuotaRecargoEquivalencia(null);
				c.fra.setCuotaTotal(VerifactuUtils.toString(21));
				c.fra.setImporteTotal(VerifactuUtils.toString(121));
			}
			, InvoiceCommunicationError.VERIFACTU_1238, InvoiceCommunicationError.VERIFACTU_1207
		);
	}

	@Test
	void verifactu_1238_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_EXENTA_E1.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setTipoImpositivo(null); 
				c.det.setCuotaRepercutida(null);
				c.det.setTipoRecargoEquivalencia("1");
				c.det.setCuotaRecargoEquivalencia(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1238, InvoiceCommunicationError.VERIFACTU_1279
		);
	}

	@Test
	void verifactu_1238_Test_4() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_EXENTA_E1.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setTipoImpositivo(null); 
				c.det.setCuotaRepercutida(null);
				c.det.setTipoRecargoEquivalencia(null);
				c.det.setCuotaRecargoEquivalencia(VerifactuUtils.toString(21));
				c.fra.setCuotaTotal(VerifactuUtils.toString(21));
				c.fra.setImporteTotal(VerifactuUtils.toString(121));
			}
			, InvoiceCommunicationError.VERIFACTU_1238, InvoiceCommunicationError.VERIFACTU_1279
		);
	}
	
	@Test
	void verifactu_1199_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_EXENTA_E1.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.det.setOperacionExenta(OperacionExentaType.E_2)
			, InvoiceCommunicationError.VERIFACTU_1199
		);
	}

	@Test
	void verifactu_1199_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_EXENTA_E1.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.det.setOperacionExenta(OperacionExentaType.E_3)
			, InvoiceCommunicationError.VERIFACTU_1199
		);
	}
	
	@Test
	void verifactu_1245_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.det.setClaveRegimen(null)
			, InvoiceCommunicationError.VERIFACTU_1245
		);
	}
	
	@Test
	void verifactu_1246_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.det.setClaveRegimen("KO")
			, InvoiceCommunicationError.VERIFACTU_1246
		);
	}
	
	@Test
	void verifactu_1260_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.det.setImpuesto("04")
			, InvoiceCommunicationError.VERIFACTU_1260
		);
	}
	
	@Test
	void verifactu_1182_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_EXTRACOMUNITARIA.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.det.setOperacionExenta(null)
			, InvoiceCommunicationError.VERIFACTU_1182, InvoiceCommunicationError.VERIFACTU_1195
		);
	}
	
	@Test
	void verifactu_1200_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setClaveRegimen("03");
				c.det.setTipoImpositivo(VerifactuUtils.toString(0));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(0));
				c.fra.setCuotaTotal(VerifactuUtils.toString(0));
				c.det.setCalificacionOperacion(CalificacionOperacionType.S_2);
			}
			, InvoiceCommunicationError.VERIFACTU_1200
		);
	}

	@Test
	void verifactu_1201_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setClaveRegimen("04");
				c.det.setTipoImpositivo(VerifactuUtils.toString(0));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(0));
				c.fra.setCuotaTotal(VerifactuUtils.toString(0));
				c.fra.setImporteTotal(VerifactuUtils.toString(100));
				c.det.setCalificacionOperacion(CalificacionOperacionType.S_1);
			}
			, InvoiceCommunicationError.VERIFACTU_1201
		);
	}
	
	@Test
	void verifactu_1202_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.det.setClaveRegimen("06")
			, InvoiceCommunicationError.VERIFACTU_1202
		);
	}

	@Test
	void verifactu_1202_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.det.setClaveRegimen("06")
			, InvoiceCommunicationError.VERIFACTU_1202
		);
	}

	@Test
	void verifactu_1203_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE_CRITERIO_CAJA.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.det.setCalificacionOperacion(CalificacionOperacionType.S_2)
			, InvoiceCommunicationError.VERIFACTU_1203, InvoiceCommunicationError.VERIFACTU_1198, InvoiceCommunicationError.VERIFACTU_1207
		);
	}

	@Test
	void verifactu_1203_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE_CRITERIO_CAJA.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setCalificacionOperacion(null);
				c.det.setTipoImpositivo(null);
				c.det.setCuotaRepercutida(null);
				c.det.setOperacionExenta(OperacionExentaType.E_2);
				c.fra.setCuotaTotal(VerifactuUtils.toString(0));
				c.fra.setImporteTotal(VerifactuUtils.toString(100));
			}
			, InvoiceCommunicationError.VERIFACTU_1203
		);
	}
	
	@Test
	void verifactu_1252_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.det.setClaveRegimen("08")
			, InvoiceCommunicationError.VERIFACTU_1252
		);
	}
	
	@Test
	void verifactu_1205_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.det.setClaveRegimen("10")
			, InvoiceCommunicationError.VERIFACTU_1205
		);
	}

	@Test
	void verifactu_1205_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setClaveRegimen("10");
				c.det.setCalificacionOperacion(CalificacionOperacionType.N_1);
				c.det.setTipoImpositivo(null);
				c.det.setCuotaRepercutida(null);
				c.det.setTipoRecargoEquivalencia(null);
				c.det.setCuotaRecargoEquivalencia(null);
				c.fra.setCuotaTotal(VerifactuUtils.toString(0));
				c.fra.setImporteTotal(VerifactuUtils.toString(104));
			}
			, InvoiceCommunicationError.VERIFACTU_1205
		);
	}
	
	@Test
	void verifactu_1205_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				PersonaFisicaJuridicaType d = c.fra.getDestinatarios().getIDDestinatario().get(0);
				d.setNIF(null);
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.ES);
				otro.setIDType("03");
				otro.setID("ddddddddd");
				d.setIDOtro(otro);
				c.det.setClaveRegimen("10");
				c.det.setCalificacionOperacion(CalificacionOperacionType.N_1);
				c.det.setTipoImpositivo(null);
				c.det.setCuotaRepercutida(null);
				c.det.setTipoRecargoEquivalencia(null);
				c.det.setCuotaRecargoEquivalencia(null);
				c.fra.setCuotaTotal(VerifactuUtils.toString(0));
				c.fra.setImporteTotal(VerifactuUtils.toString(100));
			}
			, InvoiceCommunicationError.VERIFACTU_1205
		);
	}

	@Test
	void verifactu_1206_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setTipoImpositivo(VerifactuUtils.toString(10));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(10));
				c.det.setClaveRegimen("11");
				c.fra.setCuotaTotal(VerifactuUtils.toString(10));
				c.fra.setImporteTotal(VerifactuUtils.toString(110));
			}
			, InvoiceCommunicationError.VERIFACTU_1206
		);
	}

	@Test
	void verifactu_1147_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setClaveRegimen("14");
				c.fra.setFechaOperacion(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1147
		);
	}

	@Test
	void verifactu_1147_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setClaveRegimen("14");
				Date exp = VerifactuUtils.toDate(c.fra.getIDFactura().getFechaExpedicionFactura());
				Date ope = AonDateUtils.addDays(exp, -5);
				c.fra.setFechaOperacion(VerifactuUtils.toString(ope));
			}
			, InvoiceCommunicationError.VERIFACTU_1147
		);
	}
	
	@Test
	void verifactu_1148_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				Date exp = VerifactuUtils.toDate(c.fra.getIDFactura().getFechaExpedicionFactura());
				Date ope = AonDateUtils.addDays(exp, 5);
				c.fra.setFechaOperacion(VerifactuUtils.toString(ope));
				c.fra.setTipoFactura(ClaveTipoFacturaType.F_3);
				c.det.setClaveRegimen("14");
			}
			, InvoiceCommunicationError.VERIFACTU_1148
		);
	}

	@Test
	void verifactu_1149_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				Date exp = VerifactuUtils.toDate(c.fra.getIDFactura().getFechaExpedicionFactura());
				Date ope = AonDateUtils.addDays(exp, 5);
				c.fra.setFechaOperacion(VerifactuUtils.toString(ope));
				c.fra.setDestinatarios(null);
				c.det.setClaveRegimen("14");
			}
			, InvoiceCommunicationError.VERIFACTU_1149, InvoiceCommunicationError.VERIFACTU_1189
		);
	}

	@Test
	void verifactu_1149_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setClaveRegimen("14");
				Date exp = VerifactuUtils.toDate(c.fra.getIDFactura().getFechaExpedicionFactura());
				Date ope = AonDateUtils.addDays(exp, 5);
				c.fra.setFechaOperacion(VerifactuUtils.toString(ope));
				PersonaFisicaJuridicaType d = c.fra.getDestinatarios().getIDDestinatario().get(0);
				d.setNIF(null);
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.ES);
				otro.setIDType("03");
				otro.setID("ddddddddd");
				d.setIDOtro(otro);
			}
			, InvoiceCommunicationError.VERIFACTU_1149
		);
	}

	@Test
	void verifactu_1143_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setBaseImponibleACoste(null);
				c.det.setBaseImponibleOimporteNoSujeto(VerifactuUtils.toString(-100));
				c.det.setTipoImpositivo(VerifactuUtils.toString(21));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(21));
				c.fra.setImporteTotal(VerifactuUtils.toString(121));
			}
			, InvoiceCommunicationError.VERIFACTU_1143, InvoiceCommunicationError.VERIFACTU_1210
		);
	}
	
	@Test
	void verifactu_1142_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setBaseImponibleACoste(null);
				c.det.setBaseImponibleOimporteNoSujeto(VerifactuUtils.toString(100));
				c.det.setTipoImpositivo(VerifactuUtils.toString(21));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(32));
				c.fra.setCuotaTotal(VerifactuUtils.toString(32));
				c.fra.setImporteTotal(VerifactuUtils.toString(132));
			}
			, InvoiceCommunicationError.VERIFACTU_1142
		);
	}

	@Test
	void verifactu_1140_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setClaveRegimen("06");
				c.det.setBaseImponibleOimporteNoSujeto(null);
				c.det.setBaseImponibleACoste(VerifactuUtils.toString(-100));
				c.det.setTipoImpositivo(VerifactuUtils.toString(21));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(21));
			}
			, InvoiceCommunicationError.VERIFACTU_1140
		);
	}
	
	@Test
	void verifactu_1144_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setClaveRegimen("06");
				c.det.setBaseImponibleOimporteNoSujeto(null);
				c.det.setBaseImponibleACoste(VerifactuUtils.toString(100));
				c.det.setTipoImpositivo(VerifactuUtils.toString(21));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(32));
				c.fra.setCuotaTotal(VerifactuUtils.toString(32));
			}
			, InvoiceCommunicationError.VERIFACTU_1144
		);
	}
	
	@Test
	void verifactu_1150_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.det.setBaseImponibleACoste(null);
				c.det.setBaseImponibleOimporteNoSujeto(VerifactuUtils.toString(5000));
				c.det.setTipoImpositivo(VerifactuUtils.toString(10));
				c.det.setCuotaRepercutida(VerifactuUtils.toString(500));
				c.fra.setCuotaTotal(VerifactuUtils.toString(500));
				c.fra.setImporteTotal(VerifactuUtils.toString(5500));
				c.fra.setFacturaSinIdentifDestinatarioArt61D(CompletaSinDestinatarioType.N);
			}
			, InvoiceCommunicationError.VERIFACTU_1150
		);
	}
	
	@Test
	void verifactu_1216_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setCuotaTotal(VerifactuUtils.toString(500));
			}
			, InvoiceCommunicationError.VERIFACTU_1216
		);
	}

	@Test
	void verifactu_1210_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setImporteTotal(VerifactuUtils.toString(500));
			}
			, InvoiceCommunicationError.VERIFACTU_1210
		);
	}
	
	@Test
	void verifactu_2000_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setHuella(null);
			}
			, InvoiceCommunicationError.VERIFACTU_2000
		);
	}
	
	@Test
	void verifactu_2000_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setHuella("huella incorrecta");
			}
			, InvoiceCommunicationError.VERIFACTU_2000
		);
	}
		
	@Test
	void verifactu_1179_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.setSistemaInformatico(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1179
		);
	}
	
	@Test
	void verifactu_1223_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.getSistemaInformatico().setNIF(null);
				c.fra.getSistemaInformatico().setIDOtro(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1223
		);
	}
	
	@Test
	void verifactu_1223_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> {
				c.fra.getSistemaInformatico().setIDOtro(new IDOtroType());
			}
			, InvoiceCommunicationError.VERIFACTU_1223
		);
	}
	
	@Test
	void verifactu_1221_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				IDOtroType otro = new IDOtroType();
				otro.setIDType("07");
				c.fra.getSistemaInformatico().setIDOtro(otro);
				c.fra.getSistemaInformatico().setNIF(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1221
		);
	}
	
	@Test
	void verifactu_1221_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				IDOtroType otro = new IDOtroType();
				otro.setIDType("03");
				c.fra.getSistemaInformatico().setIDOtro(otro);
				c.fra.getSistemaInformatico().setNIF(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1221
		);
	}
	
	@Test
	void verifactu_1221_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.ES);
				otro.setIDType("01");
				c.fra.getSistemaInformatico().setIDOtro(otro);
				c.fra.getSistemaInformatico().setNIF(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1221
		);
	}
	
	@Test
	void verifactu_1221_Test_4() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.AR);	// Argentina
				otro.setIDType("02");
				c.fra.getSistemaInformatico().setIDOtro(otro);
				c.fra.getSistemaInformatico().setNIF(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1221
		);
	}
	
	@Test
	void verifactu_1221_Test_5() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.FR);	// Francia
				otro.setIDType("02");
				otro.setID("0123456789");				// Debe tner 11 caracteres
				c.fra.getSistemaInformatico().setIDOtro(otro);
				c.fra.getSistemaInformatico().setNIF(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1221
		);
	}
	
	
	@Test
	void verifactu_1221_Test_6() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment());
		assertInvoiceMessage( invoice, null
			, c -> {
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.FR);	// Francia
				otro.setIDType("02");
				otro.setID("DE234567890");				// pais en los dos primeros dígitos.  
				c.fra.getSistemaInformatico().setIDOtro(otro);
				c.fra.getSistemaInformatico().setNIF(null);
			}
			, InvoiceCommunicationError.VERIFACTU_1221
		);
	}
	
	@Test
	void verifactu_1177_Test_1() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.fra.getSistemaInformatico().setIdSistemaInformatico(null)
			, InvoiceCommunicationError.VERIFACTU_1177
		);
	}
	
	@Test
	void verifactu_1177_Test_2() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.fra.getSistemaInformatico().setIdSistemaInformatico("ÑS")
			, InvoiceCommunicationError.VERIFACTU_1177
		);
	}
	
	@Test
	void verifactu_1177_Test_3() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.fra.getSistemaInformatico().setIdSistemaInformatico("")
			, InvoiceCommunicationError.VERIFACTU_1177
		);
	}

	@Test
	void verifactu_1177_Test_4() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.fra.getSistemaInformatico().setIdSistemaInformatico("ss")
			, InvoiceCommunicationError.VERIFACTU_1177
		);
	}
	
	@Test
	void verifactu_1220_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.fra.getSistemaInformatico().setNombreSistemaInformatico(null)
			, InvoiceCommunicationError.VERIFACTU_1220
		);
	}

	@Test
	void verifactu_1212_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.fra.getSistemaInformatico().setTipoUsoPosibleSoloVerifactu(null)
			, InvoiceCommunicationError.VERIFACTU_1212
		);
	}

	@Test
	void verifactu_1213_Test() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get( getEnvironment()).setId(1);
		assertInvoiceMessage( invoice, null
			, c -> c.fra.getSistemaInformatico().setTipoUsoPosibleMultiOT(null)
			, InvoiceCommunicationError.VERIFACTU_1213
		);
	}
	
}
