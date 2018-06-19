package net.aonsolutions.aon.sii.aeat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;
import com.esferalia.aon.watson.util.AonMathUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaLRAgenciasViajesType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaLRIMetalicoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaLROperacionesSegurosType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CabeceraSii;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.ClaveOperacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.ClaveTipoComunicacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CountryType2;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDOtroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.PersonaFisicaJuridicaESType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.PersonaFisicaJuridicaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRAgenciasViajesType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRCobrosMetalicoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LROperacionesSegurosType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.SuministroLRAgenciasViajes;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.SuministroLRCobrosMetalico;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.SuministroLROperacionesSeguros;
import net.aonsolutions.aon.sii.IDType;


public class OperacionesTrascendenciaTributaria extends SIIBuilt{

	public static OperacionesTrascendenciaTributaria getInstance() {
		return new OperacionesTrascendenciaTributaria();
	}
	
	public OperacionesTrascendenciaTributaria() {

	}
	
	// ------------------- SUMINISTRO COBROS METALICO

	protected byte[] getSuministroCobrosMetalico(SuministroLRCobrosMetalico suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLRCobrosMetalico.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	protected byte[] getRespuestaSuministroCobrosMetalico(RespuestaLRIMetalicoType suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRIMetalicoType.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	/**
	 * Suministros de Operaciones en metálico.
	 * 
	 * @param company
	 * @param invoiceList
	 */
	protected SuministroLRCobrosMetalico suministroCobrosMetalico(Domain domain, String login, Company company,
			Integer invoiceId, LinkedList<VatContext> contextList) {
		SuministroLRCobrosMetalico suministro = new SuministroLRCobrosMetalico();

		// CABECERA
		suministro.setCabecera(cabecera(company));

		// BODY
		VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoiceId)).findFirst()
				.orElse(new VatContext());

		LRCobrosMetalicoType metalico = new LRCobrosMetalicoType();

		metalico.setPeriodoLiquidacion(periodoLiquidacion(vat, false));
		metalico.setContraparte(contraparte(vat));
		metalico.setImporteTotal(Double.toString(AonMathUtils.round(vat.getBase() + vat.getQuota())));

		suministro.getRegistroLRCobrosMetalico().add(metalico);

		return suministro;
	}
		
	// ------------------- SUMINISTRO OPERACIONES SEGUROS
	
	protected byte[] getSuministroOperacionesSeguros(SuministroLROperacionesSeguros suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLROperacionesSeguros.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	protected byte[] getRespuestaSuministroOperacionesSeguros(RespuestaLROperacionesSegurosType suministro) {
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLROperacionesSegurosType.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * Suministro de Operaciones de seguros.
	 * 
	 * @param company
	 * @param invoiceList
	 */
	protected SuministroLROperacionesSeguros suministroOperacionesSeguros(Domain domain, String login, Company company,
			Integer invoiceId, LinkedList<VatContext> contextList) {
		SuministroLROperacionesSeguros suministro = new SuministroLROperacionesSeguros();

		// CABECERA
		suministro.setCabecera(cabecera(company));

		// BODY
		VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoiceId)).findFirst()
				.orElse(new VatContext());

		LROperacionesSegurosType seguros = new LROperacionesSegurosType();

		seguros.setPeriodoLiquidacion(periodoLiquidacion(vat, false));
		seguros.setContraparte(contraparte(vat));
		seguros.setClaveOperacion(ClaveOperacionType.A); // TODO
		seguros.setImporteTotal(Double.toString(AonMathUtils.round(vat.getBase() + vat.getQuota())));

		suministro.getRegistroLROperacionesSeguros().add(seguros);

		return suministro;
	}
			
	// ------------------- SUMINISTRO AGENCIAS VIAJES	
		
