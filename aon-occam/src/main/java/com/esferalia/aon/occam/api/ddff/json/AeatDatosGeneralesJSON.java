package com.esferalia.aon.occam.api.ddff.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.ddff.AeatDatosGenerales;
import com.esferalia.aon.occam.api.model.ddff.AeatEstadoCivil;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class AeatDatosGeneralesJSON {
	
	private AeatDatosGeneralesJSON() {
		
	}
	
	public static AeatDatosGenerales from(String text) {
		return from(new JSONObject(text));
	}
	
	public static List<AeatDatosGenerales> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AeatJSONUtils.stream(array)
			.map(AeatDatosGeneralesJSON::from)
			.collect(Collectors.toCollection(LinkedList::new));		
	}
	
	public static AeatDatosGenerales from(JSONObject json) {
		if (json == null) return null; 
		return new AeatDatosGenerales()
			.setEstadoCivil(AeatEstadoCivil.getByValue(json.optString(AeatJSONConstants.ESTADO_CIVIL)))
			.setConyugeNoResidente( AeatJSONUtils.getBoolean(json, AeatJSONConstants.CONYUGE_NO_RESIDENTE))
			.setConyugeNoResidenteUE( AeatJSONUtils.getBoolean(json, AeatJSONConstants.CONYUGE_NO_RESIDENTE_UE))
			;
	}
	
	public static JSONArray to(List<AeatDatosGenerales> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<AeatDatosGenerales> stream) {
		return stream
			.map(AeatDatosGeneralesJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(AeatDatosGenerales data) {
		if (data == null) return null;
		return new JSONObject()
			.putOpt(AeatJSONConstants.ESTADO_CIVIL, data.getEstadoCivil() != null ? data.getEstadoCivil().getCode() : null)
			.putOpt(AeatJSONConstants.CONYUGE_NO_RESIDENTE, data.isConyugeNoResidente() )
			.putOpt(AeatJSONConstants.CONYUGE_NO_RESIDENTE_UE, data.isConyugeNoResidenteUE() )
			;
	}
	
}