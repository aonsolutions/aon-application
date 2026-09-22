package net.aonsolutions.aon.sii.aeat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.Iae;
import com.esferalia.aon.occam.api.model.aonsolutions.AonSecret;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.watson.server.AonDateUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.EstadoEnvioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.EstadoRegistroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaExpedidaBajaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaExpedidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaLRBajaFEmitidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaLRFEmitidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CausaExencionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.ClaveTipoComunicacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.ClaveTipoFacturaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.DetalleExentaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.DetalleIVAEmitidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.FacturaExpedidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaARType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaPrestacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.TipoOperacionSujetaNoExentaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.BajaLRFacturasEmitidas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRBajaExpedidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRfacturasEmitidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.SuministroLRFacturasEmitidas;
import net.aonsolutions.aon.sii.SIIType;
import net.aonsolutions.aon.sii.SIIUri;

/**
 * Alta y anulacion de facturas emitidas en el entorno de pruebas del SII de la
 * AEAT.
 *
 * El alta se construye con FacturasEmitidas.suministroFacturasEmitidas y la
 * anulacion con FacturasEmitidasBaja.bajaFacturasEmitidas, las dos a partir del
 * contexto de comunicacion, y se envian con SIIAeatPost. La configuracion esta
 * en modo test, que es lo que lleva los envios al entorno de pruebas
 * (https://prewww1.aeat.es/wlpl/SSII-FACT/ws/fe/SiiFactFEV1SOAP, ver
 * {@link SIIUri}).
 *
 * Los envios se firman con el certificado de firma de AON, el mismo que usan los
 * tests de verifactu (VerifactuEnvironment.configurationWithCertificate) y los
 * de TicketBAI de Alava. El titular del libro registro es el titular del
 * certificado, para que el remitente y el titular coincidan.
 *
 * Los tests que envian de verdad estan etiquetados como "envio" y necesitan
 * acceso a la red y al secreto de AWS con el certificado. Para excluirlos:
 * mvn test -DexcludedGroups=envio
 */
class FacturasEmitidasEnvioTest {

	/** Titular del certificado de firma de AON, que es el titular del libro registro. */
	private static final String NIF_TITULAR = "B01487271";
	private static final String NOMBRE_TITULAR = "AON SOLUTIONS, S.L.";

	private static final String NIF_DESTINATARIO = "B00000034";
	private static final String NOMBRE_DESTINATARIO = "CONSULTORIA GALDETU";

	private static final String EPIGRAFE = "197330";

	private static final String DESCRIPCION_DETALLE = "Servicio de pruebas SII";

	private static final String URI_PRUEBAS = "https://prewww1.aeat.es/wlpl/SSII-FACT/ws/fe/SiiFactFEV1SOAP";

	private static final double BASE = 100.0;
	private static final double TIPO = 21.0;
	private static final double CUOTA = 21.0;
	private static final double TOTAL = 121.0;

	private static final String FORMATO_FECHA = "dd-MM-yyyy";

	// *****************************************************************
	// ********************** [CONFIGURACION] **************************
	// *****************************************************************

	/**
	 * Configuracion de comunicacion en modo test. El modo test se determina por el
	 * texto de la configuracion de SII, y es el que lleva el envio al entorno de
	 * pruebas de la AEAT.
	 */
	private InvoiceCommunicationConfiguration config() {
		return new InvoiceCommunicationConfiguration()
			.setAdministration(Administration.COMMON_TERRITORY)
			.setSiiData(new EnterpriseData()
				.setId(1)
				.setExpression("SII TEST")
				.setStartDate(new Date(0L)));
	}

	/**
	 * Configuracion con la que se envian los mensajes, con el certificado de firma
	 * de AON.
	 */
	private InvoiceCommunicationConfiguration configEnvio() {
		return config().setCertificate(AonSecret.getSigCert());
	}

	private Company company() {
		return (Company) new Company()
			.setDocument(NIF_TITULAR)
			.setName(NOMBRE_TITULAR);
	}

	private InvoiceCommunicatorContext context(InvoiceCommunicationConfiguration config, Invoice... invoices) {
		return new InvoiceCommunicatorContext(new Domain(), new User(), null, Arrays.asList(invoices))
			.setCompany(company())
			.setConfig(config);
	}

	private InvoiceCommunicatorContext context(Invoice... invoices) {
		return context(config(), invoices);
	}

