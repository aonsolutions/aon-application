package net.aonsolutions.aon.sii.aeat;

import java.io.IOException;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaLRCobrosEmitidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaLRFEmitidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CabeceraSii;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CabeceraSiiCobrosPagos;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CausaExencionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.ClaveTipoComunicacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.ClaveTipoFacturaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CobrosType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CountryType2;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CuponType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.DatosInmuebleType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.DatosPagoCobroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.DesgloseRectificacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.DetalleExentaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.DetalleIVAEmitidaPrestacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.DetalleIVAEmitidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.EmitidaPorTercerosType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.FacturaExpedidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.FacturaExpedidaType.DatosInmueble;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.FacturaExpedidaType.TipoDesglose;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.FacturaType.FacturasAgrupadas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.FacturaType.FacturasRectificadas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaARType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaBCType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaType.IDEmisorFactura;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDOtroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.MacrodatoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.NoSujetaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.PersonaFisicaJuridicaESType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.PersonaFisicaJuridicaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaPrestacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaPrestacionType.Exenta;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaPrestacionType.NoExenta;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaPrestacionType.NoExenta.DesgloseIVA;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.TipoConDesgloseType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.TipoOperacionSujetaNoExentaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.TipoSinDesglosePrestacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.TipoSinDesgloseType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.VariosDestinatariosType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRCobrosEmitidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRfacturasEmitidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.SuministroLRCobrosEmitidas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.SuministroLRFacturasEmitidas;
import net.aonsolutions.aon.sii.ClaveRegimenEspecialOTrascendenciaEmitidasType;
import net.aonsolutions.aon.sii.IDType;

public class FacturasEmitidas extends SIIBuilt {

	private static final String ID_VERSION_SII = "1.1";
	private static final String FORMATO_FECHA = "dd-MM-yyyy";

	/** Rectificativa por diferencias, que es como emite las rectificativas la aplicacion. */
	private static final String RECTIFICATIVA_POR_DIFERENCIAS = "I";

	/** Importe a partir del cual la factura es macrodato para el SII. */
	private static final double IMPORTE_MACRODATO = 100000000.0;

	private static final int MAX_DESCRIPCION_OPERACION = 500;

	private static final String DESCRIPCION_ENTREGA_BIENES = "Entrega de bienes";
	private static final String DESCRIPCION_PRESTACION_SERVICIOS = "Prestacion de servicios";

	public static FacturasEmitidas getInstance() {
		return new FacturasEmitidas();
	}
	
	public FacturasEmitidas() {

	}
	
	// ------------------- FACTURAS EMITIDAS
	 
	public byte[] getSuministroFacturasEmitidas(Domain domain, String login, Company company, Invoice invoice, LinkedList<VatContext> contextList, Boolean mod, String terceros){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLRFacturasEmitidas.class);
			b = writeXml(ctx, suministroFacturasEmitidas(domain, login, company, invoice, contextList, mod, terceros));
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	protected byte[] getSuministroFacturasEmitidas(SuministroLRFacturasEmitidas suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLRFacturasEmitidas.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	public byte[] getRespuestaSuministroFacturasEmitidas(RespuestaLRFEmitidasType suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRFEmitidasType.class);
			QName qName = new QName("https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro", "RespuestaLRFEmitidasType");
		    JAXBElement<RespuestaLRFEmitidasType> root = new JAXBElement<>(qName, RespuestaLRFEmitidasType.class, suministro);
			b = writeXml(ctx, root);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	// ------------------- ALTA DE FACTURAS EMITIDAS

	/**
	 * Suministro del libro registro de facturas expedidas con las facturas
	 * emitidas del contexto.
	 *
	 * Solo se suministran las facturas emitidas; las recibidas pertenecen al
	 * libro registro de facturas recibidas (FacturasRecibidas).
	 *
	 * @param context contexto de comunicacion con el SII
	 * @return SuministroLRFacturasEmitidas
	 */
	public SuministroLRFacturasEmitidas suministroFacturasEmitidas(InvoiceCommunicatorContext context) {
		return suministroFacturasEmitidas(context, ClaveTipoComunicacionType.A_0);
	}

	/**
	 * Suministro del libro registro de facturas expedidas con las facturas
	 * emitidas del contexto.
	 *
	 * El tipo de comunicacion distingue el alta de un registro nuevo (A0) de la
	 * modificacion de uno ya suministrado (A1); el resto del mensaje es igual.
	 *
	 * @param context contexto de comunicacion con el SII
	 * @param tipoComunicacion alta (A0) o modificacion (A1)
	 * @return SuministroLRFacturasEmitidas
	 */
	public SuministroLRFacturasEmitidas suministroFacturasEmitidas(InvoiceCommunicatorContext context, ClaveTipoComunicacionType tipoComunicacion) {
		SuministroLRFacturasEmitidas suministro = new SuministroLRFacturasEmitidas();
		suministro.setCabecera(cabecera(context.getCompany(), tipoComunicacion));
		context.invoiceStream()
			.filter(Invoice::isSales)
			.forEach(invoice -> suministro.getRegistroLRFacturasEmitidas().add(buildFacturaEmitida(context.getCompany(), invoice)));
		return suministro;
	}

	/**
	 * Construye el registro de alta de una factura emitida.
	 *
	 * El desglose se calcula con el desglose de impuestos de la propia factura
	 * (Invoice.refreshTaxBreakdown), de modo que el registro se construye sin
	 * acceder a la base de datos.
	 *
	 * @param company empresa titular del libro registro
	 * @param invoice factura que se suministra
	 * @return LRfacturasEmitidasType
	 */
	public LRfacturasEmitidasType buildFacturaEmitida(Company company, Invoice invoice) {
		invoice.refreshTaxBreakdown();

		LRfacturasEmitidasType factura = new LRfacturasEmitidasType();
		factura.setPeriodoLiquidacion(periodoLiquidacion(invoice.getTaxDate(), false));
		factura.setIDFactura(idFacturaEmitida(company, invoice));
		factura.setFacturaExpedida(facturaExpedida(invoice));
		return factura;
	}

	/**
	 * Identificacion de la factura ante la AEAT. La fecha de expedicion es la
	 * fiscal si existe, la misma que informa la baja (FacturasEmitidasBaja),
	 * porque es la que localiza el registro.
	 */
	private IDFacturaExpedidaType idFacturaEmitida(Company company, Invoice invoice) {
		IDEmisorFactura emisor = new IDEmisorFactura();
		emisor.setNIF(company.getDocument());

		IDFacturaExpedidaType idFactura = new IDFacturaExpedidaType();
		idFactura.setIDEmisorFactura(emisor);
		idFactura.setNumSerieFacturaEmisor(invoice.getReferenceCode());
		Date expDate = invoice.getExpDate() != null ? invoice.getExpDate() : invoice.getIssueDate();
		idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(expDate, FORMATO_FECHA));
		return idFactura;
	}

