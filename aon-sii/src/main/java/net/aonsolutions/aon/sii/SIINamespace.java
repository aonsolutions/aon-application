package net.aonsolutions.aon.sii;

import java.io.UnsupportedEncodingException;

import com.esferalia.aon.occam.api.model.type.Administration;

/**
 * Los esquemas del SII foral son copias del de la AEAT: los tipos son identicos
 * y lo unico que cambia es la URI del namespace. Por eso el XML se construye
 * siempre con el modelo JAXB de la AEAT y aqui se traduce el namespace al de la
 * administracion destino justo antes de enviarlo (y a la inversa al recibir la
 * respuesta), en lugar de mantener una copia del codigo por territorio.
 */
public class SIINamespace {

	public static final String AEAT = "https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/";
	public static final String ARABA = "https://sii.araba.eus/documentos/";
	public static final String BIZKAIA = "http://www.bizkaia.eus/ogasuna/sii/documentos/";
	public static final String GIPUZKOA = "https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/";

	private static final String ENCODING = "UTF-8";

	/**
	 * Prefijo comun de los namespaces (SuministroInformacion.xsd,
	 * SuministroLR.xsd, RespuestaSuministro.xsd, ...) de cada administracion.
	 */
	public static String getBase(Administration place) {
		if(Administration.ALAVA.equals(place)) return ARABA;
		else if(Administration.BIZKAIA.equals(place)) return BIZKAIA;
		else if(Administration.GIPUZKOA.equals(place)) return GIPUZKOA;
		else return AEAT;
	}

	/** Traduce el XML generado con el modelo de la AEAT al namespace de la administracion. */
	public static String toAdministration(String xml, Administration place) {
		String base = getBase(place);
		if(xml == null || AEAT.equals(base)) return xml;
		return xml.replace(AEAT, base);
	}

	/** Traduce la respuesta de la administracion al namespace de la AEAT, que es el que entiende JAXB. */
	public static String toAeat(String xml, Administration place) {
		String base = getBase(place);
		if(xml == null || AEAT.equals(base)) return xml;
		return xml.replace(base, AEAT);
	}

	/** Idem, sobre el XML que se archiva en la tabla de suministros. */
	public static byte[] toAdministration(byte[] xml, Administration place) {
		String base = getBase(place);
		if(xml == null || AEAT.equals(base)) return xml;
		try {
			return toAdministration(new String(xml, ENCODING), place).getBytes(ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return xml;
		}
	}
}