	// *****************************************************************
	// ************************* [FACTURA] *****************************
	// *****************************************************************

	/**
	 * Factura emitida nacional, no exenta, con una unica base al 21%. El numero de
	 * factura es aleatorio para que cada envio sea una factura nueva y no un
	 * duplicado de una que ya consta en el libro registro.
	 */
	private Invoice invoice() {
		Date now = AonDateUtils.today();
		int number = ThreadLocalRandom.current().nextInt(1, 100_000_000);
		String series = "S" + Calendar.getInstance().get(Calendar.YEAR);

		Invoice invoice = new Invoice()
			.setId(number)
			.setType(InvoiceType.SALES)
			.setSeries(series)
			.setNumber(number)
			.setReferenceCode(series + "/" + number)
			.setIssueDate(now)
			.setTaxDate(now)
			.setSecurityLevel(SecurityLevel.OFFICIAL)
			.setRegistryDocument(NIF_DESTINATARIO)
			.setRegistryDocumentType(DocumentType.CIF)
			.setRegistryDocumentCountry(Country.ES)
			.setRegistryName(NOMBRE_DESTINATARIO)
			.setAddress(new RegistryAddress()
				.setAddress("GRAN VIA")
				.setNumber("2")
				.setZip("01001"))
			.setRectificationType(RectificationType.NONE)
			.setTransaction(InvoiceTransactionType.NATIONAL)
			.setSurcharge(false)
			.setWithholding(false)
			.setWithholdingFarmer(false)
			.setVatAccrualPayment(false)
			.setInvestment(false)
			.setService(false)
			.setAdvance(false)
			.setTotal(TOTAL)
			.setRecorded(false)
			.setCreationDate(now)
			.setModificationDate(now)
			.setEpigraph(EPIGRAFE)
			.setActivity(activity(VATRegime.GENERAL));

		// La fecha de expedicion es la del dia, la que informa el alta y la que debe
		// repetirse despues en la anulacion.
		invoice.ensureFiscal().setExpDate(now);
		invoice.getDetails().add(detail(TIPO, CUOTA, VatDeductionType.WITH_RIGHT));
		return invoice;
	}

	/** Factura sin destinatario identificado, que para el SII es simplificada. */
	private Invoice simplificada() {
		Invoice invoice = invoice();
		invoice.setRegistryDocument(null);
		invoice.setRegistryName(null);
		return invoice;
	}

	/** Factura rectificativa por diferencias de la factura indicada. */
	private Invoice rectificativa(Invoice rectificada) {
		Invoice invoice = invoice();
		invoice.setRectificationType(RectificationType.NORMAL_RECTIFIER);
		invoice.setRectificationInvoice(rectificada.getId());
		invoice.setRectificationInvoiceSeries(rectificada.getSeries());
		invoice.setRectificationInvoiceNumber(rectificada.getNumber());
		invoice.setRectificationInvoiceReference(rectificada.getReferenceCode());
		invoice.setRectificationInvoiceDate(rectificada.getExpDate());
		return invoice;
	}

	/** Prestacion de servicios nacional. */
	private Invoice servicios() {
		Invoice invoice = invoice();
		invoice.setService(true);
		return invoice;
	}

	/** Factura de una actividad exenta, sin cuota de IVA. */
	private Invoice exenta() {
		Invoice invoice = invoice();
		invoice.setActivity(activity(VATRegime.EXEMPT));
		invoice.setTotal(BASE);
		invoice.getDetails().clear();
		invoice.getDetails().add(detail(0.0, 0.0, VatDeductionType.WITHOUT_RIGHT));
		return invoice;
	}

	/** Factura recibida, que pertenece al libro registro de facturas recibidas. */
	private Invoice recibida() {
		Invoice invoice = invoice();
		invoice.setType(InvoiceType.PURCHASE);
		return invoice;
	}

	private EnterpriseActivity activity(VATRegime regime) {
		return new EnterpriseActivity()
			.setId(1)
			.setPrincipal(true)
			.setVatRegime(regime)
			.setIae(new Iae("1", EPIGRAFE, "Actividad de pruebas"));
	}