	/**
	 * Datos de la factura expedida. La referencia externa es el id de la factura,
	 * que es con el que se identifica la factura en la respuesta de la AEAT, igual
	 * que en la baja.
	 */
	private FacturaExpedidaType facturaExpedida(Invoice invoice) {
		FacturaExpedidaType factura = new FacturaExpedidaType();
		tipoFactura(invoice, factura);
		factura.setClaveRegimenEspecialOTrascendencia(claveRegimenEspecialOTrascendencia(invoice));
		double importeTotal = importeTotal(invoice);
		factura.setImporteTotal(importe(importeTotal));
		factura.setMacrodato(importeTotal >= IMPORTE_MACRODATO ? MacrodatoType.S : MacrodatoType.N);
		factura.setDescripcionOperacion(descripcionOperacion(invoice));
		factura.setRefExterna(invoice.getId() == null ? null : invoice.getId().toString());
		factura.setEmitidaPorTercerosODestinatario(invoice.isIssuedByThirdPartyOrRecipient()
			? EmitidaPorTercerosType.S
			: EmitidaPorTercerosType.N);
		factura.setVariosDestinatarios(VariosDestinatariosType.N);
		if (!invoice.isSimplified()) factura.setContraparte(getContraparte(invoice));
		factura.setTipoDesglose(tipoDesglose(invoice));
		return factura;
	}

	/**
	 * Tipo de factura: simplificada (F2) cuando no hay destinatario identificado,
	 * rectificativa (R1, o R5 si la rectificada es simplificada) cuando la factura
	 * rectifica a otra, y completa (F1) en el resto.
	 *
	 * Las rectificativas se envian por diferencias (I), que es como las emite la
	 * aplicacion, e informan la factura rectificada.
	 */
	private void tipoFactura(Invoice invoice, FacturaExpedidaType factura) {
		boolean simplificada = invoice.isSimplified();
		if (!invoice.isRectifier()) {
			factura.setTipoFactura(simplificada ? ClaveTipoFacturaType.F_2 : ClaveTipoFacturaType.F_1);
			return;
		}
		factura.setTipoFactura(simplificada ? ClaveTipoFacturaType.R_5 : ClaveTipoFacturaType.R_1);
		factura.setTipoRectificativa(RECTIFICATIVA_POR_DIFERENCIAS);
		factura.setCupon(CuponType.N);
		facturasRectificadas(invoice).ifPresent(factura::setFacturasRectificadas);
	}

	/**
	 * Factura rectificada, identificada por su numero de serie y su fecha de
	 * expedicion. Cada rectificativa rectifica una sola factura.
	 */
	private Optional<FacturasRectificadas> facturasRectificadas(Invoice invoice) {
		if (AonStringUtils.isBlank(invoice.getRectificationInvoiceReference())) return Optional.empty();
		if (invoice.getRectificationInvoiceDate() == null) return Optional.empty();

		IDFacturaARType rectificada = new IDFacturaARType();
		rectificada.setNumSerieFacturaEmisor(invoice.getRectificationInvoiceReference());
		rectificada.setFechaExpedicionFacturaEmisor(AonDateUtils.format(invoice.getRectificationInvoiceDate(), FORMATO_FECHA));

		FacturasRectificadas facturasRectificadas = new FacturasRectificadas();
		facturasRectificadas.getIDFacturaRectificada().add(rectificada);
		return Optional.of(facturasRectificadas);
	}

	/**
	 * Clave de regimen especial o trascendencia: ventanilla unica (17) en los
	 * regimenes especiales de la Union y de importacion, exportacion (02) en las
	 * operaciones extracomunitarias y con Canarias, Ceuta y Melilla, criterio de
	 * caja (07) cuando la factura lo aplica y regimen general (01) en el resto.
	 */
	private String claveRegimenEspecialOTrascendencia(Invoice invoice) {
		if (invoice.isSalesOSS()) return ClaveRegimenEspecialOTrascendenciaEmitidasType._17.getName();
		if (invoice.isExtracommunity() || invoice.isCanCeuMel()) return ClaveRegimenEspecialOTrascendenciaEmitidasType._02.getName();
		if (invoice.isVatAccrualPayment()) return ClaveRegimenEspecialOTrascendenciaEmitidasType._07.getName();
		return ClaveRegimenEspecialOTrascendenciaEmitidasType._01.getName();
	}

	/**
	 * Descripcion de la operacion, con las descripciones de las lineas de la
	 * factura. Si la factura no las trae se describe la operacion por su
	 * naturaleza. Se recorta a los 500 caracteres que admite el SII.
	 */
	private String descripcionOperacion(Invoice invoice) {
		String descripcion = invoice.detailStream()
			.map(InvoiceDetail::getDescription)
			.filter(d -> !AonStringUtils.isBlank(d))
			.map(d -> d.replaceAll("[\\r\\n<>]", " ").trim())
			.collect(Collectors.joining(" - "));
		if (AonStringUtils.isBlank(descripcion)) {
			descripcion = invoice.isService() ? DESCRIPCION_PRESTACION_SERVICIOS : DESCRIPCION_ENTREGA_BIENES;
		}
		return AonStringUtils.left(descripcion, MAX_DESCRIPCION_OPERACION);
	}

	/**
	 * Importe total de la factura: la suma de lo no sujeto, lo exento y las bases
	 * no exentas con su cuota y su recargo de equivalencia. Se calcula con el
	 * mismo desglose que se suministra para que los dos importes cuadren.
	 */
	private double importeTotal(Invoice invoice) {
		double noExenta = vats(invoice)
			.filter(ib -> isNoExenta(invoice, ib))
			.mapToDouble(ib -> ib.getBase() + cuota(invoice, ib) + cuotaRecargo(invoice, ib))
			.sum();
		return AonMathUtils.round(baseNoSujeta(invoice) + baseExenta(invoice) + noExenta);
	}

	// ------------------- DESGLOSE

	/**
	 * Desglose de la factura. El SII exige desglosar por tipo de operacion
	 * (entrega de bienes o prestacion de servicios) cuando el destinatario no esta
	 * establecido en el territorio de aplicacion del impuesto o cuando la
	 * operacion es una prestacion de servicios; en el resto de los casos basta el
	 * desglose de la factura.
	 */
	private TipoDesglose tipoDesglose(Invoice invoice) {
		TipoDesglose tipoDesglose = new TipoDesglose();
		if (isDesglosePorTipoDeOperacion(invoice)) {
			tipoDesglose.setDesgloseTipoOperacion(desgloseTipoOperacion(invoice));
		} else {
			tipoDesglose.setDesgloseFactura(desgloseFactura(invoice));
		}
		return tipoDesglose;
	}

	/**
	 * Las facturas simplificadas no llevan destinatario, asi que siempre van con
	 * el desglose de la factura.
	 */
	private boolean isDesglosePorTipoDeOperacion(Invoice invoice) {
		return !invoice.isSimplified()
			&& (invoice.isService() || invoice.isIntracommunity() || invoice.isExtracommunity() || isNoEstablecido(invoice));
	}

	/** El destinatario no esta establecido: no tiene NIF espanol o no esta censado. */
	private boolean isNoEstablecido(Invoice invoice) {
		return !Country.ES.equals(invoice.getRegistryDocumentCountry())
			|| DocumentType.NOT_CENSUSED.equals(invoice.getRegistryDocumentType());
	}

