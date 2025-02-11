package net.aonsolutions.aon.sii.aeat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CountryType2;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDOtroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.PersonaFisicaJuridicaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.RegistroSii.PeriodoLiquidacion;
import net.aonsolutions.aon.sii.IDType;


public class SIIBuilt {

	/**
	 * Devuelve el periodo Impositivo ó periodo de liquidación.
	 * 
	 * @param invoice
	 * @return PeriodoImpositivo
	 */
	protected PeriodoLiquidacion periodoLiquidacion(Date taxDate, Boolean anual){
		Integer year = AonDateUtils.getYear(taxDate);
		Integer month = AonDateUtils.getMonth(taxDate) + 1;
		String p = month.toString();
		if(month < 10){
			p = "0" + p;
		}
		PeriodoLiquidacion periodo = new PeriodoLiquidacion();
		periodo.setEjercicio(year.toString());
		if(anual){
			periodo.setPeriodo("0A");
		} else {
			periodo.setPeriodo(p);// mes (01,02,03,04,...,12) || anual (0A));
		}
		return periodo;
	}
	

	/**
	 * Devuelve el periodo Impositivo ó periodo de liquidación.
	 * 
	 * @param invoice
	 * @return PeriodoImpositivo
	 */
	protected PeriodoLiquidacion periodoLiquidacion(Date invDate, Date date, boolean anual, boolean errorPeriodo){
		Integer nowDay = errorPeriodo ? AonDateUtils.getDay(date) : AonDateUtils.getDay(new Date());
		Integer nowMonth = errorPeriodo ? AonDateUtils.getMonth(date) + 1 : AonDateUtils.getMonth(new Date()) + 1;
		Integer nowYear = errorPeriodo ? AonDateUtils.getYear(date) : AonDateUtils.getYear(new Date());
		
		Integer invYear = AonDateUtils.getYear(invDate);
		Integer invMonth = AonDateUtils.getMonth(invDate) + 1;
		
		Integer year = AonDateUtils.getYear(date);
		Integer month = AonDateUtils.getMonth(date) + 1;
		
		if(nowYear > invYear) nowMonth = nowMonth + 12;
		Integer diffMonth = nowMonth-invMonth; 
		if(!invMonth.equals(nowMonth) && diffMonth < 2 && nowDay <= 15) {
			year = invYear;
			month = invMonth;
		} 
		
		String p = month.toString();
		if(month < 10){
			p = "0" + p;
		}
		PeriodoLiquidacion periodo = new PeriodoLiquidacion();
		periodo.setEjercicio(year.toString());
		if(anual){
			periodo.setPeriodo("0A");
		} else {
			periodo.setPeriodo(p);
		}
		return periodo;
	}

	public PersonaFisicaJuridicaType getContraparte(Invoice invoice) {
		PersonaFisicaJuridicaType contraparte = new PersonaFisicaJuridicaType();
		contraparte.setNombreRazon(invoice.getRegistryName());
		
		if((invoice.isNational() || invoice.isIsp() || invoice.isCanCeuMel())
				&& Country.ES.equals(invoice.getRegistryDocumentCountry())) {
			if(DocumentType.NOT_CENSUSED.equals(invoice.getRegistryDocumentType())) {
				IDOtroType otro = new IDOtroType();
				otro.setCodigoPais(CountryType2.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
				otro.setID(invoice.getRegistryDocument());
				otro.setIDType(invoice.getRegistryDocumentCountry().equals(Country.ES) ? 
					IDType.NO_CENSADO.getName() : IDType.valueOf(invoice.getRegistryDocumentType()).getName());
				contraparte.setIDOtro(otro);
			} else contraparte.setNIF(invoice.getRegistryDocument());
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryType2.valueOf(invoice.getRegistryDocumentCountry().getIso2()));

			String document = invoice.getRegistryDocument();
			if(!document.substring(0,2).equalsIgnoreCase(invoice.getRegistryDocumentCountry().getIso2())) {
				boolean isGrecia = Country.GR.equals(invoice.getRegistryDocumentCountry());
				String countryDocument = isGrecia ? "EL" : invoice.getRegistryDocumentCountry().getIso2();
				document = countryDocument + document;
			}
			otro.setID(document);		
			
			otro.setIDType(IDType.NIF_IVA.getName());
			contraparte.setIDOtro(otro);
		}
		return contraparte;
	}
	
	public String getMedioCobrosPagos(PayMethodType type) {
		if(PayMethodType.BANK_TRANSFER.equals(type)) return "01"; 
		else if(PayMethodType.CHEQUE.equals(type)) return "02";
		else return "04";
	}
	
	public static Object readXml(JAXBContext ctx, byte[] xmlFile) throws JAXBException{
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
	
		InputStream input = new ByteArrayInputStream(xmlFile);
		return unmarshaller.unmarshal(input);
	}
	
	public static byte[] writeXml(JAXBContext ctx, Object object) throws JAXBException, IOException{		
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		marshaller.marshal(object, baos);
		baos.close();

		return baos.toByteArray();
	}	
}