	private InvoiceDetail detail(double percentage, double quota, VatDeductionType deduction) {
		InvoiceTax tax = new InvoiceTax();
		tax.setBase(BASE);
		tax.setPercentage(percentage);
		tax.setSurcharge(0.0);
		tax.setQuota(quota);
		tax.setSurchargeQuota(0.0);
		tax.setTaxType(TaxType.VAT);
		tax.setVatDeductionType(deduction);

		InvoiceDetail detail = new InvoiceDetail();
		detail.setDescription(DESCRIPCION_DETALLE);
		detail.setQuantity(1.0);
		detail.setPrice(BASE);
		detail.setLine((short) 0);
		detail.setDiscountExpression("0.0");
		detail.getInvoiceTaxes().add(tax);
		return detail;
	}

	// *****************************************************************
	// ******************** [ALTA - SIN ENVIO] *************************
	// *****************************************************************

	@Test
	void elEnvioVaAlEntornoDePruebas_Test() {
		assertEquals(URI_PRUEBAS, SIIUri.getInstance().getURI(config(), SIIType.FACTURAS_EMITIDAS),
			"La configuracion de pruebas no apunta al entorno de pruebas del SII");
	}

	@Test
	void altaDeFacturaNacional_Test() {
		Invoice invoice = invoice();

		SuministroLRFacturasEmitidas alta = alta(context(invoice));

		assertEquals("1.1", alta.getCabecera().getIDVersionSii());
		assertSame(ClaveTipoComunicacionType.A_0, alta.getCabecera().getTipoComunicacion());
		assertEquals(NIF_TITULAR, alta.getCabecera().getTitular().getNIF());
		assertEquals(NOMBRE_TITULAR, alta.getCabecera().getTitular().getNombreRazon());

		LRfacturasEmitidasType registro = registro(alta);
		assertEquals(String.valueOf(AonDateUtils.getYear(invoice.getTaxDate())), registro.getPeriodoLiquidacion().getEjercicio());
		assertEquals(periodo(invoice.getTaxDate()), registro.getPeriodoLiquidacion().getPeriodo());
		assertEquals(NIF_TITULAR, registro.getIDFactura().getIDEmisorFactura().getNIF());
		assertEquals(invoice.getReferenceCode(), registro.getIDFactura().getNumSerieFacturaEmisor());
		assertEquals(fecha(invoice.getExpDate()), registro.getIDFactura().getFechaExpedicionFacturaEmisor());

		FacturaExpedidaType factura = registro.getFacturaExpedida();
		assertSame(ClaveTipoFacturaType.F_1, factura.getTipoFactura());
		assertEquals("01", factura.getClaveRegimenEspecialOTrascendencia());
		assertEquals("121.0", factura.getImporteTotal());
		assertEquals(DESCRIPCION_DETALLE, factura.getDescripcionOperacion());
		assertEquals(invoice.getId().toString(), factura.getRefExterna());
		assertEquals(NIF_DESTINATARIO, factura.getContraparte().getNIF());

		// El destinatario esta establecido en el territorio, asi que basta el desglose
		// de la factura.
		assertNull(factura.getTipoDesglose().getDesgloseTipoOperacion());
		SujetaType sujeta = factura.getTipoDesglose().getDesgloseFactura().getSujeta();
		assertNotNull(sujeta);
		assertNull(sujeta.getExenta());
		assertNull(factura.getTipoDesglose().getDesgloseFactura().getNoSujeta());
		assertSame(TipoOperacionSujetaNoExentaType.S_1, sujeta.getNoExenta().getTipoNoExenta());

		List<DetalleIVAEmitidaType> detalles = sujeta.getNoExenta().getDesgloseIVA().getDetalleIVA();
		assertEquals(1, detalles.size());
		assertEquals("100.0", detalles.get(0).getBaseImponible());
		assertEquals("21.0", detalles.get(0).getTipoImpositivo());
		assertEquals("21.0", detalles.get(0).getCuotaRepercutida());
		assertNull(detalles.get(0).getTipoRecargoEquivalencia());
	}

	@Test
	void altaDeFacturaSimplificada_Test() {
		SuministroLRFacturasEmitidas alta = alta(context(simplificada()));

		FacturaExpedidaType factura = registro(alta).getFacturaExpedida();
		assertSame(ClaveTipoFacturaType.F_2, factura.getTipoFactura());
		assertNull(factura.getContraparte(), "La factura simplificada no lleva destinatario");
		assertNotNull(factura.getTipoDesglose().getDesgloseFactura());
	}

