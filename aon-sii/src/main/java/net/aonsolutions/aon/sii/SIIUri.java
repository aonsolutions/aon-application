package net.aonsolutions.aon.sii;

import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.error.AonCoreException;

public class SIIUri {

	public static SIIUri getInstance() {
		return new SIIUri();
	}
	
	public SIIUri() {

	}
	
	/**
	 * Entorno de PRODUCCION
	 * @param SIIType type
	 * @return URI
	 */
	protected String getURI(SIIType type, Administration place) {
		if(Administration.NAVARRA.equals(place)) return "https://siihacienda.navarra.es/SII_PRODUCCION.proxy/SiiMensajesXsdHandlet.ashx";

		if(SIIType.FACTURAS_EMITIDAS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			else if(Administration.ALAVA.equals(place)) return "https://sii.araba.eus/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			else if(Administration.BIZKAIA.equals(place)) return "https://sii.bizkaia.eus/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			else if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			else return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
		}
		
		if(SIIType.FACTURAS_EMITIDAS_COBROS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fe/SiiFactCOBV1SOAP";
			else if(Administration.ALAVA.equals(place)) return "https://sii.araba.eus/SSII-FACT/ws/fe/SiiFactCOBV1SOAP";
			else if(Administration.BIZKAIA.equals(place)) return "https://sii.bizkaia.eus/SSII-FACT/ws/fe/SiiFactCOBV1SOAP";
			else if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fe/SiiFactCOBV1SOAP";
			else return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fe/SiiFactCOBV1SOAP";
		}
		
		if(SIIType.FACTURAS_RECIBIDAS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			else if(Administration.ALAVA.equals(place)) return "https://sii.araba.eus/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			else if(Administration.BIZKAIA.equals(place)) return "https://sii.bizkaia.eus/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			else if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			else return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
		}
		
		if(SIIType.FACTURAS_RECIBIDAS_PAGOS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			else if(Administration.ALAVA.equals(place)) return "https://sii.araba.eus/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			else if(Administration.BIZKAIA.equals(place)) return "https://sii.bizkaia.eus/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			else if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			else return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
		}
		
		if(SIIType.BIENES_INVERSION.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			else if(Administration.ALAVA.equals(place)) return "https://sii.araba.eus/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			else if(Administration.BIZKAIA.equals(place)) return "https://sii.bizkaia.eus/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			else if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			else return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
		}
		
		if(SIIType.OPERACIONES_INTRACOMUNITARIAS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			else if(Administration.ALAVA.equals(place)) return "https://sii.araba.eus/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			else if(Administration.BIZKAIA.equals(place)) return "https://sii.bizkaia.eus/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			else if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			else return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
		}
		
		if(SIIType.COBROS_METALICO.equals(type) 
				|| SIIType.OPERACIONES_SEGUROS.equals(type)
				|| SIIType.AGENCIAS_VIAJES.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			else if(Administration.ALAVA.equals(place)) return "https://sii.araba.eus/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			else if(Administration.BIZKAIA.equals(place)) return "https://sii.bizkaia.eus/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			else if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			else return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
		}
		
		return "";
	}
	
	/**
	 * Entorno de PRUEBAS
	 * @param SIIType type
	 * @return URI
	 */
	protected String getURIPruebas(SIIType type, Administration place) {
		if(Administration.NAVARRA.equals(place)) return "https://siihacienda.navarra.es/SII_PRUEBAS.proxy/SiiMensajesXsdHandlet.ashx";

		if(SIIType.FACTURAS_EMITIDAS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://prewww1.aeat.es/wlpl/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			else if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			else if(Administration.BIZKAIA.equals(place)) return "https://pruapps.bizkaia.eus/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			else if(Administration.GIPUZKOA.equals(place)) return "https://sii-prep.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			else return "https://prewww1.aeat.es/wlpl/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
		}
		
		if(SIIType.FACTURAS_EMITIDAS_COBROS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://prewww1.aeat.es/wlpl/SSII-FACT/ws/fe/SiiFactCOBV1SOAP"; 
			else if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/fe/SiiFactCOBV1SOAP"; 
			else if(Administration.BIZKAIA.equals(place)) return "https://pruapps.bizkaia.eus/SSII-FACT/ws/fe/SiiFactCOBV1SOAP"; 
			else if(Administration.GIPUZKOA.equals(place)) return "https://sii-prep.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fe/SiiFactCOBV1SOAP"; 
			else return "https://prewww1.aeat.es/wlpl/SSII-FACT/ws/fe/SiiFactCOBV1SOAP";
		}
		
		if(SIIType.FACTURAS_RECIBIDAS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://prewww1.aeat.es/wlpl/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			else if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			else if(Administration.BIZKAIA.equals(place)) return "https://pruapps.bizkaia.eus/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			else if(Administration.GIPUZKOA.equals(place)) return "https://sii-prep.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			else return "https://prewww1.aeat.es/wlpl/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
		}
		
		if(SIIType.FACTURAS_RECIBIDAS_PAGOS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://prewww1.aeat.es/wlpl/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			else if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			else if(Administration.BIZKAIA.equals(place)) return "https://pruapps.bizkaia.eus/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			else if(Administration.GIPUZKOA.equals(place)) return "https://sii-prep.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			else return "https://prewww1.aeat.es/wlpl/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
		}

		if(SIIType.BIENES_INVERSION.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place))return "https://prewww1.aeat.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			else if(Administration.ALAVA.equals(place))return "https://pruebas-sii.araba.eus/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			else if(Administration.BIZKAIA.equals(place))return "https://pruapps.bizkaia.eus/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			else if(Administration.GIPUZKOA.equals(place))return "https://sii-prep.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			else return "https://prewww1.aeat.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
		}

		if(SIIType.OPERACIONES_INTRACOMUNITARIAS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://prewww1.aeat.es/wlpl/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			else if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			else if(Administration.BIZKAIA.equals(place)) return "https://pruapps.bizkaia.eus/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			else if(Administration.GIPUZKOA.equals(place)) return "https://sii-prep.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			else return "https://prewww1.aeat.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
		}

		if(SIIType.COBROS_METALICO.equals(type) 
				|| SIIType.OPERACIONES_SEGUROS.equals(type)
				|| SIIType.AGENCIAS_VIAJES.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://prewww1.aeat.es/wlpl/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			else if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			else if(Administration.BIZKAIA.equals(place)) return "https://pruapps.bizkaia.eus/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			else if(Administration.GIPUZKOA.equals(place)) return "https://sii-prep.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			else return "https://prewww1.aeat.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
		}
		
		return "";
	}
	
	public String getURI(InvoiceCommunicationConfiguration icc, SIIType type) {
		CommunicationData cd = icc.getSiiData().orElseThrow( () -> new AonCoreException("No se han podido obtener los datos de SII") );
		if (cd.isTest()) {
			return getURIPruebas(type, cd.getAdministration().orElse( null ));
		}  
		return getURI(type, cd.getAdministration().orElse( null ));
	}
}