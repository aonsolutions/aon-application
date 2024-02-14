package com.esferalia.aon.occam.api.ddff.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.ddff.AeatFiscalData;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class AeatFiscalDataJSON {
	
	private AeatFiscalDataJSON() {
		
	}
	
	public static AeatFiscalData from(String text) {
		return from(new JSONObject(text));
	}
	
	public static List<AeatFiscalData> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AeatJSONUtils.stream(array)
			.map(AeatFiscalDataJSON::from)
			.collect(Collectors.toCollection(LinkedList::new));		
	}
	
	public static AeatFiscalData from(JSONObject json) {
		if (json == null) return null; 
		return new AeatFiscalData()
			.setDate(AeatJSONUtils.getDate(json, AeatJSONConstants.DATE ))
			.setDatosGenerales( AeatDatosGeneralesJSON.from(json.optJSONArray( AeatJSONConstants.DATOS_GENERALES)))
			.setTitulares( AeatTitularJSON.from(json.optJSONArray( AeatJSONConstants.TITULARES)))
			.setDomicilios( AeatDomicilioJSON.from(json.optJSONArray( AeatJSONConstants.DOMICILIOS)))
			.setCotizacionesAutonomo( AeatCotizacionAutonomoJSON.from(json.optJSONArray( AeatJSONConstants.COTIZACIONES_AUTONOMO)))
			;
	}
	
	public static JSONArray to(List<AeatFiscalData> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<AeatFiscalData> stream) {
		return stream
			.map(AeatFiscalDataJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(AeatFiscalData data) {
		if (data == null) return null;
		return new JSONObject()
			.putOpt(AeatJSONConstants.DATE, AeatJSONUtils.formatDate(data.getDate()))
			.putOpt(AeatJSONConstants.DATOS_GENERALES, AeatDatosGeneralesJSON.to(data.getDatosGenerales()))
			.putOpt(AeatJSONConstants.TITULARES, AeatTitularJSON.to(data.getTitulares()))
			.putOpt(AeatJSONConstants.DOMICILIOS, AeatDomicilioJSON.to(data.getDomicilios()))
			.putOpt(AeatJSONConstants.COTIZACIONES_AUTONOMO, AeatCotizacionAutonomoJSON.to(data.getCotizacionesAutonomo()))
			;
	}
	
}