	@Test
	void altaDeFacturaRectificativa_Test() {
		Invoice rectificada = invoice();
		Invoice invoice = rectificativa(rectificada);

		SuministroLRFacturasEmitidas alta = alta(context(invoice));

		FacturaExpedidaType factura = registro(alta).getFacturaExpedida();
		assertSame(ClaveTipoFacturaType.R_1, factura.getTipoFactura());
		assertEquals("I", factura.getTipoRectificativa(), "Las rectificativas se envian por diferencias");
		assertNotNull(factura.getFacturasRectificadas());

		List<IDFacturaARType> rectificadas = factura.getFacturasRectificadas().getIDFacturaRectificada();
		assertEquals(1, rectificadas.size());
		assertEquals(rectificada.getReferenceCode(), rectificadas.get(0).getNumSerieFacturaEmisor());
		assertEquals(fecha(rectificada.getExpDate()), rectificadas.get(0).getFechaExpedicionFacturaEmisor());
	}

	@Test
	void altaDeFacturaRectificativaDeUnaSimplificada_Test() {
		Invoice invoice = rectificativa(invoice());
		invoice.setRegistryDocument(null);
		invoice.setRegistryName(null);

		SuministroLRFacturasEmitidas alta = alta(context(invoice));

		assertSame(ClaveTipoFacturaType.R_5, registro(alta).getFacturaExpedida().getTipoFactura());
	}

	/**
	 * Las prestaciones de servicios se desglosan por tipo de operacion, igual que
	 * hace el envio actual (SIIManager.suministroFacturasEmitidas).
	 */
	@Test
	void altaDeFacturaDeServicios_Test() {
		SuministroLRFacturasEmitidas alta = alta(context(servicios()));

		FacturaExpedidaType factura = registro(alta).getFacturaExpedida();
		assertNull(factura.getTipoDesglose().getDesgloseFactura());
		SujetaPrestacionType sujeta = factura.getTipoDesglose().getDesgloseTipoOperacion().getPrestacionServicios().getSujeta();
		assertNotNull(sujeta);
		assertSame(TipoOperacionSujetaNoExentaType.S_1, sujeta.getNoExenta().getTipoNoExenta());
		assertEquals(1, sujeta.getNoExenta().getDesgloseIVA().getDetalleIVA().size());
		assertEquals("100.0", sujeta.getNoExenta().getDesgloseIVA().getDetalleIVA().get(0).getBaseImponible());
	}

	/** La exencion de una actividad exenta es la del articulo 20 (E1). */
	@Test
	void altaDeFacturaExenta_Test() {
		SuministroLRFacturasEmitidas alta = alta(context(exenta()));

		FacturaExpedidaType factura = registro(alta).getFacturaExpedida();
		assertEquals("100.0", factura.getImporteTotal());
		SujetaType sujeta = factura.getTipoDesglose().getDesgloseFactura().getSujeta();
		assertNull(sujeta.getNoExenta());

		List<DetalleExentaType> detalles = sujeta.getExenta().getDetalleExenta();
		assertEquals(1, detalles.size());
		assertSame(CausaExencionType.E_1, detalles.get(0).getCausaExencion());
		assertEquals("100.0", detalles.get(0).getBaseImponible());
	}

	/** Solo se suministran las facturas emitidas del contexto. */
	@Test
	void altaDeVariasFacturas_Test() {
		Invoice primera = invoice();
		Invoice segunda = invoice();

		SuministroLRFacturasEmitidas alta = alta(context(primera, recibida(), segunda));

		assertEquals(2, alta.getRegistroLRFacturasEmitidas().size());
		assertEquals(primera.getReferenceCode(), alta.getRegistroLRFacturasEmitidas().get(0).getIDFactura().getNumSerieFacturaEmisor());
		assertEquals(segunda.getReferenceCode(), alta.getRegistroLRFacturasEmitidas().get(1).getIDFactura().getNumSerieFacturaEmisor());
	}

	@Test
	void altaComoModificacion_Test() {
		SuministroLRFacturasEmitidas alta = FacturasEmitidas.getInstance()
			.suministroFacturasEmitidas(context(invoice()), ClaveTipoComunicacionType.A_1);

		assertSame(ClaveTipoComunicacionType.A_1, alta.getCabecera().getTipoComunicacion());
	}

	// *****************************************************************
	// ***************** [ANULACION - SIN ENVIO] ***********************
	// *****************************************************************

