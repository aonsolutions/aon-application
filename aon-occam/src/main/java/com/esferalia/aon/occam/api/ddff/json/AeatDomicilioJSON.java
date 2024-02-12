package com.esferalia.aon.occam.api.ddff.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.ddff.AeatDomicilio;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AeatDomicilioJSON {
	
	private AeatDomicilioJSON() {
		
	}
	
	public static AeatDomicilio from(String text) {
		return from(new JSONObject(text));
	}
	
	public static List<AeatDomicilio> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AeatJSONUtils.stream(array)
			.map(AeatDomicilioJSON::from)
			.collect(Collectors.toCollection(LinkedList::new));		
	}
	
	public static AeatDomicilio from(JSONObject json) {
		if (json == null) return null; 
		return new AeatDomicilio()
			.setTipoVia( AeatJSONUtils.getString(json, AeatJSONConstants.TIPO_VIA))
			.setCodVia( AeatJSONUtils.getString(json, AeatJSONConstants.COD_VIA ))
			.setNombreLargo( AeatJSONUtils.getString(json, AeatJSONConstants.NOMBRE_LARGO ))
			.setNombreCorto( AeatJSONUtils.getString(json, AeatJSONConstants.NOMBRE_CORTO ))
			.setNumeracion( AeatJSONUtils.getString(json, AeatJSONConstants.NUMERACION ))
			.setNumero( AeatJSONUtils.getString(json, AeatJSONConstants.NUMERO ))
			.setCalificadorNumero( AeatJSONUtils.getString(json, AeatJSONConstants.CALIFICADOR_NUMERO ))
			.setBloque( AeatJSONUtils.getString(json, AeatJSONConstants.BLOQUE ))
			.setPortal( AeatJSONUtils.getString(json, AeatJSONConstants.PORTAL ))
			.setEscalera( AeatJSONUtils.getString(json, AeatJSONConstants.ESCALERA ))
			.setPlanta( AeatJSONUtils.getString(json, AeatJSONConstants.PLANTA ))	
			.setPuerta( AeatJSONUtils.getString(json, AeatJSONConstants.PUERTA ))
			.setDatosComplementarios( AeatJSONUtils.getString(json, AeatJSONConstants.DATOS_COMPLEMENTARIOS ))	
			.setPoblacion( AeatJSONUtils.getString(json, AeatJSONConstants.POBLACION ))
			.setCodigoPostal( AeatJSONUtils.getString(json, AeatJSONConstants.CODIGO_POSTAL ))
			.setCodigoMunicipio( AeatJSONUtils.getString(json, AeatJSONConstants.CODIGO_MUNICIPIO ))
			.setMunicipio( AeatJSONUtils.getString(json, AeatJSONConstants.MUNICIPIO ))
			.setCodigoProvincia( AeatJSONUtils.getString(json, AeatJSONConstants.CODIGO_PROVINCIA ))
			.setProvincia( AeatJSONUtils.getString(json, AeatJSONConstants.PROVINCIA ))
			.setReferenciaCatastral( AeatJSONUtils.getString(json, AeatJSONConstants.REFERENCIA_CATASTRAL ))
			.setFechaModif(AeatJSONUtils.getDate(json, AeatJSONConstants.FECHA_MODIF ))
			;
	}
	
	public static JSONArray to(List<AeatDomicilio> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<AeatDomicilio> stream) {
		return stream
			.map(AeatDomicilioJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(AeatDomicilio data) {
		if (data == null) return null;
		return new JSONObject()
			.putOpt(AeatJSONConstants.TIPO_VIA, data.getTipoVia())
			.putOpt(AeatJSONConstants.COD_VIA, data.getCodVia())
			.putOpt(AeatJSONConstants.NOMBRE_LARGO, data.getNombreLargo())
			.putOpt(AeatJSONConstants.NOMBRE_CORTO, data.getNombreCorto())
			.putOpt(AeatJSONConstants.NUMERACION, data.getNumeracion())
			.putOpt(AeatJSONConstants.NUMERO, data.getNumero())
			.putOpt(AeatJSONConstants.CALIFICADOR_NUMERO, data.getCalificadorNumero())
			.putOpt(AeatJSONConstants.BLOQUE, data.getBloque())
			.putOpt(AeatJSONConstants.PORTAL, data.getPortal())
			.putOpt(AeatJSONConstants.ESCALERA, data.getEscalera())
			.putOpt(AeatJSONConstants.PLANTA, data.getPlanta())
			.putOpt(AeatJSONConstants.PUERTA, data.getPuerta())
			.putOpt(AeatJSONConstants.DATOS_COMPLEMENTARIOS, data.getDatosComplementarios())	
			.putOpt(AeatJSONConstants.POBLACION, data.getPoblacion())
			.putOpt(AeatJSONConstants.CODIGO_POSTAL, data.getCodigoPostal())
			.putOpt(AeatJSONConstants.CODIGO_MUNICIPIO, data.getCodigoMunicipio())
			.putOpt(AeatJSONConstants.MUNICIPIO, data.getMunicipio())
			.putOpt(AeatJSONConstants.CODIGO_PROVINCIA, data.getCodigoProvincia())
			.putOpt(AeatJSONConstants.PROVINCIA, data.getProvincia())
			.putOpt(AeatJSONConstants.REFERENCIA_CATASTRAL, data.getReferenciaCatastral())
			.putOpt(AeatJSONConstants.FECHA_MODIF, AeatJSONUtils.formatDate(data.getFechaModif()))
			;
	}
	
}