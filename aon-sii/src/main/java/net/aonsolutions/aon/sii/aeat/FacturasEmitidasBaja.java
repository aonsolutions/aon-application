package net.aonsolutions.aon.sii.aeat;

import java.io.IOException;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.watson.server.AonDateUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaLRBajaFEmitidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CabeceraSiiBaja;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaBCType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaBCType.IDEmisorFactura;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.PersonaFisicaJuridicaESType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.BajaLRFacturasEmitidas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRBajaExpedidasType;

/**
 * Baja de facturas emitidas del libro de registro de facturas expedidas.
 */
public class FacturasEmitidasBaja extends SIIBuilt {

	public static FacturasEmitidasBaja getInstance() {
		return new FacturasEmitidasBaja();
	}

	public FacturasEmitidasBaja() {

	}

	// ------------------- BAJA FACTURAS EMITIDAS

	/**
	 * Da de baja una factura emitida en el SII.
	 *
	 * La factura se identifica igual que en el alta (NIF del emisor, numero de
	 * serie y fecha de expedicion); si alguno de esos datos no coincide con lo
	 * suministrado en su dia, la AEAT no localiza el registro.
	 *
	 * @param company empresa titular del libro registro
	 * @param invoice factura que se da de baja
	 * @return BajaLRFacturasEmitidas
	 */
	public BajaLRFacturasEmitidas bajaFacturasEmitidas(Company company, Invoice invoice) {
		BajaLRFacturasEmitidas baja = new BajaLRFacturasEmitidas();
		baja.setCabecera(cabeceraBaja(company));
		baja.getRegistroLRBajaExpedidas().add(buildBajaFacturaEmitida(company, invoice));
		return baja;
	}
	
	/**
	 * Da de baja varias facturas emitidas en el SII.
	 *
	 * La factura se identifica igual que en el alta (NIF del emisor, numero de
	 * serie y fecha de expedicion); si alguno de esos datos no coincide con lo
	 * suministrado en su dia, la AEAT no localiza el registro.
	 *
	 * @param context contexto de comunicacion con el SII
	 * @return BajaLRFacturasEmitidas
	 */
	public BajaLRFacturasEmitidas bajaFacturasEmitidas(InvoiceCommunicatorContext context) {
		BajaLRFacturasEmitidas baja = new BajaLRFacturasEmitidas();
		baja.setCabecera(cabeceraBaja(context.getCompany()));
		context.invoiceStream().forEach(invoice -> 
			baja.getRegistroLRBajaExpedidas().add(buildBajaFacturaEmitida(context.getCompany(), invoice))
		);
		return baja;
	}

	/**
	 * Construye un registro de baja de factura emitida.
	 *
	 * @param company empresa titular del libro registro
	 * @param invoice factura que se da de baja
	 * @return LRBajaExpedidasType
	 */
	public LRBajaExpedidasType buildBajaFacturaEmitida(Company company, Invoice invoice) {
		LRBajaExpedidasType factura = new LRBajaExpedidasType();
		factura.setRefExterna(invoice.getId().toString());
		IDFacturaExpedidaBCType idFactura = new IDFacturaExpedidaBCType();
		// La misma fecha que se envio en el alta: la de expedicion fiscal si existe.
		Date expDate = invoice.getExpDate() != null ? invoice.getExpDate() : invoice.getIssueDate();
		idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(expDate, "dd-MM-yyyy"));
		idFactura.setNumSerieFacturaEmisor(invoice.getReferenceCode());
		IDEmisorFactura emisor = new IDEmisorFactura();
		emisor.setNIF(company.getDocument());
		idFactura.setIDEmisorFactura(emisor);
		factura.setIDFactura(idFactura);

		factura.setPeriodoLiquidacion(periodoLiquidacion(invoice.getTaxDate(), false));		
		return factura;
	}
	
	public byte[] getBajaFacturasEmitidas(BajaLRFacturasEmitidas suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(BajaLRFacturasEmitidas.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	public byte[] getRespuestaBajaFacturasEmitidas(RespuestaLRBajaFEmitidasType suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRBajaFEmitidasType.class);
			QName qName = new QName("https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro", "RespuestaLRBajaFEmitidasType");
			JAXBElement<RespuestaLRBajaFEmitidasType> root = new JAXBElement<>(qName, RespuestaLRBajaFEmitidasType.class, suministro);
			b = writeXml(ctx, root);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * Devuelve la cabecera para bajas.
	 *
	 * @param company
	 * @return CabeceraSiiBaja
	 */
	private CabeceraSiiBaja cabeceraBaja(Company company) {
		CabeceraSiiBaja cabecera = new CabeceraSiiBaja();
		cabecera.setIDVersionSii("1.1");
		PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
		titular.setNIF(company.getDocument());
		titular.setNombreRazon(company.getName());
		cabecera.setTitular(titular);
		return cabecera;
	}
}