	/**
	 * La anulacion identifica la factura por emisor, numero de serie y fecha de
	 * expedicion, que deben ser los mismos con los que se dio de alta; si no
	 * coinciden, la AEAT no localiza el registro.
	 */
	@Test
	void anulacionIdentificaLaFacturaDadaDeAlta_Test() {
		Invoice invoice = invoice();
		InvoiceCommunicatorContext context = context(invoice);

		LRfacturasEmitidasType registroAlta = registro(alta(context));
		LRBajaExpedidasType registroBaja = registro(baja(context));

		assertEquals(registroAlta.getIDFactura().getIDEmisorFactura().getNIF(),
			registroBaja.getIDFactura().getIDEmisorFactura().getNIF());
		assertEquals(registroAlta.getIDFactura().getNumSerieFacturaEmisor(),
			registroBaja.getIDFactura().getNumSerieFacturaEmisor());
		assertEquals(registroAlta.getIDFactura().getFechaExpedicionFacturaEmisor(),
			registroBaja.getIDFactura().getFechaExpedicionFacturaEmisor());
		assertEquals(registroAlta.getPeriodoLiquidacion().getEjercicio(),
			registroBaja.getPeriodoLiquidacion().getEjercicio());
		assertEquals(registroAlta.getPeriodoLiquidacion().getPeriodo(),
			registroBaja.getPeriodoLiquidacion().getPeriodo());
		assertEquals(registroAlta.getFacturaExpedida().getRefExterna(), registroBaja.getRefExterna());
	}

	// *****************************************************************
	// ***************** [ENVIO AL ENTORNO DE PRUEBAS] *****************
	// *****************************************************************

	@Test
	@Tag("envio")
	void altaEnEntornoDePruebas_Test() throws InvoiceCommunicationException {
		InvoiceCommunicatorContext context = context(configEnvio(), invoice());

		RespuestaLRFEmitidasType respuesta = enviarAlta(context);

		assertAceptada(respuesta);
	}

	@Test
	@Tag("envio")
	void altaDeFacturaSimplificadaEnEntornoDePruebas_Test() throws InvoiceCommunicationException {
		InvoiceCommunicatorContext context = context(configEnvio(), simplificada());

		RespuestaLRFEmitidasType respuesta = enviarAlta(context);

		assertAceptada(respuesta);
	}

	@Test
	@Tag("envio")
	void altaDeFacturaDeServiciosEnEntornoDePruebas_Test() throws InvoiceCommunicationException {
		InvoiceCommunicatorContext context = context(configEnvio(), servicios());

		RespuestaLRFEmitidasType respuesta = enviarAlta(context);

		assertAceptada(respuesta);
	}

	@Test
	@Tag("envio")
	void altaDeVariasFacturasEnEntornoDePruebas_Test() throws InvoiceCommunicationException {
		InvoiceCommunicatorContext context = context(configEnvio(), invoice(), invoice());

		RespuestaLRFEmitidasType respuesta = enviarAlta(context);

		assertAceptada(respuesta);
		assertEquals(2, respuesta.getRespuestaLinea().size());
	}

	/**
	 * Solo se puede anular una factura que conste en el libro registro, asi que
	 * primero se da de alta.
	 */
	@Test
	@Tag("envio")
	void anulacionEnEntornoDePruebas_Test() throws InvoiceCommunicationException {
		InvoiceCommunicatorContext context = context(configEnvio(), invoice());
		assertAceptada(enviarAlta(context));

		RespuestaLRBajaFEmitidasType respuesta = enviarBaja(context);

		assertAceptadaBaja(respuesta);
	}

	// *****************************************************************
	// ************************** [ENVIOS] *****************************
	// *****************************************************************

	private RespuestaLRFEmitidasType enviarAlta(InvoiceCommunicatorContext context) throws InvoiceCommunicationException {
		return SIIAeatPost.getInstance(context.getConfig()).suministroFacturasEmitidas(context, alta(context));
	}

	private RespuestaLRBajaFEmitidasType enviarBaja(InvoiceCommunicatorContext context) throws InvoiceCommunicationException {
		BajaLRFacturasEmitidas baja = baja(context);
		SiiValidation.validateFacturasEmitidasBaja(baja);
		return SIIAeatPost.getInstance(context.getConfig()).bajaFacturasEmitidas(context, baja);
	}

