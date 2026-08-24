package net.aonsolutions.aon.sii.aeat;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.server.AonDateUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaLRBajaFRecibidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CabeceraSiiBaja;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CountryType2;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaRecibidaNombreBCType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaRecibidaNombreBCType.IDEmisorFactura;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDOtroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.PersonaFisicaJuridicaESType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.BajaLRFacturasRecibidas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRBajaRecibidasType;
import net.aonsolutions.aon.sii.IDType;

/**
 * Baja de facturas recibidas del libro de registro de facturas recibidas.
 */
public class FacturasRecibidasBaja extends SIIBuilt {

	public static FacturasRecibidasBaja getInstance() {
		return new FacturasRecibidasBaja();
	}

	public FacturasRecibidasBaja() {

	}

	// ------------------- BAJA FACTURAS RECIBIDAS

	/**
	 * Da de baja una factura recibida en el SII.
	 *
	 * La factura se identifica igual que en el alta (emisor, numero de serie y
	 * fecha de expedicion); si alguno de esos datos no coincide con lo
	 * suministrado en su dia, la AEAT no localiza el registro.
	 *
	 * @param company empresa titular del libro registro
	 * @param invoice factura que se da de baja
	 * @return BajaLRFacturasRecibidas
	 */
	public BajaLRFacturasRecibidas bajaFacturasRecibidas(Company company, Invoice invoice) {
		BajaLRFacturasRecibidas baja = new BajaLRFacturasRecibidas();
		baja.setCabecera(cabeceraBaja(company));

		LRBajaRecibidasType factura = new LRBajaRecibidasType();

		IDFacturaRecibidaNombreBCType idFactura = new IDFacturaRecibidaNombreBCType();
		idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(invoice.getIssueDate(), "dd-MM-yyyy"));
		idFactura.setNumSerieFacturaEmisor(invoice.getReferenceCode());
		idFactura.setIDEmisorFactura(emisor(invoice));
		factura.setIDFactura(idFactura);

		factura.setPeriodoLiquidacion(periodoLiquidacion(invoice.getTaxDate(), false));

		baja.getRegistroLRBajaRecibidas().add(factura);

		return baja;
	}

	public byte[] getBajaFacturasRecibidas(BajaLRFacturasRecibidas suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(BajaLRFacturasRecibidas.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	public byte[] getRespuestaBajaFacturasRecibidas(RespuestaLRBajaFRecibidasType suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRBajaFRecibidasType.class);
			QName qName = new QName("https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro", "RespuestaLRBajaFRecibidasType");
			JAXBElement<RespuestaLRBajaFRecibidasType> root = new JAXBElement<>(qName, RespuestaLRBajaFRecibidasType.class, suministro);
			b = writeXml(ctx, root);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * Devuelve el emisor de la factura, es decir el proveedor.
	 *
	 * @param invoice
	 * @return IDEmisorFactura
	 */
	private IDEmisorFactura emisor(Invoice invoice) {
		IDEmisorFactura emisor = new IDEmisorFactura();
		emisor.setNombreRazon(invoice.getRegistryName());

		if(invoice.getRegistryDocumentCountry().equals(Country.ES)) {
			emisor.setNIF(invoice.getRegistryDocument());
		} else if(invoice.isIntracommunity()) {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryType2.valueOf(invoice.getRegistryDocumentCountry().getAeatCode()));
			String document = invoice.getRegistryDocument();
			if(!document.substring(0,2).equals(invoice.getRegistryDocumentCountry().getAeatCode())) {
				boolean isGrecia = Country.GR.equals(invoice.getRegistryDocumentCountry());
				String countryDocument = isGrecia ? "EL" : invoice.getRegistryDocumentCountry().getAeatCode();
				document = countryDocument + document;
			}
			otro.setID(document);
			otro.setIDType(IDType.NIF_IVA.getName());
			emisor.setIDOtro(otro);
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryType2.valueOf(invoice.getRegistryDocumentCountry().getAeatCode()));
			otro.setID(invoice.getRegistryDocument());
			otro.setIDType(IDType.valueOf(invoice.getRegistryDocumentType()).getName());
			emisor.setIDOtro(otro);
		}
		return emisor;
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