	private TipoConDesgloseType desgloseTipoOperacion(Invoice invoice) {
		TipoConDesgloseType desglose = new TipoConDesgloseType();
		if (invoice.isService()) desglose.setPrestacionServicios(prestacionServicios(invoice));
		else desglose.setEntrega(desgloseFactura(invoice));
		return desglose;
	}

	/**
	 * Desglose de la factura (tambien el de la entrega de bienes cuando se
	 * desglosa por tipo de operacion).
	 */
	private TipoSinDesgloseType desgloseFactura(Invoice invoice) {
		TipoSinDesgloseType desglose = new TipoSinDesgloseType();
		noSujeta(invoice).ifPresent(desglose::setNoSujeta);

		SujetaType sujeta = new SujetaType();
		exenta(invoice).ifPresent(sujeta::setExenta);
		noExenta(invoice).ifPresent(sujeta::setNoExenta);
		if (sujeta.getExenta() != null || sujeta.getNoExenta() != null) desglose.setSujeta(sujeta);
		return desglose;
	}

	/** Desglose de la prestacion de servicios, que no admite recargo de equivalencia. */
	private TipoSinDesglosePrestacionType prestacionServicios(Invoice invoice) {
		TipoSinDesglosePrestacionType desglose = new TipoSinDesglosePrestacionType();
		noSujeta(invoice).ifPresent(desglose::setNoSujeta);

		SujetaPrestacionType sujeta = new SujetaPrestacionType();
		exentaPrestacion(invoice).ifPresent(sujeta::setExenta);
		noExentaPrestacion(invoice).ifPresent(sujeta::setNoExenta);
		if (sujeta.getExenta() != null || sujeta.getNoExenta() != null) desglose.setSujeta(sujeta);
		return desglose;
	}

	/**
	 * Importe no sujeto: los suplidos y las operaciones no sujetas. En los
	 * regimenes de ventanilla unica la base va como no sujeta por reglas de
	 * localizacion, y sin la cuota del pais miembro.
	 */
	private Optional<NoSujetaType> noSujeta(Invoice invoice) {
		double base = baseNoSujeta(invoice);
		if (AonMathUtils.isZero(base)) return Optional.empty();

		NoSujetaType noSujeta = new NoSujetaType();
		if (invoice.isVatUnion()) noSujeta.setImporteTAIReglasLocalizacion(importe(base));
		else noSujeta.setImportePorArticulos714Otros(importe(base));
		return Optional.of(noSujeta);
	}

	private Optional<SujetaType.Exenta> exenta(Invoice invoice) {
		List<DetalleExentaType> detalles = detalleExenta(invoice);
		if (detalles.isEmpty()) return Optional.empty();

		SujetaType.Exenta exenta = new SujetaType.Exenta();
		exenta.getDetalleExenta().addAll(detalles);
		return Optional.of(exenta);
	}

	private Optional<SujetaPrestacionType.Exenta> exentaPrestacion(Invoice invoice) {
		List<DetalleExentaType> detalles = detalleExenta(invoice);
		if (detalles.isEmpty()) return Optional.empty();

		SujetaPrestacionType.Exenta exenta = new SujetaPrestacionType.Exenta();
		exenta.getDetalleExenta().addAll(detalles);
		return Optional.of(exenta);
	}

	/** Un detalle por causa de exencion con la suma de las bases exentas. */
	private List<DetalleExentaType> detalleExenta(Invoice invoice) {
		Map<CausaExencionType, Double> bases = new EnumMap<>(CausaExencionType.class);
		vats(invoice)
			.filter(ib -> isExenta(invoice, ib))
			.forEach(ib -> bases.merge(causaExencion(invoice, ib), ib.getBase(), Double::sum));

		return bases.entrySet().stream()
			.map(e -> {
				DetalleExentaType detalle = new DetalleExentaType();
				detalle.setCausaExencion(e.getKey());
				detalle.setBaseImponible(importe(e.getValue()));
				return detalle;
			})
			.collect(Collectors.toList());
	}

	/**
	 * Causa de exencion del desglose de la factura; si no viene informada se
	 * deduce de la naturaleza de la operacion: exenciones interiores (E1),
	 * exportaciones y Canarias, Ceuta y Melilla (E2) y entregas intracomunitarias
	 * (E5).
	 */
	private CausaExencionType causaExencion(Invoice invoice, InvoiceBreakdown ib) {
		if (ib.getVatExemptionCause() != null) return CausaExencionType.fromValue(ib.getVatExemptionCause().name());
		if (invoice.isIntracommunity()) return CausaExencionType.E_5;
		if (invoice.isExtracommunity() || invoice.isCanCeuMel()) return CausaExencionType.E_2;
		return CausaExencionType.E_1;
	}

	private Optional<SujetaType.NoExenta> noExenta(Invoice invoice) {
		List<InvoiceBreakdown> vats = vatsNoExentos(invoice);
		if (vats.isEmpty()) return Optional.empty();

		SujetaType.NoExenta.DesgloseIVA desgloseIVA = new SujetaType.NoExenta.DesgloseIVA();
		vats.forEach(ib -> {
			DetalleIVAEmitidaType detalle = new DetalleIVAEmitidaType();
			detalle.setBaseImponible(importe(ib.getBase()));
			detalle.setTipoImpositivo(importe(tipoImpositivo(invoice, ib)));
			detalle.setCuotaRepercutida(importe(cuota(invoice, ib)));
			if (isRecargoEquivalencia(invoice, ib)) {
				detalle.setTipoRecargoEquivalencia(importe(ib.getSurcharge()));
				detalle.setCuotaRecargoEquivalencia(importe(ib.getSurchargeQuota()));
			}
			desgloseIVA.getDetalleIVA().add(detalle);
		});

		SujetaType.NoExenta noExenta = new SujetaType.NoExenta();
		noExenta.setTipoNoExenta(tipoNoExenta(invoice));
		noExenta.setDesgloseIVA(desgloseIVA);
		return Optional.of(noExenta);
	}

	private Optional<SujetaPrestacionType.NoExenta> noExentaPrestacion(Invoice invoice) {
		List<InvoiceBreakdown> vats = vatsNoExentos(invoice);
		if (vats.isEmpty()) return Optional.empty();

		SujetaPrestacionType.NoExenta.DesgloseIVA desgloseIVA = new SujetaPrestacionType.NoExenta.DesgloseIVA();
		vats.forEach(ib -> {
			DetalleIVAEmitidaPrestacionType detalle = new DetalleIVAEmitidaPrestacionType();
			detalle.setBaseImponible(importe(ib.getBase()));
			detalle.setTipoImpositivo(importe(tipoImpositivo(invoice, ib)));
			detalle.setCuotaRepercutida(importe(cuota(invoice, ib)));
			desgloseIVA.getDetalleIVA().add(detalle);
		});

		SujetaPrestacionType.NoExenta noExenta = new SujetaPrestacionType.NoExenta();
		noExenta.setTipoNoExenta(tipoNoExenta(invoice));
		noExenta.setDesgloseIVA(desgloseIVA);
		return Optional.of(noExenta);
	}