	private SuministroLRFacturasEmitidas alta(InvoiceCommunicatorContext context) {
		return FacturasEmitidas.getInstance().suministroFacturasEmitidas(context);
	}

	private BajaLRFacturasEmitidas baja(InvoiceCommunicatorContext context) {
		return FacturasEmitidasBaja.getInstance().bajaFacturasEmitidas(context);
	}

	// *****************************************************************
	// ************************ [RESPUESTAS] ***************************
	// *****************************************************************

	/**
	 * Comprueba que la AEAT ha aceptado el suministro. Un registro puede quedar
	 * aceptado con errores (por ejemplo, si el destinatario no esta en el censo),
	 * lo que no supone rechazo.
	 */
	private void assertAceptada(RespuestaLRFEmitidasType respuesta) {
		assertNotNull(respuesta);
		assertTrue(aceptado(respuesta.getEstadoEnvio()), () -> "Envio rechazado: " + describe(respuesta));
		assertTrue(!respuesta.getRespuestaLinea().isEmpty(), "La respuesta no trae ninguna linea");
		respuesta.getRespuestaLinea().forEach(linea ->
			assertTrue(aceptado(linea.getEstadoRegistro()), () -> "Registro rechazado: " + describe(respuesta)));
	}

	private void assertAceptadaBaja(RespuestaLRBajaFEmitidasType respuesta) {
		assertNotNull(respuesta);
		assertTrue(aceptado(respuesta.getEstadoEnvio()), () -> "Baja rechazada: " + describeBaja(respuesta));
		assertTrue(!respuesta.getRespuestaLinea().isEmpty(), "La respuesta no trae ninguna linea");
		respuesta.getRespuestaLinea().forEach(linea ->
			assertTrue(aceptado(linea.getEstadoRegistro()), () -> "Registro rechazado: " + describeBaja(respuesta)));
	}

	private boolean aceptado(EstadoEnvioType estado) {
		return EstadoEnvioType.CORRECTO == estado || EstadoEnvioType.PARCIALMENTE_CORRECTO == estado;
	}

	private boolean aceptado(EstadoRegistroType estado) {
		return EstadoRegistroType.CORRECTO == estado || EstadoRegistroType.ACEPTADO_CON_ERRORES == estado;
	}

	/** Estado y errores de la respuesta, para poder diagnosticar un fallo. */
	private String describe(RespuestaLRFEmitidasType respuesta) {
		return descripcion(respuesta.getEstadoEnvio(), respuesta.getRespuestaLinea().stream()
			.map(l -> linea(l.getIDFactura() == null ? null : l.getIDFactura().getNumSerieFacturaEmisor(),
				l.getEstadoRegistro(), l.getCodigoErrorRegistro(), l.getDescripcionErrorRegistro()))
			.collect(Collectors.toList()));
	}

	private String describeBaja(RespuestaLRBajaFEmitidasType respuesta) {
		return descripcion(respuesta.getEstadoEnvio(), respuesta.getRespuestaLinea().stream()
			.map(l -> linea(l.getIDFactura() == null ? null : l.getIDFactura().getNumSerieFacturaEmisor(),
				l.getEstadoRegistro(), l.getCodigoErrorRegistro(), l.getDescripcionErrorRegistro()))
			.collect(Collectors.toList()));
	}

	private String descripcion(EstadoEnvioType estadoEnvio, List<String> lineas) {
		return System.lineSeparator() + "EstadoEnvio: " + estadoEnvio
			+ System.lineSeparator() + String.join(System.lineSeparator(), lineas);
	}

	private String linea(String numSerie, EstadoRegistroType estado, BigInteger codigo, String descripcion) {
		return "\tFactura " + numSerie + ": " + estado + " (" + codigo + ") " + descripcion;
	}

	// *****************************************************************
	// ************************** [UTILES] *****************************
	// *****************************************************************

	private LRfacturasEmitidasType registro(SuministroLRFacturasEmitidas alta) {
		return alta.getRegistroLRFacturasEmitidas().get(0);
	}

	private LRBajaExpedidasType registro(BajaLRFacturasEmitidas baja) {
		return baja.getRegistroLRBajaExpedidas().get(0);
	}

	private String fecha(Date date) {
		return AonDateUtils.format(date, FORMATO_FECHA);
	}

	private String periodo(Date date) {
		return String.format("%02d", AonDateUtils.getMonth(date) + 1);
	}
}
