package com.esferalia.aon.occam.api.ddff.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.ddff.AeatCotizacionAutonomo;
import com.esferalia.aon.occam.api.model.ddff.AeatRegCotizacion;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class AeatCotizacionAutonomoJSON {
	
	private AeatCotizacionAutonomoJSON() {
		
	}
	
	public static AeatCotizacionAutonomo from(String text) {
		return from(new JSONObject(text));
	}
	
	public static List<AeatCotizacionAutonomo> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AeatJSONUtils.stream(array)
			.map(AeatCotizacionAutonomoJSON::from)
			.collect(Collectors.toCollection(LinkedList::new));		
	}
	
	public static AeatCotizacionAutonomo from(JSONObject json) {
		if (json == null) return null; 
		return new AeatCotizacionAutonomo()
			.setNumeroAfiliacion( AeatJSONUtils.getString(json, AeatJSONConstants.NUMERO_AFILIACION))
			.setRegCotizacion(AeatRegCotizacion.getByValue(json.optString(AeatJSONConstants.REG_COTIZACION)))
			.setImporte( AeatJSONUtils.getDouble(json, AeatJSONConstants.IMPORTE))
			;
	}
	
	public static JSONArray to(List<AeatCotizacionAutonomo> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<AeatCotizacionAutonomo> stream) {
		return stream
			.map(AeatCotizacionAutonomoJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(AeatCotizacionAutonomo data) {
		if (data == null) return null;
		return new JSONObject()
			.putOpt(AeatJSONConstants.NUMERO_AFILIACION, data.getNumeroAfiliacion())
			.putOpt(AeatJSONConstants.REG_COTIZACION, data.getRegCotizacion() != null ? data.getRegCotizacion().getCode() : null)
			.putOpt(AeatJSONConstants.IMPORTE, data.getImporte())
			;
	}
	
}