	/**
	 * En la inversion del sujeto pasivo la operacion es sujeta y no exenta, pero
	 * el impuesto lo liquida el destinatario (S2).
	 */
	private TipoOperacionSujetaNoExentaType tipoNoExenta(Invoice invoice) {
		return invoice.isIsp()
			? TipoOperacionSujetaNoExentaType.S_2
			: TipoOperacionSujetaNoExentaType.S_1;
	}

	// ------------------- CLASIFICACION DEL DESGLOSE

	/** Lineas de IVA del desglose de impuestos de la factura. */
	private Stream<InvoiceBreakdown> vats(Invoice invoice) {
		return invoice.getVats().stream().filter(InvoiceBreakdown::isVat);
	}

	/**
	 * Bases no exentas agrupadas por tipo impositivo y recargo, porque el SII no
	 * admite dos detalles con el mismo tipo.
	 */
	private List<InvoiceBreakdown> vatsNoExentos(Invoice invoice) {
		Map<String, InvoiceBreakdown> vats = new LinkedHashMap<>();
		vats(invoice)
			.filter(ib -> isNoExenta(invoice, ib))
			.forEach(ib -> {
				String key = ib.getPercentage() + "/" + ib.getSurcharge();
				InvoiceBreakdown agrupado = vats.get(key);
				if (agrupado == null) vats.put(key, copy(ib));
				else agrupado.add(ib);
			});
		return new LinkedList<>(vats.values());
	}

	private InvoiceBreakdown copy(InvoiceBreakdown ib) {
		return new InvoiceBreakdown()
			.setTaxType(ib.getTaxType())
			.setBase(ib.getBase())
			.setPercentage(ib.getPercentage())
			.setQuota(ib.getQuota())
			.setSurcharge(ib.getSurcharge())
			.setSurchargeQuota(ib.getSurchargeQuota())
			.setVatDeductionType(ib.getVatDeductionType())
			.setVatExemptionCause(ib.getVatExemptionCause())
			.setPrepayment(ib.isPrepayment());
	}

	/**
	 * No sujeto: los suplidos, las lineas marcadas como no sujetas y, en los
	 * regimenes de ventanilla unica, toda la factura.
	 */
	private boolean isNoSujeta(Invoice invoice, InvoiceBreakdown ib) {
		return ib.isPrepayment()
			|| VatDeductionType.safeNoSujeto(ib.getVatDeductionType())
			|| invoice.isVatUnion();
	}

	/**
	 * Sujeto y exento: las lineas sin tipo impositivo de una operacion exenta (por
	 * la actividad, intracomunitaria, extracomunitaria o con Canarias, Ceuta y
	 * Melilla) o marcadas como exentas. En la inversion del sujeto pasivo no hay
	 * exencion: la operacion es sujeta y no exenta.
	 */
	private boolean isExenta(Invoice invoice, InvoiceBreakdown ib) {
		return !invoice.isIsp()
			&& !isNoSujeta(invoice, ib)
			&& AonMathUtils.isZero(ib.getPercentage())
			&& (isExentaLaOperacion(invoice) || VatDeductionType.safeSujetoExento(ib.getVatDeductionType()));
	}

	private boolean isNoExenta(Invoice invoice, InvoiceBreakdown ib) {
		return !isNoSujeta(invoice, ib) && !isExenta(invoice, ib);
	}

	private boolean isExentaLaOperacion(Invoice invoice) {
		return invoice.isExempt()
			|| invoice.isIntracommunity()
			|| invoice.isExtracommunity()
			|| invoice.isCanCeuMel();
	}

	private double baseNoSujeta(Invoice invoice) {
		return vats(invoice).filter(ib -> isNoSujeta(invoice, ib)).mapToDouble(InvoiceBreakdown::getBase).sum();
	}

	private double baseExenta(Invoice invoice) {
		return vats(invoice).filter(ib -> isExenta(invoice, ib)).mapToDouble(InvoiceBreakdown::getBase).sum();
	}

	/** En la inversion del sujeto pasivo no se repercute cuota. */
	private double tipoImpositivo(Invoice invoice, InvoiceBreakdown ib) {
		return invoice.isIsp() ? 0.0 : ib.getPercentage();
	}

	private double cuota(Invoice invoice, InvoiceBreakdown ib) {
		return invoice.isIsp() ? 0.0 : ib.getQuota();
	}

	private double cuotaRecargo(Invoice invoice, InvoiceBreakdown ib) {
		return isRecargoEquivalencia(invoice, ib) ? ib.getSurchargeQuota() : 0.0;
	}

	private boolean isRecargoEquivalencia(Invoice invoice, InvoiceBreakdown ib) {
		return !invoice.isIsp()
			&& AonMathUtils.isNotZero(ib.getSurcharge())
			&& AonMathUtils.isNotZero(ib.getSurchargeQuota());
	}

	private String importe(double value) {
		return Double.toString(AonMathUtils.round(value));
	}

	/**
	 * Cabecera del suministro con el titular del libro registro.
	 */
	private CabeceraSii cabecera(Company company, ClaveTipoComunicacionType tipoComunicacion) {
		PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
		titular.setNIF(company.getDocument());
		titular.setNombreRazon(company.getName());

		CabeceraSii cabecera = new CabeceraSii();
		cabecera.setIDVersionSii(ID_VERSION_SII);
		cabecera.setTipoComunicacion(tipoComunicacion);
		cabecera.setTitular(titular);
		return cabecera;
	}

	// ------------------- ALTA DE FACTURAS EMITIDAS (LEGACY)