	protected byte[] getSuministroAgenciasViajes(SuministroLRAgenciasViajes suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(SuministroLRAgenciasViajes.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	protected byte[] getRespuestaSuministroAgenciasViajes(RespuestaLRAgenciasViajesType suministro){
		JAXBContext ctx;
		byte[] b = null;
		try {
			ctx = JAXBContext.newInstance(RespuestaLRAgenciasViajesType.class);
			b = writeXml(ctx, suministro);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	
	/**
	 * Suministro de Agencias de Viajes.
	 * 
	 * @param company
	 * @param invoiceList
	 */
	protected SuministroLRAgenciasViajes suministroAgenciasViajes(Domain domain, String login, Company company,
			Integer invoiceId, LinkedList<VatContext> contextList) {
		SuministroLRAgenciasViajes suministro = new SuministroLRAgenciasViajes();

		// CABECERA
		suministro.setCabecera(cabecera(company));

		// BODY
		VatContext vat = contextList.stream().filter(f -> f.getInvoice().equals(invoiceId)).findFirst()
				.orElse(new VatContext());

		LRAgenciasViajesType agencias = new LRAgenciasViajesType();

		agencias.setPeriodoLiquidacion(periodoLiquidacion(vat, false));
		agencias.setContraparte(contraparte(vat));
		agencias.setImporteTotal(Double.toString(AonMathUtils.round(vat.getBase() + vat.getQuota())));

		suministro.getRegistroLRAgenciasViajes().add(agencias);
		return suministro;
	}

	// -------------------- FUNCIONES
	
	/**
	 * Devuelve la cabecera.
	 * 
	 * @param company
	 * @return CabeceraSii
	 */
	public CabeceraSii cabecera(Company company, Boolean mod, String terceros){
		CabeceraSii cabecera = new CabeceraSii();
		cabecera.setIDVersionSii("1.0");
		cabecera.setTipoComunicacion(mod ? ClaveTipoComunicacionType.A_1 : ClaveTipoComunicacionType.A_0);
		PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
		titular.setNIF(company.getDocument());
		titular.setNombreRazon(company.getName());
		if(terceros != null && !terceros.equals("false"))
			titular.setNIFRepresentante(terceros);
		cabecera.setTitular(titular);
		
		
		return cabecera;
	}
	public CabeceraSii cabecera(Company company){
		CabeceraSii cabecera = new CabeceraSii();
		cabecera.setIDVersionSii("1.0");
		cabecera.setTipoComunicacion(ClaveTipoComunicacionType.A_0);
		PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
		titular.setNIF(company.getDocument());
		titular.setNombreRazon(company.getName());
		cabecera.setTitular(titular);
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
			otro.setCodigoPais(CountryType2.valueOf(vat.getRegistryDocumentCountry().getIso2()));
			otro.setID(vat.getRegistryDocument());
			otro.setIDType(vat.getRegistryDocumentCountry().equals(Country.ES) ? 
				IDType.NO_CENSADO.getName() : IDType.valueOf(vat.getRegistryDocumentType()).getName());
			contraparte.setIDOtro(otro);
		}	
		return contraparte;
	}
	
	private Boolean isPersonaFisica(String document){
		String pri = document.substring(0, 1);
	return document.length() == 9 
		&& (isNumber(pri) || pri.equals("L") || pri.equals("K"));
	}
	
	private Boolean isNumber(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
	public Boolean validateNif(String nif, String name, DocumentType type) {
		return !type.equals(DocumentType.NOT_CENSUSED);
		/*
		VNifV1Ent vnif = new VNifV1Ent();
		vnif.setNif(nif);
		vnif.setNombre(name);
		return NIFPost.getInstance(cert, pass).vnifV1(vnif);
		*/
	}
	
	public static byte[] writeXml(JAXBContext ctx, Object object) throws JAXBException, IOException{		
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		marshaller.marshal(object, baos);
		baos.close();

		return baos.toByteArray();
	}

	public static Object readXml(JAXBContext ctx, byte[] xmlFile) throws JAXBException{
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
	
		InputStream input = new ByteArrayInputStream(xmlFile);
		return unmarshaller.unmarshal(input);
	}

}
