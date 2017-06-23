package net.aonsolutions.aon.sii;

public class SIIUri {

	public static SIIUri getInstance() {
		return new SIIUri();
	}
	
	public SIIUri() {

	}
	
	/**
	 * Entorno de PRODUCCION día 1 de julio 2017 
	 * @param SIIType type
	 * @return URI
	 */
	protected String getURI(SIIType type) {
		if(SIIType.FACTURAS_EMITIDAS.equals(type)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
		if(SIIType.FACTURAS_EMITIDAS_COBROS.equals(type)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fe/SiiFactCOBV1SOAP"; 
		if(SIIType.FACTURAS_RECIBIDAS.equals(type)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
		if(SIIType.FACTURAS_RECIBIDAS_PAGOS.equals(type)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
		if(SIIType.BIENES_INVERSION.equals(type)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
		if(SIIType.OPERACIONES_INTRACOMUNITARIAS.equals(type)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
		if(SIIType.COBROS_METALICO.equals(type) 
			|| SIIType.OPERACIONES_SEGUROS.equals(type)
			|| SIIType.AGENCIAS_VIAJES.equals(type)) return "https://www1.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
		return "";
	}
	
	/**
	 * Entorno de PRODUCCION día 1 de julio 2017 para acceso con certificado de sello
	 * @param SIIType type
	 * @return URI
	 */
	protected String getURISello(SIIType type) {
		if(SIIType.FACTURAS_EMITIDAS.equals(type)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
		if(SIIType.FACTURAS_EMITIDAS_COBROS.equals(type)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fe/SiiFactCOBV1SOAP"; 
		if(SIIType.FACTURAS_RECIBIDAS.equals(type)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
		if(SIIType.FACTURAS_RECIBIDAS_PAGOS.equals(type)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
		if(SIIType.BIENES_INVERSION.equals(type)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
		if(SIIType.OPERACIONES_INTRACOMUNITARIAS.equals(type)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
		if(SIIType.COBROS_METALICO.equals(type) 
			|| SIIType.OPERACIONES_SEGUROS.equals(type)
			|| SIIType.AGENCIAS_VIAJES.equals(type)) return "https://www10.agenciatributaria.gob.es/wlpl/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
		return "";
	}
	
	/**
	 * Entorno de PRUEBAS (tambien valido para acceso con certificados de sello)
	 * @param SIIType type
	 * @return URI
	 */
	protected String getURIPruebas(SIIType type) {
		if(SIIType.FACTURAS_EMITIDAS.equals(type)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/fe/SiiFactFEV1SOAP";
		if(SIIType.FACTURAS_EMITIDAS_COBROS.equals(type)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/fe/SiiFactCOBV1SOAP"; 
		if(SIIType.FACTURAS_RECIBIDAS.equals(type)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/fr/SiiFactFRV1SOAP";
		if(SIIType.FACTURAS_RECIBIDAS_PAGOS.equals(type)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/fr/SiiFactPAGV1SOAP";
		if(SIIType.BIENES_INVERSION.equals(type)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/bi/SiiFactBIV1SOAP";
		if(SIIType.OPERACIONES_INTRACOMUNITARIAS.equals(type)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/oi/SiiFactOIV1SOAP";
		if(SIIType.COBROS_METALICO.equals(type) 
			|| SIIType.OPERACIONES_SEGUROS.equals(type)
			|| SIIType.AGENCIAS_VIAJES.equals(type)) return "https://www7.aeat.es/wlpl/SSII-FACT/ws/pm/SiiFactCMV1SOAP";
		return "";
	}
}