	/**
	 * Libro de registro de Facturas expedidas.
	 * 
	 * @param company
	 * @param vatList
	 */
	protected SuministroLRFacturasEmitidas suministroFacturasEmitidas(Domain domain, String login, Company company, Invoice invoice, LinkedList<VatContext> contextList, Boolean mod, String terceros) {
		Integer invoiceId = invoice.getId();
		SuministroLRFacturasEmitidas suministro = new SuministroLRFacturasEmitidas();

		// CABECERA
		suministro.setCabecera(cabecera(company, mod, terceros));
		
		// BODY

		VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoiceId)).findFirst().orElse(new VatContext());
		
		EnterpriseActivity activity = AON.getEnterpriseActivity(domain.getName(), domain.getId(), login, vat.getActivity());
		if(activity == null) {
			activity = AON.getEnterpriseActivities(domain.getName(), domain.getId(), login)
					.findFirst().orElse(new EnterpriseActivity());
		}
		boolean exempt = (activity.getVatRegime() != null && activity.getVatRegime().isExempt()) 
				|| vat.isIntracommunity() || vat.isExtracommunity() || vat.isCanCeuMel();
		
		// Régimen especial de la Unión, se debe pasar como no sujeta por reglas de localizacion y solo la base imponible sin el IVA del pais miembro 
		boolean oss = invoice.isVatUnion();
		
		Double exenta = contextList.stream().filter(f -> f.getInvoice().equals(invoiceId) 
				&&  f.getPercentage() == 0 && 
				( 
					(exempt && VatDeductionType.WITH_RIGHT.equals(f.getVatDeductionType()))
					|| VatDeductionType.WITHOUT_RIGHT.equals(f.getVatDeductionType())
				)
				&& !f.isPrepayment() && !oss) 
				.mapToDouble(f -> f.getBase()).sum();
		Double noSujeta = contextList.stream().filter(f -> f.getInvoice().equals(invoiceId) && (VatDeductionType.NON_TAXABLE.equals(f.getVatDeductionType()) || f.isPrepayment() || oss))
				.mapToDouble(f -> f.getBase()).sum();

		LinkedList<VatData> noExenta = contextList.stream().filter(f -> f.getInvoice().equals(invoiceId) && (!exempt || f.getPercentage() > 0)  
				&& VatDeductionType.WITH_RIGHT.equals(f.getVatDeductionType()) && !f.isPrepayment() && !oss )
				.map(f -> new VatData().setBase(f.getBase())
						.setPercentage(f.getPercentage())
						.setQuota(f.getQuota())
						.setSurchargePercent(f.getSurchargePercent())
						.setSurchargeQuota(f.getSurchargeQuota()))
				.collect(Collectors.toCollection(LinkedList::new));
			
		LRfacturasEmitidasType factura = new LRfacturasEmitidasType();
		
		factura.setPeriodoLiquidacion(periodoLiquidacion(vat.getTaxDate(), false));
		IDFacturaExpedidaType idFactura = new IDFacturaExpedidaType();
		
		Date expDate = invoice != null && invoice.getExpDate() != null ? invoice.getExpDate() : vat.getIssueDate();
		idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(expDate, "dd-MM-yyyy"));
		//idFactura.setNumSerieFacturaEmisorResumenFin(vat.getReferenceCode()); // SI ES ASIENTO RESUMEN
		idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
		IDEmisorFactura emisor = new IDEmisorFactura();
		
		emisor.setNIF(company.getDocument());
		idFactura.setIDEmisorFactura(emisor);
		factura.setIDFactura(idFactura);
		FacturaExpedidaType fet = new FacturaExpedidaType();
		
		fet.setTipoFactura(ClaveTipoFacturaType.F_1); // TODO  De momento a piñon fijo!!!
		if(vat.getRegistryDocument() == null || vat.getRegistryDocument().equals("")){
			fet.setTipoFactura(ClaveTipoFacturaType.F_2);
		}
		if(vat.isRectification()){
			fet.setTipoFactura(ClaveTipoFacturaType.R_1); // TODO R_1 || R_2 || R_3 || R_4 || R_4.  De momento a piñon fijo!!!
			fet.setTipoRectificativa("I"); //TODO  S (por sustitucion) || I (por diferencia). De momento a piñon fijo!!!
			// FACTURA RECTIFICADAS
			FacturasRectificadas fr = new FacturasRectificadas();
			Invoice rectificada = AON.getInvoice(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(vat.getRectificationInvoice()));
			// SOLO 1 RECTIFICADA PARA CADA RECTIFICATIVA! 
			IDFacturaARType a2 = new IDFacturaARType();
			Date recExpDate = rectificada.getExpDate() != null ? rectificada.getExpDate() : rectificada.getIssueDate();
			a2.setFechaExpedicionFacturaEmisor(AonDateUtils.format(recExpDate, "dd-MM-yyyy"));
			a2.setNumSerieFacturaEmisor(rectificada.getReferenceCode());
			fr.getIDFacturaRectificada().add(a2);
			fet.setFacturasRectificadas(fr);
			
			// IMPORTE RECTIFICACION 
			if("S".equalsIgnoreCase(fet.getTipoRectificativa())){
				DesgloseRectificacionType drt = new DesgloseRectificacionType();
				drt.setBaseRectificada(Double.toString(AonMathUtils.round(rectificada.getTaxableBase()))); 
// TODO			drt.setCuotaRecargoRectificado(Double.toString(vat.getRectified().getSurchargeQuota()));  
				drt.setCuotaRectificada(Double.toString(AonMathUtils.round(rectificada.getVatQuota()))); // TODO ¿?
				fet.setImporteRectificacion(drt);
			}
		}
		
		if(ClaveTipoFacturaType.F_3.equals(fet.getTipoFactura())){
			// TODO FACTURAS AGRUPADAS
			FacturasAgrupadas fa = new FacturasAgrupadas();

			// FOR
			IDFacturaARType a = new IDFacturaARType();
			a.setFechaExpedicionFacturaEmisor(""); 
			a.setNumSerieFacturaEmisor(""); 
			fa.getIDFacturaAgrupada().add(a);		
			fet.setFacturasAgrupadas(fa);
		}

		
		// CLAVE REGIMEN IVA || TRANSCENDENCIA  
	
		fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaEmitidasType._01.getName()); //TODO 
		
		// CLAVE REGIMEN IVA || TRANSCENDENCIA ADICIONAL 1			
		if(vat.isVatAccrualRegime()){
			fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaEmitidasType._07.getName());//TODO OPTIONAL
		}	
		if(vat.isExtracommunity() || vat.isCanCeuMel()){
			fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaEmitidasType._02.getName());
		}
		
		// Regimen importacion IOSS
