package net.aonsolutions.aon.sii;

import com.esferalia.aon.occam.api.model.type.Administration;

public class SIIUri {

	public static SIIUri getInstance() {
		return new SIIUri();
	}
	
	public SIIUri() {

	}
	
	/**
	 * Entorno de PRODUCCION d�a 1 de julio 2017 
	 * @param SIIType type
	 * @return URI
	 */
	protected String getURI(SIIType type, Administration place) {
		if(SIIType.FACTURAS_EMITIDAS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://sii.araba.eus/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://sii.bizkaia.eus/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
		}
		if(SIIType.FACTURAS_EMITIDAS_COBROS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fe/SiiFactCOBV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://sii.araba.eus/SSII-FACT/ws/fe/SiiFactCOBV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://sii.bizkaia.eus/SSII-FACT/ws/fe/SiiFactCOBV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fe/SiiFactCOBV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fe/SiiFactCOBV1SOAP";
		}
		if(SIIType.FACTURAS_RECIBIDAS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://sii.araba.eus/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://sii.bizkaia.eus/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
		}
		if(SIIType.FACTURAS_RECIBIDAS_PAGOS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://sii.araba.eus/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://sii.bizkaia.eus/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
		}
		if(SIIType.BIENES_INVERSION.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://sii.araba.eus/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://sii.bizkaia.eus/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
		}
		if(SIIType.OPERACIONES_INTRACOMUNITARIAS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://sii.araba.eus/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://sii.bizkaia.eus/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
		}
		if(SIIType.COBROS_METALICO.equals(type) 
				|| SIIType.OPERACIONES_SEGUROS.equals(type)
				|| SIIType.AGENCIAS_VIAJES.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://sii.araba.eus/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://sii.bizkaia.eus/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
		}
		return "";
	}
	
	/**
	 * Entorno de PRODUCCION d�a 1 de julio 2017 para acceso con certificado de sello
	 * @param SIIType type
	 * @return URI
	 */
	protected String getURISello(SIIType type, Administration place) {
		if(SIIType.FACTURAS_EMITIDAS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://apps.bizkaia.eus/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
		}
		if(SIIType.FACTURAS_EMITIDAS_COBROS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fe/SiiFactCOBV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/fe/SiiFactCOBV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://apps.bizkaia.eus/SSII-FACT/ws/fe/SiiFactCOBV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fe/SiiFactCOBV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fe/SiiFactCOBV1SOAP";
		}
		if(SIIType.FACTURAS_RECIBIDAS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://apps.bizkaia.eus/wlpl/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
		}
		if(SIIType.FACTURAS_RECIBIDAS_PAGOS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://apps.bizkaia.eus/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
		}
		if(SIIType.BIENES_INVERSION.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://apps.bizkaia.eus/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
		}
		if(SIIType.OPERACIONES_INTRACOMUNITARIAS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://apps.bizkaia.eus/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
		}
		if(SIIType.COBROS_METALICO.equals(type) 
				|| SIIType.OPERACIONES_SEGUROS.equals(type)
				|| SIIType.AGENCIAS_VIAJES.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://apps.bizkaia.eus/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
		}
		return "";
	}
	
	/**
	 * Entorno de PRUEBAS (tambien valido para acceso con certificados de sello)
	 * @param SIIType type
	 * @return URI
	 */
	protected String getURIPruebas(SIIType type, Administration place) {
		if(SIIType.FACTURAS_EMITIDAS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://pruapps.bizkaia.eus/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
//			if(Administration.GIPUZKOA.equals(place)) return "https://prep9.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii-prep.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
		}
		if(SIIType.FACTURAS_EMITIDAS_COBROS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/fe/SiiFactCOBV1SOAP"; 
			if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/fe/SiiFactCOBV1SOAP"; 
			if(Administration.BIZKAIA.equals(place)) return "https://pruapps.bizkaia.eus/SSII-FACT/ws/fe/SiiFactCOBV1SOAP"; 
//			if(Administration.GIPUZKOA.equals(place)) return "https://prep9.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fe/SiiFactCOBV1SOAP"; 
			if(Administration.GIPUZKOA.equals(place)) return "https://sii-prep.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fe/SiiFactCOBV1SOAP"; 
			if(Administration.NAVARRA.equals(place)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/fe/SiiFactCOBV1SOAP"; 
		}
		if(SIIType.FACTURAS_RECIBIDAS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://pruapps.bizkaia.eus/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
//			if(Administration.GIPUZKOA.equals(place)) return "https://prep9.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii-prep.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
		}
		if(SIIType.FACTURAS_RECIBIDAS_PAGOS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://pruapps.bizkaia.eus/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
//			if(Administration.GIPUZKOA.equals(place)) return "https://prep9.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii-prep.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
			if(Administration.NAVARRA.equals(place)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
		}
		if(SIIType.BIENES_INVERSION.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place))return "https://www7.aeat.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			if(Administration.ALAVA.equals(place))return "https://pruebas-sii.araba.eus/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			if(Administration.BIZKAIA.equals(place))return "https://pruapps.bizkaia.eus/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
//			if(Administration.GIPUZKOA.equals(place))return "https://prep9.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			if(Administration.GIPUZKOA.equals(place))return "https://sii-prep.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
			if(Administration.NAVARRA.equals(place))return "https://www7.aeat.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
		}
		if(SIIType.OPERACIONES_INTRACOMUNITARIAS.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://pruapps.bizkaia.eus/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
//			if(Administration.GIPUZKOA.equals(place)) return "https://prep9.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii-prep.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
			if(Administration.NAVARRA.equals(place))return "https://www7.aeat.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
		}
		if(SIIType.COBROS_METALICO.equals(type) 
				|| SIIType.OPERACIONES_SEGUROS.equals(type)
				|| SIIType.AGENCIAS_VIAJES.equals(type)) {
			if(Administration.COMMON_TERRITORY.equals(place)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			if(Administration.ALAVA.equals(place)) return "https://pruebas-sii.araba.eus/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			if(Administration.BIZKAIA.equals(place)) return "https://pruapps.bizkaia.eus/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
//			if(Administration.GIPUZKOA.equals(place)) return "https://prep9.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			if(Administration.GIPUZKOA.equals(place)) return "https://sii-prep.egoitza.gipuzkoa.eus/JBS/HACI/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
			if(Administration.NAVARRA.equals(place))return "https://www7.aeat.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
		}
		return "";
	}
}