package net.aonsolutions.aon.sii.gipuzkoa;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;

import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaLRBajaBienesInversionType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaLRBienesInversionType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.BienDeInversionType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.CabeceraSii;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.CabeceraSiiBaja;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.ClaveTipoComunicacionType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.CountryType2;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.IDFacturaComunitariaType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.IDFacturaComunitariaType.IDEmisorFactura;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.IDOtroType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.PersonaFisicaJuridicaESType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.BajaLRBienesInversion;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.LRBajaBienesInversionType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.LRBienesInversionType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.SuministroLRBienesInversion;
import net.aonsolutions.aon.sii.IDType;

public class BienesInversion extends SIIBuilt{

	public static BienesInversion getInstance() {
		return new BienesInversion();
	}
	
	public BienesInversion() {

	}
	// ------------------- BIENES INVERSION
	
	public byte[] getSuministroBienesInversion(Domain domain, String login, Company company,
			Integer invoiceId, LinkedList<VatContext> contextList, Boolean mod, String terceros) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLRBienesInversion.class);
			b = writeXml(ctx, suministroBienesInversion(domain, login, company, invoiceId, contextList, mod, terceros));
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	protected byte[] getSuministroBienesInversion(SuministroLRBienesInversion suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLRBienesInversion.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	protected byte[] getRespuestaSuministroBienesInversion(RespuestaLRBienesInversionType suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRBienesInversionType.class);
			QName qName = new QName("https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro", "RespuestaLRBienesInversionType");
		    JAXBElement<RespuestaLRBienesInversionType> root = new JAXBElement<>(qName, RespuestaLRBienesInversionType.class, suministro);
			b = writeXml(ctx, root);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	protected byte[] getBajaBienesInversion(BajaLRBienesInversion suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(BajaLRBienesInversion.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	protected byte[] getRespuestaBajaBienesInversion(RespuestaLRBajaBienesInversionType suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRBajaBienesInversionType.class);
			QName qName = new QName("https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro", "RespuestaLRBajaBienesInversionType");
		    JAXBElement<RespuestaLRBajaBienesInversionType> root = new JAXBElement<>(qName, RespuestaLRBajaBienesInversionType.class, suministro);
			b = writeXml(ctx, root);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * Libro de registro de Bienes de Inversión.
	 * 
	 * @param company
	 * @param invoiceList
	 */
	protected SuministroLRBienesInversion suministroBienesInversion(Domain domain, String login, Company company,
			Integer invoiceId, LinkedList<VatContext> contextList, Boolean mod, String terceros) {
		SuministroLRBienesInversion suministro = new SuministroLRBienesInversion();

		// CABECERA
		suministro.setCabecera(cabecera(company, mod, terceros));

		// BODY
		VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoiceId)).findFirst()
				.orElse(new VatContext());

		LRBienesInversionType bien = new LRBienesInversionType();

		bien.setPeriodoLiquidacion(periodoLiquidacion(vat.getTaxDate(), true));

		IDFacturaComunitariaType idFactura = new IDFacturaComunitariaType();

		idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));

		IDEmisorFactura emisor = new IDEmisorFactura();
		emisor.setNombreRazon(vat.getRegistryName());
		if (vat.getRegistryDocumentCountry().equals(Country.ES)) {
			emisor.setNIF(vat.getRegistryDocument());
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryType2.valueOf(vat.getRegistryDocumentCountry().getAeatCode()));

			String document = vat.getRegistryDocument();
			if (!document.substring(0, 2).equals(vat.getRegistryDocumentCountry().getAeatCode())) {
				document = vat.getRegistryDocumentCountry().getAeatCode() + document;
			}
			otro.setID(document);
			otro.setIDType(IDType.NIF_IVA.getName());
			emisor.setIDOtro(otro);
		}

		idFactura.setIDEmisorFactura(emisor);

		idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());

		bien.setIDFactura(idFactura);

		BienDeInversionType bdit = new BienDeInversionType();

		bdit.setFechaInicioUtilizacion(AonDateUtils.format(vat.getAmortizationInitialDate(), "dd-MM-yyyy"));
		bdit.setIdentificacionBien(vat.getAmortizationDescription());
		bdit.setProrrataAnualDefinitiva(vat.getAmortizationPercentage().toString());
		// bdit.setRegularizacionAnualDeduccion(""); // OPTIONAL
		// bdit.setIdentificacionEntrega(""); // OPTIONAL
		// bdit.setRegularizacionDeduccionEfectuada(""); // OPTIONAL
		bien.setBienesInversion(bdit);

		suministro.getRegistroLRBienesInversion().add(bien);
		return suministro;
	}

	protected BajaLRBienesInversion bajaBienesInversion(Company company, Integer invoiceId,
			LinkedList<VatContext> vatList, String terceros) {
		BajaLRBienesInversion baja = new BajaLRBienesInversion();
		baja.setCabecera(cabeceraBaja(company, terceros));

		VatContext vat = vatList.stream().filter(f -> f.getInvoice().equals(invoiceId)).findFirst()
				.orElse(new VatContext());

		LRBajaBienesInversionType factura = new LRBajaBienesInversionType();

		factura.setIdentificacionBien(""); // TODO
		IDFacturaComunitariaType idFactura = new IDFacturaComunitariaType();
		idFactura.setFechaExpedicionFacturaEmisor(AonDateUtils.format(vat.getIssueDate(), "dd-MM-yyyy"));
		idFactura.setNumSerieFacturaEmisor(vat.getReferenceCode());
		IDEmisorFactura emisor = new IDEmisorFactura();
		emisor.setNIF(company.getDocument());
		idFactura.setIDEmisorFactura(emisor);
		factura.setIDFactura(idFactura);

		factura.setPeriodoLiquidacion(periodoLiquidacion(vat.getTaxDate(), false));

		baja.getRegistroLRBajaBienesInversion().add(factura);
		return baja;
	}

	// -------------------- FUNCIONES

	/**
	 * Devuelve la cabecera.
	 * 
	 * @param company
	 * @return CabeceraSii
	 */
	public CabeceraSii cabecera(Company company, Boolean mod, String terceros) {
		CabeceraSii cabecera = new CabeceraSii();
		cabecera.setIDVersionSii("1.1");
		cabecera.setTipoComunicacion(mod ? ClaveTipoComunicacionType.A_1 : ClaveTipoComunicacionType.A_0);
		PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
		titular.setNIF(company.getDocument());
		titular.setNombreRazon(company.getName());
		if (terceros != null && !terceros.equals("false"))
			titular.setNIFRepresentante(terceros);
		cabecera.setTitular(titular);

		return cabecera;
	}

	public CabeceraSii cabecera(Company company) {
		CabeceraSii cabecera = new CabeceraSii();
		cabecera.setIDVersionSii("1.1");
		cabecera.setTipoComunicacion(ClaveTipoComunicacionType.A_0);
		PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
		titular.setNIF(company.getDocument());
		titular.setNombreRazon(company.getName());
		cabecera.setTitular(titular);
		return cabecera;
	}

	/**
	 * Devuelve la cabecera para bajas.
	 * 
	 * @param company
	 * @return CabeceraSiiBaja
	 */
	private CabeceraSiiBaja cabeceraBaja(Company company, String terceros) {
		CabeceraSiiBaja cabecera = new CabeceraSiiBaja();
		cabecera.setIDVersionSii("1.1");
		PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
		titular.setNIF(company.getDocument());
		titular.setNombreRazon(company.getName());
		if (terceros != null && !terceros.equals("false"))
			titular.setNIFRepresentante(terceros);
		cabecera.setTitular(titular);
		return cabecera;
	}

	public Boolean validateNif(String nif, String name, DocumentType type) {
		return !type.equals(DocumentType.NOT_CENSUSED);
		/*
		 * VNifV1Ent vnif = new VNifV1Ent(); vnif.setNif(nif); vnif.setNombre(name);
		 * return NIFPost.getInstance(cert, pass).vnifV1(vnif);
		 */
	}

	public static byte[] writeXml(JAXBContext ctx, Object object) throws JAXBException, IOException {
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		marshaller.marshal(object, baos);
		baos.close();

		return baos.toByteArray();
	}

	public static Object readXml(JAXBContext ctx, byte[] xmlFile) throws JAXBException {
		Unmarshaller unmarshaller = ctx.createUnmarshaller();

		InputStream input = new ByteArrayInputStream(xmlFile);
		return unmarshaller.unmarshal(input);
	}
}