//		if(invoice.isSales() && invoice.isVatImportationAvailable() && invoice.isVatImportation() && invoice.isVatImportationAmountValid()) {
//			fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaEmitidasType._17.getName());
//		}
		// Regimenes especiales ventanilla única
		if(invoice.isSales() && (invoice.isVatUnion() || invoice.isVatUnionExternal() || invoice.isVatImportation())) {
			fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaEmitidasType._17.getName());
		}

		ApplicationParameter ap = AON.getApplicationParameter(domain.getName(), domain.getId(), login, AppParam.FS_MODEL_CFG_SII);
    	Boolean isRegistro = "R".equals(ap.getValue());
		Date opDate = isRegistro ? vat.getCreationDate() : vat.getTaxDate();
		if(opDate.compareTo(AonDateUtils.getDate(2017, 6, 1)) < 0){
//			fet.setClaveRegimenEspecialOTrascendencia(ClaveRegimenEspecialOTrascendenciaEmitidasType._16.getName());
		}
			
		// CLAVE REGIMEN IVA || TRANSCENDENCIA ADICIONAL 2
		//fet.setClaveRegimenEspecialOTrascendenciaAdicional2("");//TODO OPTIONAL

		// NUMERO REGISTRO AUTORIZACION
		fet.setNumRegistroAcuerdoFacturacion("");// TODO TIENE K DARLO EL CLIENTE
		
		// IMPORTE TOTAL 
		//Double total2 = contextList.stream().filter(g -> g.getInvoice().equals(invoice)).mapToDouble(g -> g.getBase() + g.getQuota()).sum();
		Double total = noSujeta + exenta + noExenta.stream().mapToDouble(f -> f.getBase() + f.getQuota() + f.getSurchargeQuota()).sum();
		fet.setImporteTotal(Double.toString(AonMathUtils.round(total)));
		fet.setMacrodato(total >= 100000000 ? MacrodatoType.S: MacrodatoType.N);
		// BASE IMPONIBLE A COSTE (OPTIONAL)
		if(fet.getClaveRegimenEspecialOTrascendencia().equals("06")
				|| (fet.getClaveRegimenEspecialOTrascendenciaAdicional1() != null && fet.getClaveRegimenEspecialOTrascendenciaAdicional1().equals("06"))
				|| (fet.getClaveRegimenEspecialOTrascendenciaAdicional2() != null && fet.getClaveRegimenEspecialOTrascendenciaAdicional2().equals("06"))){
			Double base = contextList.stream().filter(g -> g.getInvoice().equals(invoiceId)).mapToDouble(g -> g.getBase()).sum();
			fet.setBaseImponibleACoste(Double.toString(AonMathUtils.round(base)));
		}			
		// DESCRIPCION OPERACION 
		
		AccountingInvoice ai = ACCOUNTING.getAccountingInvoiceFromInvoice(domain.getName(), domain.getId(), login, invoiceId);
		String str = "";
		if(ai != null && ai.getAccountEntry() != null && ai.getAccountEntry().getDetails() != null){
			for(AccountEntryDetail aed : ai.getAccountEntry().getDetails()){
				Account a = ACCOUNTING.getAccount(domain.getName(), domain.getId(), login,  aed.getAccountId());
				if(a.getCode().substring(0, 1).equals("6") ||  a.getCode().substring(0, 1).equals("7")) {
					str = str + a.getDescription() + "-";
				}
			}
		}
		
		str = str + vat.getDetailDescription().replaceAll("<", "").replaceAll(">", "");
		
		fet.setDescripcionOperacion(str.length() > 100 ? str.substring(0, 99) : str);
		
		// DATOS INMUEBLES (OPTIONAL) TODO // sii regimen IVA 12 - Operaciones de arrendamiento de local de negocio no sujetos a retención.
		if(fet.getClaveRegimenEspecialOTrascendencia().equals("12")
				|| (fet.getClaveRegimenEspecialOTrascendenciaAdicional1() != null && fet.getClaveRegimenEspecialOTrascendenciaAdicional1().equals("12"))
				|| (fet.getClaveRegimenEspecialOTrascendenciaAdicional2() != null && fet.getClaveRegimenEspecialOTrascendenciaAdicional2().equals("12"))){
			DatosInmueble datosInmueble = new DatosInmueble();
			DatosInmuebleType dit = new DatosInmuebleType();
			dit.setReferenciaCatastral("");//TODO
			dit.setSituacionInmueble("");//TODO
			datosInmueble.getDetalleInmueble().add(dit);
			fet.setDatosInmueble(datosInmueble);
			//Invoice i = new Invoice();
		}
		// IMPORTE TRANSMISION SUJETOS A IVA (OPTIONAL) ... importe TODO ¿?
		//fet.setImporteTransmisionSujetoAIVA("");//TODO
	
		// EMITIDA POR TERCEROS (OPTIONAL) ... default N
		fet.setEmitidaPorTercerosODestinatario(EmitidaPorTercerosType.N);//TODO
		
		// VARIOS DESTINATARIOS (OPTIONAL) ... default N
		fet.setVariosDestinatarios(VariosDestinatariosType.N);//TODO
		
		// CUPON (OPTIONAL) ... default N
		if(fet.getTipoFactura().equals(ClaveTipoFacturaType.R_1)
			|| fet.getTipoFactura().equals(ClaveTipoFacturaType.R_5)
			|| fet.getTipoFactura().equals(ClaveTipoFacturaType.F_4)){
			fet.setCupon(CuponType.N);//TODO
		}
		
		// CONTRAPARTE
		if(vat.getRegistryDocument() != null && !vat.getRegistryDocument().equals("")){
			if(vat.isIntracommunity()) {
				fet.setContraparte(contraparteIntracomunitario(vat));
			} else fet.setContraparte(contraparte(vat));
		}
		// TIPO DESGLOSE
		TipoDesglose tipoDesglose = new TipoDesglose(); 

		HashMap<Double, VatData> noExentaMap = new HashMap<>();
		noExenta.stream().forEach(r -> {
			if(noExentaMap.containsKey(r.getPercentage())){
				VatData vd = noExentaMap.get(r.getPercentage());
				noExentaMap.get(r.getPercentage()).setBase(vd.getBase() + r.getBase());
				noExentaMap.get(r.getPercentage()).setQuota(vd.getQuota() + r.getQuota());
				noExentaMap.get(r.getPercentage()).setSurchargeQuota(vd.getSurchargeQuota() + r.getSurchargeQuota());					
			} else noExentaMap.put(r.getPercentage(), r);
		});
		
		if(!fet.getTipoFactura().equals(ClaveTipoFacturaType.F_2) && !fet.getTipoFactura().equals(ClaveTipoFacturaType.F_4) 
			&& (vat.isService() || vat.isIntracommunity() || vat.isExtracommunity() || isNDocument(vat.getRegistryDocument()) || oss)){
			TipoConDesgloseType tcdt = new TipoConDesgloseType();
			
			if(vat.isService() && (!vat.isIntracommunity() || oss)){
				TipoSinDesglosePrestacionType prestacion = new TipoSinDesglosePrestacionType();
				if(!noSujeta.equals(0.0)) {
					NoSujetaType nst3 = new NoSujetaType();
					if (oss)
						nst3.setImporteTAIReglasLocalizacion(Double.toString(AonMathUtils.round(noSujeta)));
					else
						nst3.setImportePorArticulos714Otros(Double.toString(AonMathUtils.round(noSujeta))); 
					//	nst3.setImporteTAIReglasLocalizacion(""); // TODO
					prestacion.setNoSujeta(nst3);
				}
				SujetaPrestacionType st3 = new SujetaPrestacionType();
				
				NoExenta noExenta3 = new NoExenta();
				DesgloseIVA diva3 = new DesgloseIVA();
				if(AonMathUtils.isNotZero(exenta)) {
					if(vat.isOtherISP()) {
						DetalleIVAEmitidaPrestacionType diet = new DetalleIVAEmitidaPrestacionType();
						diet.setBaseImponible(Double.toString(AonMathUtils.round(exenta))); 
						diet.setCuotaRepercutida("0"); 
						diet.setTipoImpositivo("0"); 
						diva3.getDetalleIVA().add(diet);


						noExenta3.setDesgloseIVA(diva3);
						noExenta3.setTipoNoExenta(TipoOperacionSujetaNoExentaType.S_2);		
						st3.setNoExenta(noExenta3);
					} else {
						Exenta exenta3 = new Exenta();
						DetalleExentaType detalleExenta = new DetalleExentaType();
						detalleExenta.setBaseImponible(Double.toString(AonMathUtils.round(exenta)));
						if(vat.isIntracommunity()){
							detalleExenta.setCausaExencion(CausaExencionType.E_5);
						}else if(vat.isExtracommunity() ||  vat.isCanCeuMel()){
							detalleExenta.setCausaExencion(CausaExencionType.E_2);
						} else {
							detalleExenta.setCausaExencion(CausaExencionType.E_6);
						}
						exenta3.getDetalleExenta().add(detalleExenta);
						st3.setExenta(exenta3);
					}
				}
				
				if(!noExenta.isEmpty()){
					noExentaMap.keySet().stream().forEach(key -> {
						DetalleIVAEmitidaPrestacionType diet = new DetalleIVAEmitidaPrestacionType();
						diet.setBaseImponible(Double.toString(AonMathUtils.round(noExentaMap.get(key).getBase()))); 
						diet.setCuotaRepercutida(Double.toString(AonMathUtils.round(noExentaMap.get(key).getQuota()))); 
						diet.setTipoImpositivo(Double.toString(AonMathUtils.round(key))); 
						diva3.getDetalleIVA().add(diet);
					});
					noExenta3.setDesgloseIVA(diva3);
					noExenta3.setTipoNoExenta(vat.isOtherISP() 
							? TipoOperacionSujetaNoExentaType.S_2
							: TipoOperacionSujetaNoExentaType.S_1);
					st3.setNoExenta(noExenta3);
				}
				if ((st3.getExenta() != null || st3.getNoExenta() != null) && !oss)
					prestacion.setSujeta(st3);
				tcdt.setPrestacionServicios(prestacion);
			} else {
				TipoSinDesgloseType entrega = new TipoSinDesgloseType();
				if(!noSujeta.equals(0.0)) {
					NoSujetaType nst2 = new NoSujetaType();
					if ((vat.isIntracommunity() && vat.isOtherISP()) || oss) {
						nst2.setImporteTAIReglasLocalizacion(Double.toString(AonMathUtils.round(noSujeta)));
					} else nst2.setImportePorArticulos714Otros(Double.toString(AonMathUtils.round(noSujeta))); // TODO
					// // TODO
					entrega.setNoSujeta(nst2);
				}
				SujetaType st2 = new SujetaType();
					
				https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.Exenta exenta2 = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.Exenta();
				DetalleExentaType detalleExenta = new DetalleExentaType();
				detalleExenta.setBaseImponible(Double.toString(AonMathUtils.round(exenta)));

				if(vat.isIntracommunity()){
					detalleExenta.setCausaExencion(CausaExencionType.E_5);
				}else if(vat.isExtracommunity() ||  vat.isCanCeuMel()){
					detalleExenta.setCausaExencion(CausaExencionType.E_2);
				} else {
					detalleExenta.setCausaExencion(CausaExencionType.E_6);
				}
				exenta2.getDetalleExenta().add(detalleExenta);
				st2.setExenta(exenta2); // TODO
				if(!noExenta.isEmpty()){
					https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta.DesgloseIVA diva2 = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta.DesgloseIVA();
					noExentaMap.keySet().stream().forEach(key -> {
						DetalleIVAEmitidaType diet = new DetalleIVAEmitidaType();
						diet.setBaseImponible(Double.toString(AonMathUtils.round(noExentaMap.get(key).getBase())));
						diet.setCuotaRepercutida(Double.toString(AonMathUtils.round(noExentaMap.get(key).getQuota())));
						diet.setTipoImpositivo(Double.toString(AonMathUtils.round(key)));
						if(noExentaMap.get(key).getSurchargePercent() > 0.0 && noExentaMap.get(key).getSurchargeQuota() > 0.0){
							diet.setTipoRecargoEquivalencia(Double.toString(AonMathUtils.round(noExentaMap.get(key).getSurchargeQuota())));
							diet.setCuotaRecargoEquivalencia(Double.toString(AonMathUtils.round(noExentaMap.get(key).getSurchargePercent())));
						}
						diva2.getDetalleIVA().add(diet);
					});

					https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta noExenta2 = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta();
					noExenta2.setDesgloseIVA(diva2);
					noExenta2.setTipoNoExenta(vat.isOtherISP()
							? TipoOperacionSujetaNoExentaType.S_2
							: TipoOperacionSujetaNoExentaType.S_1);
					st2.setNoExenta(noExenta2); // TODO
				}
				if((st2.getExenta() != null || st2.getNoExenta() != null) && !oss)
					entrega.setSujeta(st2);
				tcdt.setEntrega(entrega);
			}				
			tipoDesglose.setDesgloseTipoOperacion(tcdt);
		} else {
			TipoSinDesgloseType tsdt = new TipoSinDesgloseType();
			if(!noSujeta.equals(0.0)) {
				NoSujetaType nst = new NoSujetaType();
				if (oss)
					nst.setImporteTAIReglasLocalizacion(Double.toString(AonMathUtils.round(noSujeta))); 
				else
					nst.setImportePorArticulos714Otros(Double.toString(AonMathUtils.round(noSujeta))); // TODO
				//	nst.setImporteTAIReglasLocalizacion(""); // TODO
				tsdt.setNoSujeta(nst);
			}
			SujetaType st = new SujetaType();
		
			https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta noExenta1 = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta();
			https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta.DesgloseIVA diva = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.NoExenta.DesgloseIVA();
			if(AonMathUtils.isNotZero(exenta)) {
				if(vat.isOtherISP()) {
					DetalleIVAEmitidaType diet = new DetalleIVAEmitidaType();
					diet.setBaseImponible(Double.toString(AonMathUtils.round(exenta))); 
					diet.setCuotaRepercutida("0"); 
					diet.setTipoImpositivo("0"); 
					diva.getDetalleIVA().add(diet);

					noExenta1.setDesgloseIVA(diva);
					noExenta1.setTipoNoExenta(TipoOperacionSujetaNoExentaType.S_2);		
					st.setNoExenta(noExenta1);
				} else {
					https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.Exenta exenta1 = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SujetaType.Exenta();
					DetalleExentaType detalleExenta = new DetalleExentaType();
					detalleExenta.setBaseImponible(Double.toString(AonMathUtils.round(exenta)));
					if(vat.isIntracommunity()){
						detalleExenta.setCausaExencion(CausaExencionType.E_5);
					}else if(vat.isExtracommunity() ||  vat.isCanCeuMel()){
						detalleExenta.setCausaExencion(CausaExencionType.E_2);
					} else {
						detalleExenta.setCausaExencion(CausaExencionType.E_6);
					}
					exenta1.getDetalleExenta().add(detalleExenta);
					st.setExenta(exenta1);
				}
			}			
			if(!noExenta.isEmpty()){				
				noExentaMap.keySet().stream().forEach(key -> {
					DetalleIVAEmitidaType diet = new DetalleIVAEmitidaType();
					diet.setBaseImponible(Double.toString(AonMathUtils.round(noExentaMap.get(key).getBase())));
					diet.setCuotaRepercutida(Double.toString(AonMathUtils.round(noExentaMap.get(key).getQuota())));
					diet.setTipoImpositivo(Double.toString(AonMathUtils.round(key)));
					if(noExentaMap.get(key).getSurchargePercent() > 0.0 && noExentaMap.get(key).getSurchargeQuota() > 0.0){
						diet.setTipoRecargoEquivalencia(Double.toString(AonMathUtils.round(noExentaMap.get(key).getSurchargePercent())));
						diet.setCuotaRecargoEquivalencia(Double.toString(AonMathUtils.round(noExentaMap.get(key).getSurchargeQuota())));
					}
					diva.getDetalleIVA().add(diet);
				});

				noExenta1.setDesgloseIVA(diva);

				noExenta1.setTipoNoExenta(vat.isOtherISP()
						? TipoOperacionSujetaNoExentaType.S_2
						: TipoOperacionSujetaNoExentaType.S_1);
				
				st.setNoExenta(noExenta1);
			}
			if(st.getExenta() != null || st.getNoExenta() != null)
				tsdt.setSujeta(st);
			tipoDesglose.setDesgloseFactura(tsdt);
		}
		fet.setTipoDesglose(tipoDesglose);
			
		factura.setFacturaExpedida(fet);
		suministro.getRegistroLRFacturasEmitidas().add(factura);
		return suministro;
	}
	
	// ------------------- SUMINISTRO FACTURAS EMITIDAS COBROS
	
	public byte[] getSuministroFacturasEmitidasCobros(SuministroLRCobrosEmitidas suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLRCobrosEmitidas.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	public byte[] getRespuestaSuministroFacturasEmitidasCobros(RespuestaLRCobrosEmitidasType suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRCobrosEmitidasType.class);
			QName qName = new QName("https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro", "RespuestaLRCobrosEmitidasType");
		    JAXBElement<RespuestaLRCobrosEmitidasType> root = new JAXBElement<>(qName, RespuestaLRCobrosEmitidasType.class, suministro);
			b = writeXml(ctx, root);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	/**
	 * Libro de registro de Facturas expedidas, COBROS.
	 *  
	 * @param company
	 */
	public SuministroLRCobrosEmitidas suministroFacturasEmitidasCobros(Domain domain, String login, Company company, LinkedList<Finance> financeList, Integer invoiceId) {
		SuministroLRCobrosEmitidas suministro = new SuministroLRCobrosEmitidas();
		
		suministro.setCabecera(cabeceraCobrosPagos(company));

		LRCobrosEmitidasType cobros = new LRCobrosEmitidasType();
		CobrosType ct = new CobrosType();
		financeList.stream().filter(f -> f.getInvoice().getId().equals(invoiceId)).forEach(f -> {
			DatosPagoCobroType dpct = new DatosPagoCobroType();
			dpct.setFecha(AonDateUtils.format(f.getDueDate(), "dd-MM-yyyy"));
			dpct.setImporte(Double.toString(AonMathUtils.round(f.getAmount())));
			if(f.getPayMethodType().equals(PayMethodType.BANK_TRANSFER)){
				dpct.setMedio("01");
			} else if(f.getPayMethodType().equals(PayMethodType.CHEQUE)){
				dpct.setMedio("02");
			} else dpct.setMedio("04");
			ct.getCobro().add(dpct);
		});
			
		Invoice invoice = financeList.stream().filter(f -> f.getInvoice().getId().equals(invoiceId)).findFirst().get().getInvoice();
		cobros.setCobros(ct);
		IDFacturaExpedidaBCType f = new IDFacturaExpedidaBCType();
		f.setFechaExpedicionFacturaEmisor(AonDateUtils.format(invoice.getIssueDate(), "dd-MM-yyyy"));
		https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaBCType.IDEmisorFactura emisor = new https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaBCType.IDEmisorFactura();
		emisor.setNIF(company.getDocument());
		f.setIDEmisorFactura(emisor);
		f.setNumSerieFacturaEmisor(invoice.getReferenceCode());
		cobros.setIDFactura(f);
		
		suministro.getRegistroLRCobros().add(cobros);

		return suministro;
	}
	


	// -------------------- FUNCIONES
	
	/**
	 * Devuelve la cabecera para cobros y pagos.
	 * 
	 * @param company
	 * @return CabeceraSiiCobrosPagos
	 */
	private CabeceraSiiCobrosPagos cabeceraCobrosPagos(Company company){
		CabeceraSiiCobrosPagos cabecera = new CabeceraSiiCobrosPagos();
		cabecera.setIDVersionSii("1.1");
		PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
		titular.setNIF(company.getDocument());
		titular.setNombreRazon(company.getName());
		cabecera.setTitular(titular);
		return cabecera;
	}
	
	/**
	 * Devuelve la cabecera.
	 * 
	 * @param company
	 * @return CabeceraSii
	 */
	private CabeceraSii cabecera(Company company, Boolean mod, String terceros){
		CabeceraSii cabecera = cabecera(company, mod ? ClaveTipoComunicacionType.A_1 : ClaveTipoComunicacionType.A_0);
		if(terceros != null && !terceros.equals("false"))
			cabecera.getTitular().setNIFRepresentante(terceros);
		return cabecera;
	}
	
	/**
	 * Devuelve la contraparte, persona fisica o juridica (cliente ó proveedor).
	 * 
	 * @param invoice
	 * @return PersonaFisicaJuridicaType
	 */
	private PersonaFisicaJuridicaType contraparte(VatContext vat) {
		PersonaFisicaJuridicaType contraparte = new PersonaFisicaJuridicaType();
		contraparte.setNombreRazon(vat.getRegistryName());

		if((vat.getRegistryDocumentCountry() == null || vat.getRegistryDocumentCountry().equals(Country.ES))
				&& (!vat.getInvoiceType().equals(InvoiceType.SALES) || !isPersonaFisica(vat.getRegistryDocument()) ||  validateNif(vat.getRegistryDocument(), vat.getRegistryName(), vat.getRegistryDocumentType()))){
			contraparte.setNIF(vat.getRegistryDocument());
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryType2.valueOf(vat.getRegistryDocumentCountry().getAeatCode()));
			otro.setID(vat.getRegistryDocument());
			otro.setIDType(vat.getRegistryDocumentCountry().equals(Country.ES) ? 
				IDType.NO_CENSADO.getName() : IDType.valueOf(vat.getRegistryDocumentType()).getName());
			contraparte.setIDOtro(otro);
		}	
		return contraparte;
	}
	
	private PersonaFisicaJuridicaType contraparteIntracomunitario(VatContext vat) {
		PersonaFisicaJuridicaType contraparte = new PersonaFisicaJuridicaType();
		contraparte.setNombreRazon(vat.getRegistryName());
		if(vat.getRegistryDocumentCountry().equals(Country.ES)
				&& validateNif(vat.getRegistryDocument(), vat.getRegistryName(),vat.getRegistryDocumentType())){
			contraparte.setNIF(vat.getRegistryDocument());
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryType2.valueOf(vat.getRegistryDocumentCountry().getAeatCode()));

			String document = vat.getRegistryDocument();
			if(!document.substring(0,2).equalsIgnoreCase(vat.getRegistryDocumentCountry().getAeatCode())) {
				boolean isGrecia = Country.GR.equals(vat.getRegistryDocumentCountry());
				String countryDocument = isGrecia ? "EL" : vat.getRegistryDocumentCountry().getAeatCode();
				document = countryDocument + document;
			}
			otro.setID(document);		
			
			otro.setIDType(IDType.NIF_IVA.getName());
			contraparte.setIDOtro(otro);
		}	
		return contraparte;
	}
	
	private Boolean isPersonaFisica(String document){
		String pri = document.substring(0, 1);
	return document.length() == 9 
		&& (isNumber(pri) || pri.equals("L") || pri.equals("K") || pri.equals("Z"));
	}
	
	private Boolean isNumber(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private Boolean validateNif(String nif, String name, DocumentType type) {
		return !DocumentType.NOT_CENSUSED.equals(type);
	}
	
	private boolean isNDocument(String document) {
		return !AonStringUtils.isBlank(document) && 'N' == document.charAt(0);
	}
	
}
